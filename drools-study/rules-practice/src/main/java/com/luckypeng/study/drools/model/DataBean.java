package com.luckypeng.study.drools.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenzhipeng
 * @date 2018/8/17 11:40
 */
@Data
@NoArgsConstructor
public class DataBean {
    private Integer cnt;

    private String tag;

    private Double value;

    private String expr;

    private Boolean result = false;

    public DataBean(Double value) {
        this.value = value;
    }
}
