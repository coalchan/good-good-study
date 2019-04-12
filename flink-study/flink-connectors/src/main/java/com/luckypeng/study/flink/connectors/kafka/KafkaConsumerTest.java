package com.luckypeng.study.flink.connectors.kafka;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.junit.Test;

import java.util.Properties;

/**
 * @author coalchan
 * @date 2019/4/12 11:10
 */
public class KafkaConsumerTest {
    /**
     * 如果连接 Kafka 异常，可以将日志级别置为 DEBUG 以便定位问题
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties prop = new Properties();
        prop.put("bootstrap.servers", "172.17.8.228:9092");
        prop.put("group.id", "test-consumer-group-1");
        String topic = "test";

        FlinkKafkaConsumer011<String> consumer = new FlinkKafkaConsumer011(topic, new SimpleStringSchema(), prop);
        DataStreamSource<String> dataStreamSource = env.addSource(consumer);
        dataStreamSource.print();
        env.execute();
    }
}
