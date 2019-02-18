package com.luckypeng.study.guice.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;

/**
 * @author chenzhipeng
 * @date 2019/1/3 14:11
 */
public class DruidDataSourceFactory extends PooledDataSourceFactory{
    public DruidDataSourceFactory() {
        this.dataSource = new DruidDataSource();
    }
}
