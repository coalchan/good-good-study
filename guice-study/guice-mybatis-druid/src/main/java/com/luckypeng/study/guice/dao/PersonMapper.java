package com.luckypeng.study.guice.dao;

import com.luckypeng.study.guice.model.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    @Select("select id,age,name from person where id = #{id}")
    Person get(Integer id);
}
