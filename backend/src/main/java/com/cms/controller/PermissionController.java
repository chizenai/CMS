package com.cms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.Result;
import com.cms.entity.Permission;
import com.cms.service.PermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/page")
    public Result<Page<Permission>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String permissionName) {
        Page<Permission> result = permissionService.getPermissionPage(pageNum, pageSize, permissionName);
        return Result.success(result);
    }

    @GetMapping("/tree")
    public Result<List<Permission>> tree() {
        List<Permission> result = permissionService.getPermissionTree();
        return Result.success(result);
    }

    @GetMapping("/menus")
    public Result<List<Permission>> getMenus(Authentication authentication) {
        String username = authentication.getName();
        Long userId = 1L;
        List<Permission> menus = permissionService.getMenusByUserId(userId);
        return Result.success(menus);
    }

    @GetMapping("/codes")
    public Result<List<String>> getPermissionCodes(Authentication authentication) {
        String username = authentication.getName();
        Long userId = 1L;
        List<String> codes = permissionService.getPermissionCodesByUserId(userId);
        return Result.success(codes);
    }

    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        return Result.success(permission);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Permission permission) {
        boolean result = permissionService.save(permission);
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Permission permission) {
        boolean result = permissionService.update(permission);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = permissionService.delete(id);
        return Result.success(result);
    }

    @GetMapping("/role/{roleId}")
    public Result<List<Permission>> getByRoleId(@PathVariable Long roleId) {
        List<Permission> permissions = permissionService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }

    @PostMapping("/role/assign")
    public Result<Boolean> assignPermissions(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> permissionIds = (List<Long>) params.get("permissionIds");
        boolean result = permissionService.assignPermissionsToRole(roleId, permissionIds);
        return Result.success(result);
    }
}
