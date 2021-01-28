package com.luckypeng.study.mybatis.hello.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;

/**
 * @author coalchan
 */
public class DruidDataSourceFactory extends PooledDataSourceFactory{
    public DruidDataSourceFactory() {
        this.dataSource = new DruidDataSource();
    }
}
