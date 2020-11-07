package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Menu;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserDao {
     User findByUsername(@Param("username") String username);

     Page<User> findByCondition(String queryString);
     void add(User user);
     void setUserAndRole(Map map);
     User findById(Integer id);
     Integer findRoleIdsByUserId(Integer id);
     void edit(User user);
     void deleteRoleIdByUserId(Integer userId);
     void delete(Integer userId);
    Integer findUserId(String username);
    List<Role> findUserRoleById(Integer id);
    List<Integer> findMenuIdByRoleId(Integer roleId);
    Menu findMenuIdList(Integer integer);
}
