package com.luckypeng.study.flink.sql.udf;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.types.Row;

import java.util.Iterator;

/**
 * 多对一函数测试
 * @author coalchan
 */
public class AggregationFunctionTest {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tableEnv = BatchTableEnvironment.create(env);
        tableEnv.registerFunction("wAvg", new WeightedAvg());
        String sql = "SELECT username, wAvg(points, level) AS avgPoints " +
                "FROM (VALUES ('Bob', 100, 8), ('Bob', 50, 2), ('Ben', 50, 8), ('Ben', 100, 2)) " +
                "AS userScores(username, points, level) " +
                "GROUP BY username";
        Table result = tableEnv.sqlQuery(sql);
        tableEnv.toDataSet(result, Row.class).print();
    }

    public static class WeightedAvgAccum {
        public int sum = 0;
        public int count = 0;
    }

    public static class WeightedAvg extends AggregateFunction<Integer, WeightedAvgAccum> {

        @Override
        public WeightedAvgAccum createAccumulator() {
            return new WeightedAvgAccum();
        }

        @Override
        public Integer getValue(WeightedAvgAccum acc) {
            if (acc.count == 0) {
                return null;
            } else {
                return acc.sum / acc.count;
            }
        }

        public void accumulate(WeightedAvgAccum acc, int iValue, int iWeight) {
            acc.sum += iValue * iWeight;
            acc.count += iWeight;
        }

        public void retract(WeightedAvgAccum acc, int iValue, int iWeight) {
            acc.sum -= iValue * iWeight;
            acc.count -= iWeight;
        }

        public void merge(WeightedAvgAccum acc, Iterable<WeightedAvgAccum> it) {
            Iterator<WeightedAvgAccum> iter = it.iterator();
            while (iter.hasNext()) {
                WeightedAvgAccum a = iter.next();
                acc.count += a.count;
                acc.sum += a.sum;
            }
        }

        public void resetAccumulator(WeightedAvgAccum acc) {
            acc.count = 0;
            acc.sum = 0;
        }
    }
}
