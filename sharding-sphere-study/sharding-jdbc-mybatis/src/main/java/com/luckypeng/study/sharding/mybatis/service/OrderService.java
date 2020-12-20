package com.luckypeng.study.sharding.mybatis.service;

import com.luckypeng.study.sharding.mybatis.dao.OrderMapper;
import com.luckypeng.study.sharding.mybatis.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author coalchan
 */
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public void save(Order order) {
        orderMapper.save(order);
    }

    public Order get(Long id) {
        return orderMapper.get(id);
    }
}
