package com.luckypeng.study.mybatis.hello;

import com.luckypeng.study.mybatis.hello.dao.PersonMapper;
import com.luckypeng.study.mybatis.hello.model.Person;
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

        PersonMapper personMapper = session.getMapper(PersonMapper.class);
        try {
            Person person = personMapper.get(1);
            session.commit();
        } catch (Exception e) {
            session.rollback();
        } finally {
            session.close();;
        }
    }
}
