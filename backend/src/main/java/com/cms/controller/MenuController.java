package com.cms.controller;

import com.cms.common.Result;
import com.cms.entity.Menu;
import com.cms.service.MenuService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/tree")
    public Result<List<Menu>> getMenuTree() {
        List<Menu> menuTree = menuService.getMenuTree();
        return Result.success(menuTree);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Menu>> getMenusByUserId(@PathVariable Long userId) {
        List<Menu> menus = menuService.getMenusByUserId(userId);
        return Result.success(menus);
    }

    @GetMapping("/role/{roleId}")
    public Result<List<Menu>> getMenusByRoleId(@PathVariable Long roleId) {
        List<Menu> menus = menuService.getMenusByRoleId(roleId);
        return Result.success(menus);
    }

    @GetMapping("/{id}")
    public Result<Menu> getById(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        return Result.success(menu);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Menu menu) {
        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(LocalDateTime.now());
        boolean result = menuService.save(menu);
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Menu menu) {
        menu.setUpdateTime(LocalDateTime.now());
        boolean result = menuService.updateById(menu);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = menuService.removeById(id);
        return Result.success(result);
    }

    @PostMapping("/assign")
    public Result<Boolean> assignMenus(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> menuIds = (List<Long>) params.get("menuIds");
        boolean result = menuService.assignMenusToRole(roleId, menuIds);
        return Result.success(result);
    }
}
