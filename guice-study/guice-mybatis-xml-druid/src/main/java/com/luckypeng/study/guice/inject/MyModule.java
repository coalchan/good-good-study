package com.luckypeng.study.guice.inject;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.luckypeng.study.guice.service.PersonService;
import com.luckypeng.study.guice.service.impl.PersonServiceImpl;
import org.mybatis.guice.XMLMyBatisModule;

import java.io.IOException;
import java.util.Properties;

/**
 * @author chenzhipeng
 * @date 2019/1/3 10:58
 * To change this template use File | Settings | File Templates.
 */
public class MyModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new XMLMyBatisModule() {
            @Override
            protected void initialize() {
                setClassPathResource("mybatis-config.xml");
            }
        });

        bind(PersonService.class).to(PersonServiceImpl.class);
    }
}
