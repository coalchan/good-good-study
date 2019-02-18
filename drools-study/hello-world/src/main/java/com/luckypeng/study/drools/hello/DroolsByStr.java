package com.luckypeng.study.drools.hello;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.util.DroolsStreamUtils;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

/**
 * 从字符串中读取规则，可以不依赖于kmodule.xml文件，比较灵活
 * @author chenzhipeng
 * @date 2018/8/16 18:29
 */
public class DroolsByStr {
    public static void main(String[] args) {
        String rule = "package drools\n" +
                "import com.luckypeng.study.drools.hello.Applicant\n" +
                "\n" +
                "rule \"Is of valid age\"\n" +
                "when\n" +
                "    $a : Applicant( age < 18 )\n" +
                "then\n" +
                "    $a.setValid( false );\n" +
                "end";

        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        Resource resource = ResourceFactory.newByteArrayResource(rule.getBytes());

        kBuilder.add(resource, ResourceType.DRL);

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();

        kBase.addPackages(kBuilder.getKnowledgePackages());

        KieSession kSession = kBase.newKieSession();

        Applicant obj = new Applicant("Zhang San", 16, true);

        System.out.println(obj.isValid());

        kSession.insert(obj);

        // 校验规则
        kSession.fireAllRules();

        System.out.println(obj.isValid());
    }
}
