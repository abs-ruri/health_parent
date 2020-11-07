package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.MenuDao;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.UserDao;
import com.itheima.dao.RoleDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.*;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private MenuDao menuDao;

    public User findByUsername(String username) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            return null;
        }
        //获得用户id
        Integer userId = user.getId();
        //根据用户id查询，用户角色
        Set<Role> roles = roleDao.findByUserId(userId);
        //对查询的结果做非空判断
        if (roles != null && roles.size() > 0) {
            //遍历角色，获取角色权限
            for (Role role : roles) {
                Integer roleId = role.getId();
                //根据角色id，获得角色对应的权限
                Set<Permission> permissions = permissionDao.findByRoleId(roleId);
                if (permissions != null && permissions.size() > 0) {
                    role.setPermissions(permissions);
                }
            }
            user.setRoles(roles);
        }
        return user;
    }


    /**
     * 分页查询
     *
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //分页查询
        PageHelper.startPage(currentPage, pageSize);
        Page<User> page = userDao.findByCondition(queryString);

        //封装数据并返回
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增
     *
     * @param roleIds
     * @param user
     */
    @Override
    public void add(Integer[] roleIds, User user) {
        //user表里添加数据
        userDao.add(user);
        //中间表添加数据(map封装参数)
        Integer userId = user.getId();

        setUserAndRole(roleIds, userId);
    }



    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public User findById(Integer id) {
        User user = userDao.findById(id);
        return user;
    }

    /**
     * 根据id查询用户相关角色
     * @param id
     * @return
     */
    @Override
    public Integer findRoleIdsByUserId(Integer id) {
        return userDao.findRoleIdsByUserId(id);
    }

    //编辑用户
    @Override
    public void edit(Integer[] roleIds, User user) {
        //修改user表
        userDao.edit(user);
        //修改中间表
        //1.先删除原来关联数据
        Integer userId = user.getId();
        userDao.deleteRoleIdByUserId(userId);
        //2.建立新的关联关系
        setUserAndRole(roleIds,userId);

    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Integer id) {
        //先删除中间关系表数据
        userDao.deleteRoleIdByUserId(id);
        //删除用户数据
        userDao.delete(id);
    }

    /**
     * 根据用户名查询菜单
     * @param username
     * @return
     */
    @Override
    public List<Menu> findMenuByUser(String username) {
        User user = userDao.findByUsername(username);
        Integer userId = user.getId();
        Set<Role> roleSet = roleDao.findByUserId(userId);
        List<Menu> list = new ArrayList<>();
        for (Role role : roleSet) {
            Integer roleId = role.getId();
            Set<Menu> menuSet = menuDao.findByRoleId(roleId);
            for (Menu menu : menuSet) {
                List<Menu> menuChildren = menuDao.findByParentMenuId(menu.getId());
                menu.setChildren(menuChildren);
                list.add(menu);
            }
        }
        return list;
    }


    //添加用户和角色中间关系表方法抽取
    public void setUserAndRole(Integer[] roleIds, Integer userId) {
        if (roleIds != null && roleIds.length > 0){
            for (Integer roleId : roleIds) {
                Map<String,Integer> map = new HashMap();
                map.put("roleId",roleId);
                map.put("userId",userId);
                userDao.setUserAndRole(map);
            }
        }
    }


}
