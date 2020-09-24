package com.itheima.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SpringSecurityUserService implements UserDetailsService {
    @Reference
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    //根据用户名查询用户信息
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("输入的用户名"+username);
        //远程调用用户服务信息，根据用户名查询用户信息
        User user = userService.findByUsername(username);
        String password = user.getPassword();
        System.out.println("查询的密码"+password);
        String encode = passwordEncoder.encode(password);
        System.out.println("加密后的密码"+encode);
        //判断查询的数据是否为空
        if (user==null){
            return null;
        }
        //根据框架要求，创建集合，添加角色的权限
        List<GrantedAuthority> list = new ArrayList<>();
        //获取用户的角色,一个用户可能有多个角色
        Set<Role> roles = user.getRoles();
        //遍历角色，获得角色的权限,并添加到集合中
        for (Role role : roles) {
            System.out.println("当前用户的role"+role);
            //遍历角色集合，为用户授予角色
            list.add(new SimpleGrantedAuthority(role.getKeyword()));
            //遍历权限集合，为用户授权
            Set<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions) {
                System.out.println("当前用户的permission"+permission);
                list.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
        }
        //提交给框架验证
        org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User(username, user.getPassword(), list);
        String userPassword = securityUser.getPassword();
        System.out.println("提交给框架的密码"+userPassword);
        return securityUser;
    }
}
