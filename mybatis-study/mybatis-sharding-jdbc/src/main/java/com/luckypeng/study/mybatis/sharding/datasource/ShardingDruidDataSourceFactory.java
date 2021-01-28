package com.luckypeng.study.mybatis.sharding.datasource;

import org.apache.commons.io.IOUtils;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.yaml.YamlShardingDataSourceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * @author coalchan
 */
public class ShardingDruidDataSourceFactory extends PooledDataSourceFactory{
    public ShardingDruidDataSourceFactory() throws IOException, SQLException {
        InputStream inputStream = ShardingDruidDataSourceFactory.class.getClassLoader()
                .getResourceAsStream("sharding.yml");
        byte[] yamlBytes = IOUtils.toByteArray(inputStream);
        this.dataSource = YamlShardingDataSourceFactory.createDataSource(yamlBytes);
    }
}
