package com.luckypeng.study.drools;

import com.luckypeng.study.drools.model.DataBean;
import com.luckypeng.study.drools.model.Food;
import com.luckypeng.study.drools.model.Person;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.*;

/**
 * @author chenzhipeng
 * @date 2018/8/17 11:58
 */
public class CheckApplicationTest {
    /**
     * 创建会话
     * @param fileName
     * @return
     */
    private KieSession createSession(String fileName) {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource resource = ResourceFactory.newInputStreamResource(CheckApplicationTest.class.getClassLoader().getResourceAsStream(fileName));
        kBuilder.add(resource, ResourceType.DRL);

        KnowledgeBuilderErrors errors = kBuilder.getErrors();
        for (KnowledgeBuilderError error : errors) {
            System.out.println(error);
        }

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addPackages(kBuilder.getKnowledgePackages());
        return kBase.newKieSession();
    }

    /**
     * 单变量简单规则
     */
    @Test
    public void test1() {
        KieSession kSession = createSession("rule1.drl");

        DataBean data = new DataBean(-20.0);
        kSession.insert(data);
        // 校验规则
        kSession.fireAllRules();

        assertTrue(data.getResult());

        DataBean t = new DataBean(20.0);
        kSession.insert(t);
        // 校验规则
        kSession.fireAllRules();
        assertFalse(t.getResult());
    }

    /**
     * eval(true) 永远为真
     */
    @Test
    public void test2() {
        KieSession kSession = createSession("rule2.drl");
        int n = kSession.fireAllRules();
        assertEquals(1, n);
    }


    /**
     * 多个条件组合： value < 10 && (value - 3 == 0)
     */
    @Test
    public void test3() {
        KieSession kSession = createSession("rule3.drl");
        DataBean data = new DataBean(3.0);
        kSession.insert(data);
        kSession.fireAllRules();
        assertTrue(data.getResult());
    }

    /**
     * 绑定内部变量 $data: DataBean(value < 10, $v: value)
     */
    @Test
    public void test4() {
        KieSession kSession = createSession("rule4.drl");
        DataBean data = new DataBean(7.1);
        kSession.insert(data);

        DataBean data1 = new DataBean(71.1);
        kSession.insert(data1);
        kSession.fireAllRules();
    }

    /**
     * 条件中变量来自于检测的成员
     */
    @Test
    public void test5() {
        KieSession kSession = createSession("rule5.drl");

        Person p = new Person();
        p.setLike("rice");

        Food food = new Food();
        food.setName("rice");

        DataBean data = new DataBean(0.0);

        kSession.insert(p);
        kSession.insert(food);
        kSession.insert(data);

        kSession.fireAllRules();

        assertEquals("nice", data.getTag());
    }

    /**
     * collect用法
     */
    @Test
    public void test6() {
        KieSession kSession = createSession("rule6.drl");
        Person p1 = new Person(18, "zhangsan");
        Person p2 = new Person(18, "lisi");
        Person p3 = new Person(18, "wangwu");
        Person p4 = new Person(19, "maoliu");
        Person p5 = new Person(20, "wuqi");

        kSession.insert(p1);
        kSession.insert(p2);
        kSession.insert(p3);
        kSession.insert(p4);
        kSession.insert(p5);

        DataBean data = new DataBean();
        kSession.insert(data);
        kSession.fireAllRules();

        assertEquals(Integer.valueOf(3), data.getCnt());
    }

    /**
     * accumulate用法
     */
    @Test
    public void test7() {
        KieSession kSession = createSession("rule7.drl");
        Person p1 = new Person(16, "zhangsan");
        Person p2 = new Person(17, "lisi");
        Person p3 = new Person(18, "wangwu");
        Person p4 = new Person(19, "maoliu");
        Person p5 = new Person(20, "wuqi");

        kSession.insert(p1);
        kSession.insert(p2);
        kSession.insert(p3);
        kSession.insert(p4);
        kSession.insert(p5);

        DataBean data = new DataBean();
        kSession.insert(data);
        kSession.fireAllRules();

    }
}
