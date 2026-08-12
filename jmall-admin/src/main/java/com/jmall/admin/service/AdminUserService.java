package com.jmall.admin.service;
import com.jmall.admin.entity.*; import java.util.List;
public interface AdminUserService { List<AdminUser> list(); AdminUser save(AdminUser x); }
