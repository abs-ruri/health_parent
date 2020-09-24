package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;

    @Override
    public Result order(Map map) throws Exception {
        //1.查询当前选择的日期是否能够进行预约
        //获取预约日期
        String orderDate = (String) map.get("orderDate");
        Date date = DateUtils.parseString2Date(orderDate);
        //根据日期查询预约设置信息
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(date);
        if (orderSetting!=null){
            //证明可以预约，查看当前预约人数是否已满
            int number = orderSetting.getNumber();
            int reservations = orderSetting.getReservations();

            if (reservations>=number){
                //人数已满
                return new Result(false,MessageConstant.ORDER_FULL);
            }
            //人数未满可以预约，检查是否为会员
            String telephone = (String) map.get("telephone");
            Member member = memberDao.findByTelephone(telephone);
            if (member==null){
                //说明为空需要注册
                member.setSex((String) map.get("sex"));
                member.setPhoneNumber(telephone);
                member.setRegTime(new Date());
                member.setIdCard((String) map.get("idCard"));
                member.setName((String) map.get("name"));
                memberDao.add(member);

            }else {
                //不为空，已经是会员了，检查是否重复预约
                //查询预约的日期，会员id，套餐id
                Integer setmealId = (Integer) map.get("setmealId");
                Integer memberId = member.getId();
                Order order = new Order(memberId, date, setmealId);
                List<Order> orderList = orderDao.findByCondition(order);
                if (orderList!=null&&orderList.size()>0){
                    //不为空，说明重复预约
                    return new Result(false,MessageConstant.HAS_ORDERED);
                }
            }
            //注册完成后，order添加预约参数
            Order order = new Order();
            order.setMemberId(member.getId());
            order.setOrderDate(date);
            order.setOrderType((String) map.get("orderType"));
            order.setOrderStatus(order.ORDERSTATUS_NO);
            order.setSetmealId(  Integer.parseInt((String)map.get("setmealId")));
            orderDao.add(order);
            //跟新预约人数
             reservations = reservations+1;
             orderSetting.setReservations(reservations);
             orderSettingDao.editReservationsByOrderDate(orderSetting);
            return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());
        }else {

            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }



       /* //获取预约日期
        String orderDate = (String) map.get("orderDate");
        // 1、检查用户所选择的预约日期是否已经提前进行了预约设置，如果没有设置则无法进行预约
            OrderSetting orderSetting = orderSettingDao.findByOrderDate(DateUtils.parseString2Date(orderDate));
            if (orderSetting==null){
                //所选日期不能进行体检预约
                return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
            }
        //2、检查用户所选择的预约日期是否已经约满，如果已经约满则无法预约
        int number = orderSetting.getNumber();
        int reservations = orderSetting.getReservations();
        if (reservations>number){
            return new Result(false,MessageConstant.ORDER_FULL);
        }
        //3、检查用户是否重复预约（同一个用户在同一天预约了同一个套餐），如果是重复预约则无法完成再次预约
        String telephone = (String) map.get("telephone");
        //根据输入的手机号，查询是否注册过
        Member member = memberDao.findByTelephone(telephone);
        if (member!=null){
            //注册过，则查询成员当前预约的套餐和日期，是否和已经预约的相同
            Integer memberId = member.getId();//获得成员id
            //获得当前预约日期
            Date order_Date = DateUtils.parseString2Date(orderDate);
            //获取当前预约的套餐
            String setmealId = (String) map.get("setmealId");
            Order order = new Order(memberId, order_Date, Integer.parseInt(setmealId));
            List<Order> list = orderDao.findByCondition(order);
            if (list!=null&&list.size()>0){
                //不为空，说明重复预约
                return new Result(false,MessageConstant.HAS_ORDERED);
            }

        }else {
            //为空则证明没有注册过
            //4、检查当前用户是否为会员，如果是会员则直接完成预约，如果不是会员则自动完成注册并进行预约
             member= new Member();
             member.setName((String) map.get(("name")));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());
            memberDao.add(member);//自动完成会员注册
        }
        //5、预约成功，添加到预约表单，并更新预约人数
        Order order = new Order();
        order.setMemberId(member.getId());
        order.setOrderDate(DateUtils.parseString2Date(orderDate));
        order.setOrderType((String) map.get("orderType"));
        order.setOrderStatus(order.ORDERSTATUS_NO);
        order.setSetmealId(  Integer.parseInt((String)map.get("setmealId")));
        orderDao.add(order);
        //获取当前时间的预约人数
        int settingReservations = orderSetting.getReservations();
        //重新设置人数
        orderSetting.setReservations(settingReservations+1);

        orderSettingDao.editReservationsByOrderDate(orderSetting);
        return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());*/
    }

    @Override
    public Map findById(Integer id) throws Exception {
        Map map = orderDao.findById4Detail(id);
        if (map!=null){
            //处理日期格式
            Date orderDate = (Date) map.get("orderDate");
            map.put("orderDate",DateUtils.parseDate2String(orderDate));
        }
        return map;
    }
}
