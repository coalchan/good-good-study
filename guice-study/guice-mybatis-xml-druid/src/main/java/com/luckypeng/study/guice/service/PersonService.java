package com.luckypeng.study.guice.service;

import com.luckypeng.study.guice.model.Person;

/**
 * @author chenzhipeng
 * @date 2019/1/3 11:21
 */
public interface PersonService {
    /**
     * query by id
     * @param id
     * @return
     */
    Person get(Integer id);
}
