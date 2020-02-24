package com.luckypeng.study.pool2.hello;

import lombok.Getter;

import java.io.Closeable;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @author coalchan
 */
public class MyConnection implements Closeable {
    @Getter
    private boolean open;

    @Getter
    private String connectionId;

    public MyConnection() {
        connectionId = UUID.randomUUID().toString();
        open = true;
        // after some time, close connection
        scheduleClose(30 * 1000);
        System.out.println("My connection " + connectionId + " created!");
    }

    public MyConnection(String connectionId) {
        this.connectionId = connectionId;
        open = true;
        // after some time, close connection
        scheduleClose(30 * 1000);
        System.out.println("My connection " + connectionId + " created!");
    }

    @Override
    public void close() throws IOException {
        System.out.println("My connection closed");
    }

    public String request(String msg) {
        return "hello, " + msg;
    }

    public void scheduleClose(long delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                open = false;
            }
        }, delay);
    }
}
