package com.jmall.admin.controller;

import com.jmall.admin.entity.*;
import com.jmall.admin.service.*;
import com.jmall.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 平台管理核心接口。 */
@Tag(name="平台管理")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminUserService adminUserService;
    private final AdminRoleService adminRoleService;
    private final AdminPermissionService adminPermissionService;
    private final HomeBannerService homeBannerService;
    private final SysConfigService sysConfigService;
    private final DailyReportService dailyReportService;

    @Operation(summary="管理员列表")
    @GetMapping("/users")
    public Result<List<AdminUser>> users(){ return Result.success(adminUserService.list()); }
    @Operation(summary="保存管理员")
    @PostMapping("/users")
    public Result<AdminUser> saveUser(@RequestBody AdminUser x){ return Result.success(adminUserService.save(x)); }
    @Operation(summary="角色列表")
    @GetMapping("/roles")
    public Result<List<AdminRole>> roles(){ return Result.success(adminRoleService.list()); }
    @Operation(summary="保存角色")
    @PostMapping("/roles")
    public Result<AdminRole> saveRole(@RequestBody AdminRole x){ return Result.success(adminRoleService.save(x)); }
    @Operation(summary="权限列表")
    @GetMapping("/permissions")
    public Result<List<AdminPermission>> permissions(){ return Result.success(adminPermissionService.list()); }
    @Operation(summary="保存权限")
    @PostMapping("/permissions")
    public Result<AdminPermission> savePermission(@RequestBody AdminPermission x){ return Result.success(adminPermissionService.save(x)); }
    @Operation(summary="首页轮播列表")
    @GetMapping("/banners")
    public Result<List<HomeBanner>> banners(){ return Result.success(homeBannerService.list()); }
    @Operation(summary="保存首页轮播")
    @PostMapping("/banners")
    public Result<HomeBanner> saveBanner(@RequestBody HomeBanner x){ return Result.success(homeBannerService.save(x)); }
    @Operation(summary="删除首页轮播")
    @DeleteMapping("/banners/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id){ homeBannerService.delete(id); return Result.success(); }
    @Operation(summary="系统配置列表")
    @GetMapping("/configs")
    public Result<List<SysConfig>> configs(){ return Result.success(sysConfigService.list()); }
    @Operation(summary="保存系统配置")
    @PostMapping("/configs")
    public Result<SysConfig> saveConfig(@RequestBody SysConfig x){ return Result.success(sysConfigService.save(x)); }
    @Operation(summary="每日数据报表")
    @GetMapping("/reports/daily")
    public Result<List<DailyReport>> reports(){ return Result.success(dailyReportService.list()); }
}
