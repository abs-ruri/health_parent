package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.itheima.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;


/**
 * 体检预约处理
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map) {

        String telephone = (String) map.get("telephone");
        //从redis中获取保存的验证码
        String validateCodeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);

        String validateCode = (String) map.get("validateCode");
        //将用户输入的验证码和redis中保存的验证码进行比较
        if (validateCodeInRedis!=null&&validateCode!=null&&validateCode.equals(validateCodeInRedis)){
            //如果成功，设置预约类型，分为微信预约、电话预约
            map.put("orderType", Order.ORDERTYPE_WEIXIN);
            Result result =null;
            try {
                 result =  orderService.order(map);
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
            }
            if (result.isFlag()){
                //预约成功，可以为用户发送短信
                try {
                    SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE,telephone,(String) map.get("orderDate"));
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }
            return result;

        }else {
            //如果比对不成功，返回结果给页面
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }


    }

    @RequestMapping("/findById")
    public Result findById(Integer id){
        Map map = null;
        try {
            map = orderService.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }
        return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
    }
}
