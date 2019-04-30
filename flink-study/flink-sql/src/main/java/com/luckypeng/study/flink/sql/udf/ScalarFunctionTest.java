package com.luckypeng.study.flink.sql.udf;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.types.Row;

/**
 * 一对一函数测试
 * @author coalchan
 */
public class ScalarFunctionTest {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tableEnv = BatchTableEnvironment.create(env);
        tableEnv.registerFunction("hashCode", new HashCode(10));
        String sql = "SELECT name, hashCode(name) FROM (VALUES ('Bob'), ('Alice'), ('Greg')) AS NameTable(name)";
        Table result = tableEnv.sqlQuery(sql);
        tableEnv.toDataSet(result, Row.class).print();
    }

    public static class HashCode extends ScalarFunction {
        private int factor = 12;

        public HashCode(int factor) {
            this.factor = factor;
        }

        public int eval(String s) {
            return s.hashCode() * factor;
        }
    }
}

