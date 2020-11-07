package com.itheima.service;


import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Role;

import java.util.List;

public interface RoleService {

    List<Role> findAll();

    void addRole(Role role, Integer[] permissionIds, Integer[] menuIds);

    Role findRoleById(Integer id);

    void EditRole(Role role);

    void EditRoleAndPermission(Integer roleId, Integer[] permissionIds);

    void EditRoleAndMenu(Integer roleId, Integer[] menuIds);

    PageResult findPage(QueryPageBean queryPageBean);

    void deleteById(Integer id);
}
