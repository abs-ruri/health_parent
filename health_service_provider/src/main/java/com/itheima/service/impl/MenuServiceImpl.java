package com.itheima.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.MenuDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Menu;
import com.itheima.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceClass = MenuService.class)
@Transactional
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuDao menuDao;

    public List<Menu> findAllmenu(){
        List<Menu> allmenu = menuDao.findAllmenu();
        return allmenu;
    }

    @Override
    public List<Integer> findMenuById(Integer id) {

       List<Integer> menuList =  menuDao.findMenuById(id);
       return menuList;
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {

        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //分页查询
        PageHelper.startPage(currentPage, pageSize);
        Page<Menu> page = menuDao.findByCondition(queryString);
        for (Menu menu : page.getResult()) {
            Integer id = menu.getId();
            //根据id去查询子节点
            List<Menu> list = menuDao.findByParentMenuId(id);
            menu.setChildren(list);
        }

        //封装数据并返回
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 查询父菜单
     *
     * @return
     */
    @Override
    public List<Menu> findParentMenu() {
        List<Menu> list = menuDao.findParentMenu();
        return list;
    }

    /**
     * 新增
     *
     * @param menu
     */
    @Override
    public void add(Menu menu) {
        menuDao.add(menu);
    }


    /**
     * 根据id查询菜单
     * @param id
     * @return
     */
    @Override
    public Menu findById(Integer id) {
        Menu menu = menuDao.findById(id);
        return menu;
    }

    /**
     * 编辑
     * @param menu
     */
    @Override
    public void edit(Menu menu) {
        menuDao.edit(menu);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Integer id) {
        //判断t_role_menu表是否引用了待删除的菜单编号
        long count = menuDao.findRoleIdByMenuId(id);
        if (count > 0 ){
            //此时有关联关系不能删除
            throw new RuntimeException("当前菜单被引用，不能删除!");
        }
        //先删除中间关联表
        menuDao.deleteRoleAndMenu(id);
        //根据id删除菜单
        menuDao.delete(id);
    }

}
