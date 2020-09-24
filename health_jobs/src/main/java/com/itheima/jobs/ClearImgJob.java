package com.itheima.jobs;

import com.itheima.constant.RedisConstant;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * 自定义 job，实现定时清理垃圾图片
 */
public class ClearImgJob {
    @Autowired
    private JedisPool jedisPool;
    public void clearImg(){
        //根据redis中保存的两个set集合进行差值计算
        Set<String> sdiff = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_DB_RESOURCES, RedisConstant.SETMEAL_PIC_RESOURCES);
        if (sdiff!=null){
            //循环遍历
            for (String s : sdiff) {
                //删除除七牛云服务器上的图片
                QiniuUtils.deleteFileFromQiniu(s);
                //删除redis集合中的图片名称
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,s);
            }
        }

    }



}
