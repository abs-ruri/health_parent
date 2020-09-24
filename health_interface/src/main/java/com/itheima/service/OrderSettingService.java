package com.itheima.service;

import com.itheima.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {
    void add(List<OrderSetting> orderSettingArrayList);

    List<Map> getOrderSettingByMonth(String data);

    void editNumberByDate(OrderSetting orderSetting);

    /*public void add(List<OrderSetting> data);
    public List<Map> getOrderSettingByMonth(String date);
    public void editNumberByDate(OrderSetting orderSetting);*/
}
