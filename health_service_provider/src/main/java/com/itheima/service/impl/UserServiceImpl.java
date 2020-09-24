package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.UserDao;
import com.itheima.dao.RoleDao;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;

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
}
