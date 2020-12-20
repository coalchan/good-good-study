package com.luckypeng.study.sharding.mybatis.dao;

import com.luckypeng.study.sharding.mybatis.model.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    void save(Order order);

    Order get(Long id);
}
