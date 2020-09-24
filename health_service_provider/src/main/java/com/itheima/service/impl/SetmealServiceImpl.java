package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${out_put_path}")
    private String outPutPath;

    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {

        //添加套餐
        setmealDao.add(setmeal);
        //绑定套餐和组id的关系
        if (checkgroupIds != null && checkgroupIds.length > 0) {
            Map<String, Integer> map = new HashMap<>();
            //获得套餐插入后的id
            Integer id = setmeal.getId();
            //遍历组id进行绑定
            for (Integer checkgroupId : checkgroupIds) {
                map.put("setmeal_id", id);
                map.put("checkgroup_id", checkgroupId);
                setmealDao.setSetmealAndCheckGroup(map);
            }
        }
        //将图片名称保存到redis
        savePic2Redis(setmeal.getImg());

        //当添加套餐后需要重新生成静态页面（套餐列表页面、套餐详情页面）

        generateMobileStaticHtml();

    }

    //当前方法所需的静态页面
    private void generateMobileStaticHtml() {

        //在生成静态页面之前需要查询数据,查询所有的套餐
        List<Setmeal> all = setmealDao.findAll();
        //根据数据生成套餐的静态页面
        generateMobileSetmealListHtml(all);
        //生成套餐详细页
        generateMobileSetmealDetailHtml(all);

    }
    //生成套餐详情静态页面（可能有多个）
    private void generateMobileSetmealDetailHtml(List<Setmeal> all) {
        HashMap hashMap = new HashMap();
        for (Setmeal setmeal : all) {
            hashMap.put("setmeal",setmealDao.findById(setmeal.getId()));
            generateHtml("mobile_setmeal_detail.ftl","setmeal_detail_"+setmeal.getId()+".html",hashMap);
        }
    }
    //生成套餐列表的静态页面
    private void generateMobileSetmealListHtml(List<Setmeal> all) {
        HashMap hashMap = new HashMap();
        hashMap.put("setmealList",all);
        generateHtml("mobile_setmeal.ftl","m_setmeal.html",hashMap);
    }
    //通用生成静态页面
    public void generateHtml(String templateName, String htmlPageName, Map map) {
        //获取配置对象
        Configuration configuration =
                freeMarkerConfigurer.getConfiguration();
        try {
            //加载配置模板
            Template template = configuration.getTemplate(templateName);
            //创建输出流,在指定文件夹生成静态页面
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outPutPath + "/" + htmlPageName), "utf-8");
            PrintWriter printWriter = new PrintWriter(writer);
            template.process(map,printWriter);
            printWriter.flush();
            printWriter.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //将图片名称保存到redis
    private void savePic2Redis(String img) {
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, img);
    }

    //查找全部
    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage, pageSize);
        Page<Setmeal> page = setmealDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Setmeal> findAll() {

        return setmealDao.findAll();

    }

    @Override
    public Setmeal findById(Integer id) {
        Setmeal setmeal = setmealDao.findById(id);
        return setmeal;
    }

    //查询套餐预约占比数据
    public List<Map<String, Object>> findSetmealCount() {

        return setmealDao.findSetmealCount();
    }

    @Override
    public Setmeal findByIdToUpdate(Integer id) {
        return setmealDao.findByIdToUpdate(id);
    }

   //查询套餐包含的检查组
    public List<Integer> checkId(Integer id) {
       List<Integer> groupId =  setmealDao.checkId(id);
       return groupId;
    }


}
