package com.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cms.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    List<Menu> getMenuTree();
    List<Menu> getMenusByUserId(Long userId);
    List<Menu> getMenusByRoleId(Long roleId);
    boolean assignMenusToRole(Long roleId, List<Long> menuIds);
}
