package com.luckypeng.study.sharding.hello.service;

import com.luckypeng.study.sharding.hello.dao.OrderRepository;
import com.luckypeng.study.sharding.hello.model.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author coalchan
 */
@Service
public class OrderService {

    @Resource
    private OrderRepository orderRepository;

    public void save(Order order) {
        orderRepository.save(order);
    }

}
