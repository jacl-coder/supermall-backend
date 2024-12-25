package com.supermall.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermall.backend.common.response.Result;
import com.supermall.backend.dto.CommentDTO;
import com.supermall.backend.service.CommentService;
import com.supermall.backend.vo.CommentVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Result<CommentVO> createComment(
            @Valid @RequestBody CommentDTO commentDTO) {
        Long userId = 1L;
        return Result.success(commentService.createComment(commentDTO, userId));
    }

    @GetMapping("/product/{productId}")
    public Result<IPage<CommentVO>> getProductComments(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("Getting comments for product: {}, page: {}, size: {}", productId, pageNum, pageSize);
        return Result.success(commentService.getProductComments(productId, pageNum, pageSize));
    }

    @GetMapping("/users/{userId}")
    public Result<IPage<CommentVO>> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(commentService.getUserComments(userId, pageNum, pageSize));
    }

    @PostMapping("/images")
    public Result<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        return Result.success(commentService.uploadCommentImages(files));
    }
}