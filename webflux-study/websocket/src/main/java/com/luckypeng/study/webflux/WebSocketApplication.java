/*
 * 运行应用之后，可以使用工具来测试该 WebSocket 服务。
 * 打开工具页面 https://www.websocket.org/echo.html，
 * 然后连接到 ws://localhost:8080/echo，可以发送消息并查看服务器端返回的结果。
 */
package com.luckypeng.study.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenzhipeng
 */
@SpringBootApplication
public class WebSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }
}
