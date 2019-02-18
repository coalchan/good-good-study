package com.luckypeng.study.drools.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenzhipeng
 * @date 2018/8/17 14:36
 */
@Data
@NoArgsConstructor
public class Person {
    private String name;
    private Integer age;
    private String gender;
    private String like;

    public Person(Integer age) {
        this.age = age;
    }

    public Person(Integer age, String name) {
        this.age = age;
        this.name = name;
    }
}
