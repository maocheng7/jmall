package com.jmall.admin.service;
import com.jmall.admin.entity.*; import java.util.List;
public interface AdminPermissionService { List<AdminPermission> list(); AdminPermission save(AdminPermission x); }
