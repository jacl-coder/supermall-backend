package com.supermall.backend.domain.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT id, username, password, avatar, phone, email, status, role, " +
           "created_time, updated_time, deleted " +
           "FROM user " +
           "WHERE deleted = 0 AND username = #{username}")
    User selectByUsername(@Param("username") String username);
    
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
} 