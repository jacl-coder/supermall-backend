package com.supermall.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermall.backend.dto.CommentDTO;
import com.supermall.backend.vo.CommentVO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CommentService {
    // 创建评论
    CommentVO createComment(CommentDTO commentDTO, Long userId);
    
    // 更新评论
    CommentVO updateComment(Long id, CommentDTO commentDTO, Long userId);
    
    // 删除评论
    void deleteComment(Long id, Long userId);
    
    // 获取评论详情
    CommentVO getComment(Long id);
    
    // 获取商品评论列表
    IPage<CommentVO> getProductComments(Long productId, Integer pageNum, Integer pageSize);
    
    // 获取用户评论列表
    IPage<CommentVO> getUserComments(Long userId, Integer pageNum, Integer pageSize);
    
    // 上传评论图片
    List<String> uploadCommentImages(List<MultipartFile> files);
    
    // 添加订单评论相关方法
    CommentVO createOrderComment(Long userId, Long orderId, Long productId, CommentDTO commentDTO);
    boolean canComment(Long userId, Long orderId, Long productId);
} 