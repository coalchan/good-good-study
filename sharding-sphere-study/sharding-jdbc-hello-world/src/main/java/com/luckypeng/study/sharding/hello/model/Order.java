package com.luckypeng.study.sharding.hello.model;

import lombok.Data;

import javax.persistence.*;

/**
 * @author coalchan
 */
@Data
@Entity
@Table(name = "t_order")
public class Order {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Integer userId;

    private Integer status = 1;

    public Order() {
    }
}
