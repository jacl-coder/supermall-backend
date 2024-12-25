package com.supermall.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRepository extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0")
    User findByUsername(String username);
}