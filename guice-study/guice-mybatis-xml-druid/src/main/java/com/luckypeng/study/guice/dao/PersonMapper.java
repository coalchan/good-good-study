package com.luckypeng.study.guice.dao;

import com.luckypeng.study.guice.model.Person;

/**
 * @author chenzhipeng
 * @date 2019/1/3 11:14
 */
public interface PersonMapper {
    /**
     * query by id
     * @param id
     * @return
     */
    Person get(Integer id);
}
