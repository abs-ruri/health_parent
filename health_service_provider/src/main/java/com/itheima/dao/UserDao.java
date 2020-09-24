package com.itheima.dao;

import com.itheima.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserDao {
    User findByUsername(@Param("username") String username);
}
