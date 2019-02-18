package com.luckypeng.study.drools.hello;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author chenzhipeng
 * @date 2018/8/16 17:55
 */
@Data
@AllArgsConstructor
public class Applicant {
    private String name;
    private int age;
    private boolean valid;
}
