package com.luckypeng.study.sharding.mybatis.controller;

import com.luckypeng.study.sharding.mybatis.model.Order;
import com.luckypeng.study.sharding.mybatis.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author coalchan
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/save")
    public String save(@RequestParam("userId") Integer userId) {
        Order entity = new Order();
        entity.setUserId(userId);
        orderService.save(entity);
        return "ok";
    }

    @GetMapping("/get")
    public Order get(@RequestParam("orderId") Long orderId) {
        return orderService.get(orderId);
    }
}
