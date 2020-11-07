package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Role;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface RoleDao {
     Set<Role> findByUserId(Integer userId);

     List<Role> findAll();


      void addRole(Role role);

      void setRelation(HashMap<String, Integer> map);

      void setRelationMenu(HashMap<String, Integer> map);

    Role findRoleById(Integer id);

    void EditRole(Role role);


    Page<Role> findPage(String queryString);

    void deleteRoleAndPermission(Integer roleId);

    void deleteRoleAndMenu(Integer roleId);

    void deleteById(Integer id);
}
