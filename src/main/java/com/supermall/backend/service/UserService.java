package com.supermall.backend.service;

import com.supermall.backend.dto.UserLoginDTO;
import com.supermall.backend.dto.UserRegisterDTO;
import com.supermall.backend.entity.User;
import com.supermall.backend.vo.UserLoginVO;
import com.supermall.backend.dto.UserUpdateDTO;
import com.supermall.backend.dto.PasswordUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserLoginVO register(UserRegisterDTO userDTO);
    UserLoginVO login(UserLoginDTO loginDTO);
    boolean checkUsernameExists(String username);
    boolean checkEmailExists(String email);
    boolean checkPhoneExists(String phone);
    User getUserByUsername(String username);
    User updateProfile(String username, UserUpdateDTO updateDTO);
    void updatePassword(String username, PasswordUpdateDTO passwordDTO);
    String updateAvatar(String username, MultipartFile file);
} 