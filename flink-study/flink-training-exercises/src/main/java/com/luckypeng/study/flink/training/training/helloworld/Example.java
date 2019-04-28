package com.luckypeng.study.flink.training.training.helloworld;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author chenzhipeng
 * @date 2019/2/21 10:20
 */
public class Example {
    public static void main(String[] args) throws Exception {
        // Flink 执行环境，会创建job的 DAG
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // data source: 从数据源构造数据流，一般来自文件、网络、数据库、Kafka等，这里使用 fromElements 利用几个元素产生作为示例
        DataStream dataStream = env.fromElements(
                new Person("zhangsan", 12),
                new Person("lisi", 25),
                new Person("wangwu", 19)
        );

        // 执行过滤的操作（transform）
        DataStream adults = dataStream.filter((FilterFunction<Person>) person -> person.getAge() >= 18);

        // data sink: 打印数据，这里会调用 toString() 方法进行输出，“>”前的数字表示子任务线程的序号
        // 也可以利用 writeAsText, writeToSocket 等方法将结果写入文件或者网络当中
        adults.print();

        // 将 DAG 发送给 Job Manager，它会将任务发送给 Task Manager 并执行
        env.execute();
    }

    @Data
    @AllArgsConstructor
    public static class Person {
        private String name;
        private int age;
    }
}
