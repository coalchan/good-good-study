package com.luckypeng.study.mybatis.hello.dao;

import com.luckypeng.study.mybatis.hello.model.Person;

/**
 * @author coalchan
 */
public interface PersonMapper {
    /**
     * query by id
     * @param id
     * @return
     */
    Person get(Integer id);
}
