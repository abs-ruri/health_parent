package com.itheima.dao;


import com.github.pagehelper.Page;
import com.itheima.pojo.Menu;

import java.util.List;
import java.util.Set;

public interface MenuDao {

    public List<Menu> findAllmenu();

    List<Integer> findMenuById(Integer id);

    List<Menu> findMenuIdList(List<Integer> menuId);

    public Page<Menu> findByCondition(String queryString);
    public List<Menu> findByParentMenuId(Integer id);
    public List<Menu> findParentMenu();
    public void add(Menu menu);
    public Menu findById(Integer id);
    public void edit(Menu menu);
    public long findRoleIdByMenuId(Integer id);
    public  void deleteRoleAndMenu(Integer id);
    public void delete(Integer id);



    Set<Menu> findByRoleId(Integer roleId);
}
