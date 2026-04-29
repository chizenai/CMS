package com.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cms.entity.Menu;
import com.cms.mapper.MenuMapper;
import com.cms.mapper.RoleMenuMapper;
import com.cms.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private final RoleMenuMapper roleMenuMapper;

    public MenuServiceImpl(RoleMenuMapper roleMenuMapper) {
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public List<Menu> getMenuTree() {
        List<Menu> allMenus = this.list(new LambdaQueryWrapper<Menu>()
                .orderByAsc(Menu::getSort)
                .orderByAsc(Menu::getId));
        return buildTree(allMenus, 0L);
    }

    @Override
    public List<Menu> getMenusByUserId(Long userId) {
        List<Menu> menus = baseMapper.selectByUserId(userId);
        return buildTree(menus, 0L);
    }

    @Override
    public List<Menu> getMenusByRoleId(Long roleId) {
        return baseMapper.selectByRoleId(roleId);
    }

    @Override
    @Transactional
    public boolean assignMenusToRole(Long roleId, List<Long> menuIds) {
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            roleMenuMapper.batchInsert(roleId, menuIds);
        }
        return true;
    }

    private List<Menu> buildTree(List<Menu> menus, Long parentId) {
        List<Menu> tree = new ArrayList<>();
        for (Menu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                menu.setChildren(buildTree(menus, menu.getId()));
                tree.add(menu);
            }
        }
        return tree;
    }
}
