package com.supermall.backend.domain.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.domain.role.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    List<Role> selectByUserId(@Param("userId") Long userId);
} 