package com.cms.controller;

import com.cms.common.Result;
import com.cms.entity.Permission;
import com.cms.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/tree")
    public Result<List<Permission>> getPermissionTree() {
        List<Permission> permissionTree = permissionService.getPermissionTree();
        return Result.success(permissionTree);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Permission>> getPermissionsByUserId(@PathVariable Long userId) {
        List<Permission> permissions = permissionService.getPermissionsByUserId(userId);
        return Result.success(permissions);
    }

    @GetMapping("/role/{roleId}")
    public Result<List<Permission>> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<Permission> permissions = permissionService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }

    @GetMapping("/codes/{userId}")
    public Result<List<String>> getPermissionCodesByUserId(@PathVariable Long userId) {
        List<String> codes = permissionService.getPermissionCodesByUserId(userId);
        return Result.success(codes);
    }

    @GetMapping("/check")
    public Result<Boolean> checkPermission(@RequestParam Long userId, @RequestParam String permissionCode) {
        boolean hasPermission = permissionService.hasPermission(userId, permissionCode);
        return Result.success(hasPermission);
    }

    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        return Result.success(permission);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Permission permission) {
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        boolean result = permissionService.save(permission);
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Permission permission) {
        permission.setUpdateTime(LocalDateTime.now());
        boolean result = permissionService.updateById(permission);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = permissionService.removeById(id);
        return Result.success(result);
    }

    @PostMapping("/assign")
    public Result<Boolean> assignPermissions(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> permissionIds = (List<Long>) params.get("permissionIds");
        boolean result = permissionService.assignPermissionsToRole(roleId, permissionIds);
        return Result.success(result);
    }
}
