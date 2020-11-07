package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionDao {
    Set<Permission> findByRoleId(Integer roleId);


    List<Permission> findAllpermission();

    List<Integer> findPermissionById(Integer id);

    Page<Permission> findPage(String queryString);

    void add(Permission permission);

    Permission findAll(Integer id);

    void edit(Permission permission);

    void delete(Integer id);
}
