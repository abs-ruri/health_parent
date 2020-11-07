package com.itheima.service;


import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Menu;

import java.util.List;

public interface MenuService {

    public List<Menu> findAllmenu();

    List<Integer> findMenuById(Integer id);

    public PageResult findPage(QueryPageBean queryPageBean);
    public List<Menu> findParentMenu();
    public void add(Menu menu);
    public Menu findById(Integer id);
    public void edit(Menu menu);
    public void delete(Integer id);
}
