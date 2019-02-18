package com.luckypeng.study.guice.service.impl;

import com.luckypeng.study.guice.dao.PersonMapper;
import com.luckypeng.study.guice.model.Person;
import com.luckypeng.study.guice.service.PersonService;

import javax.inject.Inject;

/**
 * @author chenzhipeng
 * @date 2019/1/3 11:22
 * To change this template use File | Settings | File Templates.
 */
public class PersonServiceImpl implements PersonService {
    @Inject
    private PersonMapper personMapper;

    @Override
    public Person get(Integer id) {
        return personMapper.get(id);
    }
}
