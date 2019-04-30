package com.luckypeng.study.flink.sql.udf;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.types.Row;

/**
 * 一对多函数测试
 * @author coalchan
 */
public class TableFunctionTest {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tableEnv = BatchTableEnvironment.create(env);
        tableEnv.registerFunction("split", new Split("#"));
        String sql = "SELECT a, word, length " +
                "FROM (VALUES ('Bob#Jack#Mary'), ('Alice#Bob'), ('Greg#Ben')) AS MyTable(a)" +
                ", LATERAL TABLE(split(a)) as T(word, length)";
        /*
        // 以下 SQL 结果等同于上面的 SQL
        sql = "SELECT a, word, length " +
                "FROM (VALUES ('Bob#Jack#Mary'), ('Alice#Bob'), ('Greg#Ben')) AS MyTable(a) " +
                "LEFT JOIN LATERAL TABLE(split(a)) as T(word, length) ON TRUE";
        */
        Table result = tableEnv.sqlQuery(sql);
        tableEnv.toDataSet(result, Row.class).print();
    }

    public static class Split extends TableFunction<Tuple2<String, Integer>> {
        private String separator = " ";

        public Split(String separator) {
            this.separator = separator;
        }

        public void eval(String str) {
            for (String s : str.split(separator)) {
                collect(new Tuple2<String, Integer>(s, s.length()));
            }
        }
    }
}
