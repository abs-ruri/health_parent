package com.itheima.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Permission;
import com.itheima.service.PermissionService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Reference
    private PermissionService permissionService;

    @RequestMapping("/findAllpermission")
    public Result findAllpermission(){
        List<Permission> allpermission =
                permissionService.findAllpermission();
        if (allpermission!=null){
            return new Result(true, MessageConstant.GET_ROLE,allpermission);
        }
        return new Result(false,MessageConstant.Fail);
    }

    @RequestMapping("/findPermissionById")
    public Result findPermissionById(Integer id){

        List<Integer> permissionList = permissionService.findPermissionById(id);

        return new Result(true,MessageConstant.GET_ROLE,permissionList);
    }

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){

         PageResult pageResult =permissionService.findPage(queryPageBean);

         return pageResult;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody Permission permission){

        permissionService.add(permission);
        return new Result(true,MessageConstant.ADD_MEMBER_SUCCESS);
    }

    @RequestMapping("/findAll")
    public Result findAll(Integer id){

        try {
           Permission permission =  permissionService.findAll(id);
            return new Result(true,MessageConstant.GET_ROLE,permission);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.Fail);
        }

    }

    @RequestMapping("/edit")
    public Result edit(@RequestBody Permission permission){

        permissionService.edit(permission);
        return new Result(true,MessageConstant.GET_ROLE);

    }
    @RequestMapping("/delete")
    public Result delete(Integer id){
        permissionService.delete(id);
        return new Result(true,MessageConstant.GET_ROLE);
    }


}
