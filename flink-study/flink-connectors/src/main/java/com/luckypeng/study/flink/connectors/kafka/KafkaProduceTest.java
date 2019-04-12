package com.luckypeng.study.flink.connectors.kafka;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.junit.Test;

import java.util.Properties;

/**
 * @author coalchan
 * @date 2019/4/12 13:44
 */
public class KafkaProduceTest {
    /**
     * 同时开启消费者进行观察：bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties prop = new Properties();
        prop.put("bootstrap.servers", "172.17.8.228:9092");
        String topic = "test";

        DataStreamSource<String> dataStreamSource = env.fromElements("a1", "a2", "a3");
        FlinkKafkaProducer011 producer = new FlinkKafkaProducer011(topic, new SimpleStringSchema(), prop);
        dataStreamSource.addSink(producer);

        env.execute();
    }
}
