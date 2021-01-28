package com.luckypeng.study.mybatis.sharding.dao;

import com.luckypeng.study.mybatis.sharding.model.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author coalchan
 */
@Mapper
public interface OrderMapper {

    void save(Order order);

    Order get(Long id);
}
