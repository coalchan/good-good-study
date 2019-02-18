package com.luckypeng.study.drools.hello;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * 从文件中读取规则，依赖于kmodule.xoml、hello.drl规则文件
 * @author chenzhipeng
 * @date 2018/8/16 17:59
 */
public class DroolsByFile {
    public static void main(String[] args) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();

        KieSession kSession = kContainer.newKieSession("hello-World");

        Applicant obj = new Applicant("Zhang San", 16, true);

        System.out.println(obj.isValid());

        kSession.insert(obj);

        // 校验规则
        kSession.fireAllRules();

        System.out.println(obj.isValid());
    }
}
