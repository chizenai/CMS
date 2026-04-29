package com.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cms.entity.Permission;
import com.cms.mapper.PermissionMapper;
import com.cms.mapper.RolePermissionMapper;
import com.cms.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final RolePermissionMapper rolePermissionMapper;

    public PermissionServiceImpl(RolePermissionMapper rolePermissionMapper) {
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public List<Permission> getPermissionTree() {
        List<Permission> allPermissions = this.list(new LambdaQueryWrapper<Permission>()
                .orderByAsc(Permission::getSort)
                .orderByAsc(Permission::getId));
        return buildTree(allPermissions, 0L);
    }

    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return baseMapper.selectByRoleId(roleId);
    }

    @Override
    public List<String> getPermissionCodesByUserId(Long userId) {
        return baseMapper.selectPermissionCodesByUserId(userId);
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        List<String> permissions = getPermissionCodesByUserId(userId);
        return permissions.contains(permissionCode);
    }

    @Override
    @Transactional
    public boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.batchInsert(roleId, permissionIds);
        }
        return true;
    }

    private List<Permission> buildTree(List<Permission> permissions, Long parentId) {
        List<Permission> tree = new ArrayList<>();
        for (Permission permission : permissions) {
            if (parentId.equals(permission.getParentId())) {
                permission.setChildren(buildTree(permissions, permission.getId()));
                tree.add(permission);
            }
        }
        return tree;
    }
}
