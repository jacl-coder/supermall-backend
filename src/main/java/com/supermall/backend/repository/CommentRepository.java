package com.supermall.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentRepository extends BaseMapper<Comment> {
} 