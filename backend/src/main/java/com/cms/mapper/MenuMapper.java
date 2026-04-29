package com.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    
    @Select("SELECT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id = #{roleId} " +
            "ORDER BY m.sort ASC")
    List<Menu> selectByRoleId(Long roleId);
    
    @Select("SELECT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.status = 1 " +
            "ORDER BY m.sort ASC")
    List<Menu> selectByUserId(Long userId);
}
