package com.luckypeng.study.mybatis.sharding.model;

import lombok.Data;

/**
 * @author coalchan
 */
@Data
public class Order {
    private Long orderId;

    private Integer userId;

    private Integer status = 1;
}
