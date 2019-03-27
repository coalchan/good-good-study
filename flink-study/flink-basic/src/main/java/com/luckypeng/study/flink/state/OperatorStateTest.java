package com.luckypeng.study.flink.state;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.OperatorStateStore;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

/**
 * run 方法中抛出异常后，会执行 initializeState 方法，
 * 读取最近的状态（由 snapshotState 定期（checkpointConfig.setCheckpointInterval）写入），
 * 然后重新执行 run 方法。
 * @author coalchan
 * @date 2019/3/27 15:18
 */
public class OperatorStateTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream dataStream = env.addSource(new OperatorStateFunction());
        dataStream.print();
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setCheckpointInterval(10000);
        env.execute();
    }
}

class OperatorStateFunction implements SourceFunction<Integer>, CheckpointedFunction {
    private volatile boolean isRunning = true;

    private transient ListState<Integer> numState;
    private static Integer num = 0;

    private static Random RANDOM = new Random();

    @Override
    public void cancel() {
        isRunning = false;
    }

    @Override
    public void run(SourceFunction.SourceContext<Integer> ctx) {
        System.out.println("start in run...");
        while (isRunning) {
            num++;
            ctx.collect(num);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (RANDOM.nextDouble() < 0.3) {
                System.out.println("eeeeeeerror...");
                throw new RuntimeException("run error!");
            }
        }
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        System.out.println("save state: " + num);
        this.numState.clear();
        this.numState.add(num);
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        OperatorStateStore stateStore = context.getOperatorStateStore();
        ListStateDescriptor<Integer> descriptor = new ListStateDescriptor<>("currentNum", Integer.class);
        this.numState = stateStore.getListState(descriptor);

        if (context.isRestored()) {
            int j = 1;
            for (Integer i : this.numState.get()) {
                System.out.println("state-" + j + ": " + i);
                num = i;
                j++;
            }
            System.out.println("read from state: " + num);
        }
    }
}