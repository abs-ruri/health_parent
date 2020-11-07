package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.MenuDao;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.RoleDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Menu;
import com.itheima.pojo.Role;
import com.itheima.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service(interfaceClass = RoleService.class)
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private MenuDao menuDao;
    @Override
    public List<Role> findAll() {

       List<Role> roles =  roleDao.findAll();

       return roles;
    }

    @Override
    public void addRole(Role role, Integer[] permissionIds, Integer[] menuIds) {
        //先添加角色，获得角色添加后的id
         roleDao.addRole(role);
        Integer roleId = role.getId();

        //根据添加后的id，关联permission
        setRelation(roleId,permissionIds);
        //根据添加后的id，关联menu
        setRelationMenu(roleId,menuIds);

    }
    //根据id查询
    @Override
    public Role findRoleById(Integer id) {

        Role role = roleDao.findRoleById(id);
        return role;
    }

    @Override
    public void EditRole(Role role) {
        roleDao.EditRole(role);
    }

    //编辑role和permission的关系
    @Override
    public void EditRoleAndPermission(Integer roleId, Integer[] permissionIds) {
        //先删除之前的关系
        roleDao.deleteRoleAndPermission(roleId);

        //在建立关系
        setRelation(roleId,permissionIds);
    }

    //编辑role和menu的关系
    @Override
    public void EditRoleAndMenu(Integer roleId, Integer[] menuIds) {
        //先删除之前的关系
        roleDao.deleteRoleAndMenu(roleId);
        //在建立关系
        setRelationMenu(roleId,menuIds);

    }

    /**
     * 查询
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {

        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);

        Page<Role> page = roleDao.findPage(queryString);
        return new PageResult(page.getTotal(),page.getResult());

    }


    @Override
    public void deleteById(Integer id) {
        roleDao.deleteById(id);
    }

    //设置role和permissionIds表单之间的关系
    private void  setRelation(Integer roleId,Integer[] permissionIds){
        if (permissionIds.length>0){
            HashMap<String, Integer> map = new HashMap<>();
            for (Integer permissionId : permissionIds) {
                map.put("roleId",roleId);
                map.put("permissionId",permissionId);
                roleDao.setRelation(map);
            }
        }
    }

    //设置role和menu表单之间的关系
    private void  setRelationMenu(Integer roleId,Integer[] menuIds){
        if (menuIds.length>0){
            HashMap<String, Integer> map = new HashMap<>();
            for (Integer menuId : menuIds) {
                map.put("roleId",roleId);
                map.put("menuId",menuId);
                roleDao.setRelationMenu(map);
            }
        }
    }
}
