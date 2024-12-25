package com.supermall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.common.utils.JwtUtil;
import com.supermall.backend.dto.UserLoginDTO;
import com.supermall.backend.dto.UserRegisterDTO;
import com.supermall.backend.dto.UserUpdateDTO;
import com.supermall.backend.dto.PasswordUpdateDTO;
import com.supermall.backend.entity.User;
import com.supermall.backend.repository.UserRepository;
import com.supermall.backend.service.UserService;
import com.supermall.backend.vo.UserLoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    public UserServiceImpl(UserRepository userRepository, 
                         PasswordEncoder passwordEncoder,
                         JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public UserLoginVO register(UserRegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (checkUsernameExists(registerDTO.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (checkEmailExists(registerDTO.getEmail())) {
            throw new BusinessException("邮箱已被使用");
        }
        
        // 检查手机号是否已存在
        if (checkPhoneExists(registerDTO.getPhone())) {
            throw new BusinessException("手机号已被使用");
        }

        // 创建新用户
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        
        // 设置用户状态为正常
        user.setStatus(1);
        
        // 保存用户
        userRepository.insert(user);
        
        // 转换为VO并返回
        UserLoginVO vo = new UserLoginVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public UserLoginVO login(UserLoginDTO loginDTO) {
        // 查询用户
        User user = userRepository.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginDTO.getUsername())
        );

        // 验证用户存在性和密码
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建返回对象
        return UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .token(token)
                .build();
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userRepository.selectCount(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        ) > 0;
    }

    @Override
    public boolean checkEmailExists(String email) {
        return userRepository.selectCount(
            new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
        ) > 0;
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        return userRepository.selectCount(
            new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
        ) > 0;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    @Transactional
    public User updateProfile(String username, UserUpdateDTO updateDTO) {
        // 获取当前用户
        User user = getUserByUsername(username);
        
        // 检查邮箱是否被其他用户使用
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (checkEmailExists(updateDTO.getEmail())) {
                throw new BusinessException("邮箱已被使用");
            }
            user.setEmail(updateDTO.getEmail());
        }
        
        // 检查手机号是否被其他用户使用
        if (updateDTO.getPhone() != null && !updateDTO.getPhone().equals(user.getPhone())) {
            if (checkPhoneExists(updateDTO.getPhone())) {
                throw new BusinessException("手机号已被使用");
            }
            user.setPhone(updateDTO.getPhone());
        }
        
        // 更新用户信息
        userRepository.updateById(user);
        
        // 清空密码后返回
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public void updatePassword(String username, PasswordUpdateDTO passwordDTO) {
        // 从 username 中获取用户ID（因为 username 实际上是用户ID）
        Long userId = Long.valueOf(username);
        User user = userRepository.selectById(userId);
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 验证新密码和确认密码是否一致
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new BusinessException("新密码和确认密码不一致");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userRepository.updateById(user);
    }

    @Override
    @Transactional
    public String updateAvatar(String username, MultipartFile file) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的头像文件");
        }

        // 检查文件大小（例如最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("头像文件大小不能超过5MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }

        // 从 username 中获取用户ID（因为 username 实际上是用户ID）
        Long userId = Long.valueOf(username);
        User user = userRepository.selectById(userId);
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        try {
            // 获取项目根目录
            String projectPath = System.getProperty("user.dir");
            File uploadDir = new File(projectPath, uploadPath);
            
            // 创建上传目���
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    throw new BusinessException("创建上传目录失败");
                }
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            } else {
                // 如果没有扩展名，根据文件类型设置默认扩展名
                switch (contentType) {
                    case "image/jpeg":
                        extension = ".jpg";
                        break;
                    case "image/png":
                        extension = ".png";
                        break;
                    case "image/gif":
                        extension = ".gif";
                        break;
                    default:
                        extension = ".jpg";  // 默认扩展名
                }
            }
            
            // 生成唯一文件名
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            File destFile = new File(uploadDir, filename);
            file.transferTo(destFile);
            
            // 更新用户头像URL
            String avatarUrl = "/uploads/" + filename;
            user.setAvatar(avatarUrl);
            userRepository.updateById(user);
            
            log.debug("File uploaded successfully. Path: {}", destFile.getAbsolutePath());
            return avatarUrl;
            
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new BusinessException("上传头像失败: " + e.getMessage());
        }
    }
} 