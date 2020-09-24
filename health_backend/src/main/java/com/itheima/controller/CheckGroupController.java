package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckGroupService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {

    @Reference
    private CheckGroupService checkGroupService;

    @RequestMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds){

        try {
            if (checkGroup==null){
                return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
            }
            checkGroupService.add(checkGroup,checkitemIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
        }
        return new Result(true,MessageConstant.ADD_CHECKGROUP_SUCCESS);

    }

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageResult pageResult = checkGroupService.pageQuery(currentPage,pageSize,queryString);
        return pageResult;
    }
    //根据ID查询检查组
    @RequestMapping("/findById")
    public Result findById(Integer id){

        try {
            CheckGroup checkGroup =   checkGroupService.findById(id);
            return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroup);
        } catch (Exception e){
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }

    @RequestMapping("/findCheckItemIdByCheckGroupId")
    public Result findCheckItemIdByCheckGroupId(Integer id){

        try {
            List<Integer> checkitemIds =  checkGroupService.findCheckItemIdByCheckGroupId(id);
            return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkitemIds);
        } catch (Exception e) {

            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKITEM_FAIL);
        }

    }
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds){

        try {
            checkGroupService.edit(checkGroup,checkitemIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_CHECKGROUP_FAIL);//新增失败
        }
        return new Result(true,MessageConstant.EDIT_CHECKGROUP_SUCCESS);//新增成功
    }

    @RequestMapping("/findAll")
    public Result findAll(){

           List<CheckGroup> checkGroupList =  checkGroupService.findAll();
           if (checkGroupList!=null&&checkGroupList.size()>0){
               Result result = new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, checkGroupList);
               return result;
           }
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }

        @RequestMapping("/deleteByid")
        public Result deleteByid(Integer id){

            try {
                checkGroupService.deleteByid(id);
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false,MessageConstant.DELETE_CHECKGROUP_FAIL);
            }
            return new Result(true,MessageConstant.DELETE_CHECKGROUP_SUCCESS);

        }


    }




