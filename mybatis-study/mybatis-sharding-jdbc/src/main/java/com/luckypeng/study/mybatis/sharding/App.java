package com.luckypeng.study.mybatis.sharding;

import com.luckypeng.study.mybatis.sharding.dao.OrderMapper;
import com.luckypeng.study.mybatis.sharding.model.Order;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * @author coalchan
 */
public class App {
    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(reader);
        SqlSession session = factory.openSession();

        OrderMapper orderMapper = session.getMapper(OrderMapper.class);
        try {
            Order order = orderMapper.get(547535475963854849L);
            System.out.println(order.getUserId());
            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();
        }
    }
}
