package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    //使用JedisPool操作redis服务
    @Autowired
    private JedisPool jedisPool;
    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile){
        System.out.println(imgFile);
        //获取原始文件名
        String originalFilename = imgFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");

        //获取文件后缀

        String substring = originalFilename.substring(i);//.jpg

        //使用UUID随机产生文件名，防止文件同名覆盖
        String fileName = UUID.randomUUID().toString()+substring;

        //将文件上传到服务器
        try {
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            //将上传图片名称存入Redis，基于Redis的Set集合存储
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
        return new Result(true,MessageConstant.PIC_UPLOAD_SUCCESS,fileName);

    }

    @Reference
    private SetmealService setmealService;

    //新增套餐
    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds){

        try {
            setmealService.add(setmeal,checkgroupIds);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }
        return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
       return setmealService.findPage(queryPageBean);
    }

    @RequestMapping("/findById")
    public Result findById(Integer id){

        try {
            Setmeal setmeal = setmealService.findByIdToUpdate(id);
            return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,setmeal);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }
    //复选框回显
    @RequestMapping("/checkId")
    public Result checkId(Integer id){
        List<Integer> gruopId=null;
        try {
            gruopId= setmealService.checkId(id);
            for (Integer integer : gruopId) {
                System.out.println("复选框id"+integer);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEALLIST_FAIL);
        }
        return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,gruopId);
    }



}
