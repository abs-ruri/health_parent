package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class checkGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;
    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //新增检查组操作t_checkgroup表
        checkGroupDao.add(checkGroup);
        //关联检查组和检查项，操作t_checkgroup_checkitem表
        Integer checkGroupId = checkGroup.getId();
        this.setCheckGroupAndCheckItem(checkGroupId,checkitemIds);
    }

    @Override
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckGroup> page=checkGroupDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public CheckGroup findById(Integer id) {
        return  checkGroupDao.findById(id);

    }
    //根据检查组ID查询关联的检查项ID
    @Override
    public List<Integer> findCheckItemIdByCheckGroupId(Integer id) {
        return   checkGroupDao.findCheckItemIdByCheckGroupId(id);

    }

    @Override
    //编辑检查组信息，同时需要关联检查项
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {

        //修改检查组基本信息，操作检查组t_checkgroup表
        checkGroupDao.edit(checkGroup);

        //清理当前检查组关联的检查项，操作中间表t_checkgroup_checkitem表
        checkGroupDao.deleteAssocication(checkGroup.getId());

        //重新建立关系
        Integer checkGroupId = checkGroup.getId();
        this.setCheckGroupAndCheckItem(checkGroupId,checkitemIds);
    }

    public List<CheckGroup> findAll(){

        List<CheckGroup> checkGroups = checkGroupDao.findAll();
        return checkGroups;
    }

    @Override
    public void deleteByid(Integer id) {
        List<Integer> checkItemIdByCheckGroupId = checkGroupDao.findCheckItemIdByCheckGroupId(id);
             //根据id删除检查组,先删除checkItem
        for (Integer integer : checkItemIdByCheckGroupId) {
            checkGroupDao.deleteByid(integer);
        }
        //再删除检查组
        checkGroupDao.deleteGroup(id);
        //删除检查组和套餐的关系
        //checkGroupDao.deleteGroupAndSetmeal(id);
    }


    //建立检查项和检查组的多对多关系
    public void setCheckGroupAndCheckItem(Integer checkGroupId, Integer[] checkitemIds) {
        if (checkitemIds!=null&&checkitemIds.length>0){
            Map<String, Integer> map = new HashMap<>();
            for (Integer checkitemId : checkitemIds) {
                map.put("checkgroupId",checkGroupId);
                map.put("checkitemId",checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }


}
