package com.luckypeng.study.sharding.mybatis.model;

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
