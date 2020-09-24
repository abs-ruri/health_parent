package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;

import java.util.List;

//服务接口
public interface CheckItemService {

    public void add(CheckItem checkItem);

    public PageResult pageQuery(QueryPageBean queryPageBean);

    void deleteById(Integer id);

    void edit(CheckItem checkItem);

    public CheckItem findById(Integer id);

    List<CheckItem> findAll();
}
