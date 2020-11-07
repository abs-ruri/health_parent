package com.itheima.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Menu;


import com.itheima.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;
    @RequestMapping("/getUsername")
    public Result getUsername(){
        //当Spring security完成认证后，会将当前用户信息保存到框架提供的上下文对象
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();


        if (user!=null){

            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,user);
        }

        return new Result(false, MessageConstant.GET_USERNAME_FAIL);

    }





    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = userService.findPage(queryPageBean);
        return pageResult;
    }

    /**
     * 新增
     * @param roleIds
     * @param user
     * @return
     */
    @RequestMapping("/add")
    public Result add(Integer[] roleIds, @RequestBody com.itheima.pojo.User user){
        try {
            String password = user.getPassword();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encode = bCryptPasswordEncoder.encode(password);
            user.setPassword(encode);
            userService.add(roleIds,user);
            return new Result(true,MessageConstant.ADD_USER_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_USER_FAIL);
        }
    }

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            com.itheima.pojo.User user = userService.findById(id);
            return new Result(true,MessageConstant.GET_USER_SUCCESS,user);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_USER_FAIL);
        }
    }

    /**
     * 根据id查询用户相关联的角色id
     * @param id
     * @return
     */
    @RequestMapping("/findRoleIdsByUserId")
    public Result findRoleIdsByUserId(Integer id){
        try {
            Integer list = userService.findRoleIdsByUserId(id);
            return new Result(true,MessageConstant.GET_ROLE_LIST_SUCCESS,list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_ROLE_LIST_FAIL);
        }
    }

    /**
     * 编辑
     * @param roleIds
     * @param user
     * @return
     */
    @RequestMapping("/edit")
    public Result edit(Integer[] roleIds, @RequestBody com.itheima.pojo.User user){
        try {
            String password = user.getPassword();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String encode = bCryptPasswordEncoder.encode(password);
            user.setPassword(encode);
            userService.edit(roleIds,user);
            return new Result(true,MessageConstant.EDIT_USER_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.EDIT_USER_FAIL);
        }
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Integer id){
        try {
            userService.delete(id);
            return new Result(true,MessageConstant.DELETE_USER_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.DELETE_USER_FAIL);
        }
    }

    /**
     * 根据用户名查询菜单
     * @return
     */
    @RequestMapping("/getMenu")
    public Result getMenu(){
        User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null){
            String username = user.getUsername();
            List<Menu> list = userService.findMenuByUser(username);
            return new Result(true,MessageConstant.GET_MENU_SUCCESS,list);
        }else {
            return new Result(false,MessageConstant.GET_MENU_FAIL);
        }
    }

}
