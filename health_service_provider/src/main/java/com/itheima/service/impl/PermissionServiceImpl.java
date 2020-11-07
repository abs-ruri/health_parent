package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.PermissionDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Permission;
import com.itheima.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceClass = PermissionService.class)
@Transactional
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionDao permissionDao;

    /**
     * 查询persimmion
     * @return
     */
    public List<Permission> findAllpermission(){
        List<Permission> permissionList = permissionDao.findAllpermission();
        return permissionList;
    }

    @Override
    public List<Integer> findPermissionById(Integer id) {
        List<Integer> permissionList = permissionDao.findPermissionById(id);
        return permissionList;
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {

        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        PageHelper.startPage(currentPage,pageSize);

        Page<Permission> permissions = permissionDao.findPage(queryString);

        return new PageResult(permissions.getTotal(),permissions.getResult());

    }

    @Override
    public void add(Permission permission) {
        permissionDao.add(permission);
    }

    @Override
    public Permission findAll(Integer id) {
        Permission permission = permissionDao.findAll(id);
        return permission;
    }

    @Override
    public void edit(Permission permission) {
        permissionDao.edit(permission);
    }

    @Override
    public void delete(Integer id) {
        permissionDao.delete(id);
    }


}
