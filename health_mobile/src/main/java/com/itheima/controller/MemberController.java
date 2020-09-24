package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;

    /**
     * 1、校验用户输入的短信验证码是否正确，如果验证码错误则登录失败
     * 2、如果验证码正确，则判断当前用户是否为会员，如果不是会员则自动完成会员注册
     * 3、向客户端写入Cookie，内容为用户手机号
     * 4、将会员信息保存到Redis，使用手机号作为key，保存时长为30分钟
     * @return
     */
    @RequestMapping("/login")
    public Result login( HttpServletResponse response,@RequestBody Map map){
        //校验验证码
        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");
        String validateCodeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
        if (validateCode!=null&&validateCodeInRedis!=null&&validateCode.equals(validateCodeInRedis)){
            //验证码匹配,检测是否为会员，不是自动注册
            Member member = memberService.findByTelephone(telephone);
            if (member==null){
                Member member1 = new Member();
                //不是会员自动完成注册
                member1.setRegTime(new Date());
                member1.setPhoneNumber(telephone);
                memberService.add(member1);
            }
            //向客户端浏览器写入cookie
            Cookie login_member_telephone = new Cookie("login_member_telephone", telephone);
            login_member_telephone.setPath("/");
            login_member_telephone.setMaxAge(60*60*24*30);
            response.addCookie(login_member_telephone);
            //保存到redis
            String json = JSON.toJSON(member).toString();
            jedisPool.getResource().setex("telephone",1800,json);
            return new Result(true,MessageConstant.LOGIN_SUCCESS);
        }else {
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }


    }
}
