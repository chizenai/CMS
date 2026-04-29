package com.cms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.Permission;
import com.cms.entity.RolePermission;
import com.cms.entity.User;
import com.cms.mapper.PermissionMapper;
import com.cms.mapper.RolePermissionMapper;
import com.cms.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserMapper userMapper;

    public PermissionService(PermissionMapper permissionMapper,
                              RolePermissionMapper rolePermissionMapper,
                              UserMapper userMapper) {
        this.permissionMapper = permissionMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.userMapper = userMapper;
    }

    public Page<Permission> getPermissionPage(Integer pageNum, Integer pageSize, String permissionName) {
        Page<Permission> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        if (permissionName != null && !permissionName.isEmpty()) {
            wrapper.like(Permission::getPermissionName, permissionName);
        }
        wrapper.orderByAsc(Permission::getParentId).orderByAsc(Permission::getSort);
        return permissionMapper.selectPage(page, wrapper);
    }

    public List<Permission> getPermissionTree() {
        List<Permission> allPermissions = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getStatus, 1)
                        .orderByAsc(Permission::getSort)
        );
        
        Map<Long, List<Permission>> parentMap = allPermissions.stream()
                .collect(Collectors.groupingBy(Permission::getParentId));
        
        List<Permission> rootPermissions = parentMap.getOrDefault(0L, new ArrayList<>());
        
        for (Permission permission : rootPermissions) {
            buildChildren(permission, parentMap);
        }
        
        return rootPermissions;
    }

    private void buildChildren(Permission parent, Map<Long, List<Permission>> parentMap) {
        List<Permission> children = parentMap.getOrDefault(parent.getId(), new ArrayList<>());
        parent.setChildren(children);
        for (Permission child : children) {
            buildChildren(child, parentMap);
        }
    }

    public Permission getById(Long id) {
        return permissionMapper.selectById(id);
    }

    public boolean save(Permission permission) {
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        return permissionMapper.insert(permission) > 0;
    }

    public boolean update(Permission permission) {
        permission.setUpdateTime(LocalDateTime.now());
        return permissionMapper.updateById(permission) > 0;
    }

    @Transactional
    public boolean delete(Long id) {
        LambdaQueryWrapper<Permission> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Permission::getParentId, id);
        Long childCount = permissionMapper.selectCount(childWrapper);
        if (childCount > 0) {
            return false;
        }
        
        LambdaQueryWrapper<RolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(RolePermission::getPermissionId, id);
        rolePermissionMapper.delete(rpWrapper);
        
        return permissionMapper.deleteById(id) > 0;
    }

    public List<Permission> getPermissionsByRoleId(Long roleId) {
        LambdaQueryWrapper<RolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(rpWrapper);
        
        if (rolePermissions.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
        
        return permissionMapper.selectBatchIds(permissionIds);
    }

    public List<String> getPermissionCodesByRoleId(Long roleId) {
        List<Permission> permissions = getPermissionsByRoleId(roleId);
        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());
    }

    public List<String> getPermissionCodesByUserId(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getRoleId() == null) {
            return new ArrayList<>();
        }
        return getPermissionCodesByRoleId(user.getRoleId());
    }

    public List<Permission> getMenusByUserId(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getRoleId() == null) {
            return new ArrayList<>();
        }
        
        List<Permission> permissions = getPermissionsByRoleId(user.getRoleId());
        
        List<Permission> menuPermissions = permissions.stream()
                .filter(p -> p.getPermissionType() == 1 && p.getStatus() == 1)
                .sorted((p1, p2) -> {
                    int sortCompare = Integer.compare(p1.getSort(), p2.getSort());
                    if (sortCompare != 0) return sortCompare;
                    return Long.compare(p1.getId(), p2.getId());
                })
                .collect(Collectors.toList());
        
        Map<Long, List<Permission>> parentMap = menuPermissions.stream()
                .collect(Collectors.groupingBy(Permission::getParentId));
        
        List<Permission> rootMenus = parentMap.getOrDefault(0L, new ArrayList<>());
        
        for (Permission menu : rootMenus) {
            buildChildren(menu, parentMap);
        }
        
        return rootMenus;
    }

    @Transactional
    public boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permissionId);
                rp.setCreateTime(LocalDateTime.now());
                rolePermissionMapper.insert(rp);
            }
        }
        
        return true;
    }
}
