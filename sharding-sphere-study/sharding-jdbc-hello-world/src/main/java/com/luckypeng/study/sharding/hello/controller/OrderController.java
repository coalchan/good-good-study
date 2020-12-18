package com.luckypeng.study.sharding.hello.controller;

import com.luckypeng.study.sharding.hello.model.Order;
import com.luckypeng.study.sharding.hello.service.OrderService;
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
}
