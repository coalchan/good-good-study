package com.luckypeng.study.sharding.hello.dao;

import com.luckypeng.study.sharding.hello.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author coalchan
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

}