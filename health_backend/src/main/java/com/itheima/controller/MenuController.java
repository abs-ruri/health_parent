package com.itheima.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Menu;
import com.itheima.security.SpringSecurityUserService;
import com.itheima.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {



    @Reference
    private MenuService menuService;

    @RequestMapping("/findAllmenu")
    public Result findAllmenu(){

        List<Menu> allmenu = menuService.findAllmenu();
        if (allmenu!=null){
            return new Result(true, MessageConstant.GET_ROLE,allmenu);
        }
        return new Result(false,MessageConstant.Fail);
    }

    @RequestMapping("/findMenuById")
    public Result findMenuById(Integer id){

        List<Integer> menuList = menuService.findMenuById(id);

        return new Result(true, MessageConstant.GET_ROLE,menuList);
    }




    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = menuService.findPage(queryPageBean);
        return pageResult;
    }

    /**
     * 查询父菜单
     * @return
     */
    @RequestMapping("/findParentMenu")
    public Result findParentMenu(){
        try {
            List<Menu> list = menuService.findParentMenu();
            return new Result(true,MessageConstant.QUERY_MENU_SUCCESS,list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_MENU_FAIL);
        }

    }

    /**
     * 新增
     * @param menu
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Menu menu){
        System.out.println(menu.getParentMenuId());
        try {
            menuService.add(menu);
            return new Result(true,MessageConstant.ADD_MENU_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_MENU_FAIL);
        }
    }


    /**
     * 根据id查询
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            Menu menu = menuService.findById(id);
            return new Result(true,MessageConstant.QUERY_MENU_SUCCESS,menu);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_MENU_FAIL);
        }

    }

    /**
     * 编辑
     * @param menu
     * @return
     */
    @RequestMapping("/edit")
    public Result edit(@RequestBody Menu menu){
        try {
            menuService.edit(menu);
            return new Result(true,MessageConstant.EDIT_MENU_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.EDIT_MENU_FAIL);
        }
    }

    @RequestMapping("/delete")
    public Result delete(Integer id){
        try {
            menuService.delete(id);
            return new Result(true,MessageConstant.DELETE_MENU_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.DELETE_MENU_FAIL);
        }
    }

}
