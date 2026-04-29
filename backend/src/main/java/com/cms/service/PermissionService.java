package com.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cms.entity.Permission;

import java.util.List;

public interface PermissionService extends IService<Permission> {
    List<Permission> getPermissionTree();
    List<Permission> getPermissionsByUserId(Long userId);
    List<Permission> getPermissionsByRoleId(Long roleId);
    List<String> getPermissionCodesByUserId(Long userId);
    boolean hasPermission(Long userId, String permissionCode);
    boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds);
}
