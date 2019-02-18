package com.luckypeng.study.guice.inject;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.luckypeng.study.guice.service.PersonService;
import com.luckypeng.study.guice.service.impl.PersonServiceImpl;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.druid.DruidDataSourceProvider;

import java.io.IOException;
import java.util.Properties;

/**
 * @author chenzhipeng
 * @date 2019/1/3 10:58
 */
public class MyModule extends AbstractModule {
    private String file = "app.properties";

    @Override
    protected void configure() {
        Properties props = new Properties();
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Names.bindProperties(binder(), props);

        install(new MyBatisModule() {
            @Override
            protected void initialize() {
                //绑定我们自定义的数据源provider，也可以使用guice已经编写好的
                bindDataSourceProviderType(DruidDataSourceProvider.class);
                bindTransactionFactoryType(JdbcTransactionFactory.class);

                // 添加我们的mapper接口，可以按类注入（即通过类名注入），也可以指定整个包的路径
//                addMapperClass(PersonMapper.class);
                addMapperClasses("com.luckypeng.study.guice.dao");


            }
        });

        bind(PersonService.class).to(PersonServiceImpl.class);
    }
}
