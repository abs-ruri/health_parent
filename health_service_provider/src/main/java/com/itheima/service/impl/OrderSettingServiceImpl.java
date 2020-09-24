package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(interfaceClass =OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    //批量添加
    @Override
    public void add(List<OrderSetting> list) {

        if (list!=null&&list.size()>0){
            for (OrderSetting orderSetting : list) {
                //检查此日期是否存在
              long count=  orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
              if (count>0){
                  //已经存在，执行更新操作
                  orderSettingDao.editNumberByOrderDate(orderSetting);
              }
              //不存在，执行插入操作
                orderSettingDao.add(orderSetting);
            }
        }

    }

    @Override
    public List<Map> getOrderSettingByMonth(String date) {

        String dataBegin = date + "-1";//2019-3-1
        String dataEnd = date + "-31";//2019-3-31

        Map<String, String> map = new HashMap<>();
        map.put("dataBegin",dataBegin);
        map.put("dataEnd",dataEnd);

        //根据年月查询当月的数据
       List<OrderSetting> list =  orderSettingDao.getOrderSettingByMonth(map);
        //添加封装后的数据
        List<Map> data = new ArrayList<>();
        for (OrderSetting orderSetting : list) {
            //将查询后的数据进行封装
            Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("date",orderSetting.getOrderDate().getDate());
            hashMap.put("number",orderSetting.getNumber());
            hashMap.put("reservations",orderSetting.getReservations());
            data.add(hashMap);
        }
        //返回数据
        return data;


    }

    @Override
    public void editNumberByDate(OrderSetting orderSetting) {

        long countByOrderDate = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if (countByOrderDate>0){
            //当前日期已经进行了预约，需要进行修改
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else {
            //没有预约，则进行插入
            orderSettingDao.add(orderSetting);
        }
    }
}
