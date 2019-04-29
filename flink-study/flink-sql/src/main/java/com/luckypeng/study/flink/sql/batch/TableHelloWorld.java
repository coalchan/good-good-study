package com.luckypeng.study.flink.sql.batch;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.types.Row;
import org.junit.Test;

/**
 * @author coalchan
 */
public class TableHelloWorld {
    /**
     * 从 DataSet 构建 Table，将 Table 转换为 DataSet
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment  tableEnv = BatchTableEnvironment.create(env);

        DataSource<String> dataStreamSource = env.fromElements("hello", "hello", "world", "table", "sql");

        // 构建 Table, 这样写的话，tableEnv 也会在内部注册一个表名（具体可以参考源码）
        Table table = tableEnv.fromDataSet(dataStreamSource, "word");

        // 使用 Table API 计算
        Table count = table.groupBy("word").select("word, count(1) as cn");

        /*
        这里也可以将 Table 显式注册一个表名，便于以后使用：
        tableEnv.registerDataSet("test", dataStreamSource, "word");
        Table count = tableEnv.scan("test").groupBy("word").select("word,count(1) as cn");
        */

        // 转换为 DataSet
        tableEnv.toDataSet(count, Row.class).print();

        /*
        查看语法优化过程
        String explanation = tableEnv.explain(count);
        System.out.println(explanation);
        */
    }
}
