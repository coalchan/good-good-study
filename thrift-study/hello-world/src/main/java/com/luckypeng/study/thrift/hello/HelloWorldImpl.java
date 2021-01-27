package com.luckypeng.study.thrift.hello;

import com.luckypeng.study.thrift.hello.generated.HelloWorldService;
import org.apache.thrift.TException;

/**
 * @author coalchan
 * @date 2020/02/18
 */
public class HelloWorldImpl implements HelloWorldService.Iface {
    @Override
    public String sayHello(String name) throws TException {
        System.out.println("received name: " + name);
        return "hello, " + name;
    }
}
