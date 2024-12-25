package com.supermall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.common.utils.FileUtils;
import com.supermall.backend.dto.CommentDTO;
import com.supermall.backend.entity.Comment;
import com.supermall.backend.entity.Product;
import com.supermall.backend.entity.User;
import com.supermall.backend.repository.CommentRepository;
import com.supermall.backend.repository.ProductRepository;
import com.supermall.backend.repository.UserRepository;
import com.supermall.backend.service.CommentService;
import com.supermall.backend.vo.CommentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CommentVO createComment(CommentDTO commentDTO, Long userId) {
        // 检查商品是否存在
        Product product = productRepository.selectById(commentDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        comment.setUserId(userId);
        
        // 处理图片
        if (commentDTO.getImages() != null && !commentDTO.getImages().isEmpty()) {
            comment.setImages(String.join(",", commentDTO.getImages()));
        }

        commentRepository.insert(comment);
        return getComment(comment.getId());
    }

    @Override
    @Transactional
    public CommentVO updateComment(Long id, CommentDTO commentDTO, Long userId) {
        Comment comment = commentRepository.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权修改他人评论");
        }

        BeanUtils.copyProperties(commentDTO, comment);
        
        // 处理图片
        if (commentDTO.getImages() != null && !commentDTO.getImages().isEmpty()) {
            comment.setImages(String.join(",", commentDTO.getImages()));
        }

        commentRepository.updateById(comment);
        return getComment(id);
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long userId) {
        Comment comment = commentRepository.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除他人评论");
        }

        commentRepository.deleteById(id);
    }

    @Override
    public CommentVO getComment(Long id) {
        Comment comment = commentRepository.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        return convertToVO(comment);
    }

    @Override
    public IPage<CommentVO> getProductComments(Long productId, Integer pageNum, Integer pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getProductId, productId)
                   .orderByDesc(Comment::getCreateTime);
        
        IPage<Comment> commentPage = commentRepository.selectPage(page, queryWrapper);
        
        return commentPage.convert(this::convertToVO);
    }

    @Override
    public IPage<CommentVO> getUserComments(Long userId, Integer pageNum, Integer pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getUserId, userId)
                   .orderByDesc(Comment::getCreateTime);
        
        IPage<Comment> commentPage = commentRepository.selectPage(page, queryWrapper);
        
        return commentPage.convert(this::convertToVO);
    }

    @Override
    public List<String> uploadCommentImages(List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            imageUrls.add(FileUtils.uploadFile(file, uploadPath, "comments"));
        }
        return imageUrls;
    }

    private CommentVO convertToVO(Comment comment) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        
        // 设置用户信息
        User user = userRepository.selectById(comment.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setUserAvatar(user.getAvatar());
        }
        
        // 设置商品信息
        Product product = productRepository.selectById(comment.getProductId());
        if (product != null) {
            vo.setProductName(product.getName());
        }
        
        // 处理图片
        if (StringUtils.hasText(comment.getImages())) {
            vo.setImages(List.of(comment.getImages().split(",")));
        } else {
            vo.setImages(new ArrayList<>());
        }
        
        return vo;
    }
} 