package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckGroup;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {

    void add(CheckGroup checkGroup);

    void setCheckGroupAndCheckItem(Map<String, Integer> map);

    Page<CheckGroup> selectByCondition(String queryString);

    CheckGroup findById(Integer id);

    List<Integer> findCheckItemIdByCheckGroupId(Integer id);

    void edit(CheckGroup checkGroup);

    void deleteAssocication(Integer id);

    List<CheckGroup> findAll();

    void deleteByid(Integer id);

    void deleteGroup(Integer id);

    void deleteGroupAndSetmeal(Integer id);
}
