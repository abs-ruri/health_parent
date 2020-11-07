package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Menu;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);


    public PageResult findPage(QueryPageBean queryPageBean);
    public void add(Integer[] roleIds,User user);
    public User findById(Integer id);
    public Integer findRoleIdsByUserId(Integer id);
    public void edit(Integer[] roleIds,User user);
    public void delete(Integer id);


    public List<Menu> findMenuByUser(String username);

}
