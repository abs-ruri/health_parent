package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;

import com.itheima.pojo.Role;
import com.itheima.service.RoleService;
import com.itheima.utils.POIUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Reference
    private RoleService roleService;

    @RequestMapping("/findAllRole")
    public Result findAllRole(){

        List<Role> roleList = roleService.findAll();
        return new Result(true, MessageConstant.GET_ROLE,roleList);

    }

    @RequestMapping("/addRole")
    public Result addRole(Integer[] permissionIds,Integer[] menuIds,@RequestBody Role role){

       /* Role role = addRole.getFormData();
        Integer[] menuIds = addRole.getMenuIds();
        Integer[] permissionIds = addRole.getPermissionIds();*/

        try {
            if (role==null){
                return new Result(false,MessageConstant.Fail);
            }
            roleService.addRole(role,permissionIds,menuIds);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return new Result(true,MessageConstant.GET_ROLE);
    }

    @RequestMapping("/findRoleById")
    public Result findRoleById(Integer id){

        Role role = roleService.findRoleById(id);
        return new Result(true,MessageConstant.GET_ROLE,role);

    }

    @RequestMapping("/EditRole")
    public Result EditRole(Integer[] permissionIds,Integer[] menuIds,@RequestBody Role role){

        //更新role
        roleService.EditRole(role);
        Integer roleId = role.getId();
        //跟新role和permission的关系
         roleService.EditRoleAndPermission(roleId,permissionIds);
        //role和menu的关系

        roleService.EditRoleAndMenu(roleId,menuIds);
        return new Result(true,MessageConstant.GET_ROLE);

    }
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){

       PageResult pageResults =  roleService.findPage(queryPageBean);
       return pageResults;

    }

    @RequestMapping("/deleteById")
    public Result deleteById(Integer id){
        // roleService.deleteById(id);
        return new Result(true,MessageConstant.DELETE_MEMBER_SUCCESS);
    }



}
