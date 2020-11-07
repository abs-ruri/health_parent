package com.itheima.service;


import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Permission;

import java.util.List;

public interface PermissionService {

    public List<Permission> findAllpermission();

    List<Integer> findPermissionById(Integer id);

    PageResult findPage(QueryPageBean queryPageBean);

    void add(Permission permission);

    Permission findAll(Integer id);

    void edit(Permission permission);

    void delete(Integer id);
}
