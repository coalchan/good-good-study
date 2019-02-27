package com.luckypeng.study.basic;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.Test;

/**
 * 计算最大值（字符串比较）
 * 时间特征: 处理时间 {@link org.apache.flink.streaming.api.TimeCharacteristic.ProcessingTime}
 *
 * 要求安装NetCat
 * linux 和 Mac 上是 nc 命令: nc -kl 12348
 * windows 可以从 http://nmap.org/dist/ncat-portable-5.59BETA1.zip 下载解压使用 ncat.exe 命令: ncat -kl 12348
 * @author chenzhipeng
 * @date 2019/2/27 10:49
 */
public class ProcessingTimeWindowTest {
    /**
     * 窗口类型: 滚动时间窗口
     * @throws Exception
     */
    @Test
    public void tumblingTimeWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream.timeWindowAll(Time.seconds(10)).max(0).print();
        env.execute();
    }

    /**
     * 窗口类型: 滚动计数窗口
     * @throws Exception
     */
    @Test
    public void tumblingCountWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream.countWindowAll(3).max(0).print();
        env.execute();
    }

    /**
     * 窗口类型: 滑动时间窗口
     * @throws Exception
     */
    @Test
    public void slidingTimeWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream.timeWindowAll(Time.seconds(10), Time.seconds(3)).max(0).print();
        env.execute();
    }

    /**
     * 窗口类型: 滑动计数窗口
     * @throws Exception
     */
    @Test
    public void slidingCountWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream.countWindowAll(10, 3).max(0).print();
        env.execute();
    }
}
