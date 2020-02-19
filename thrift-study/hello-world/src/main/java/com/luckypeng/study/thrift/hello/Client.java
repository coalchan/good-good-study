package com.luckypeng.study.thrift.hello;

import com.luckypeng.study.thrift.hello.generated.HelloWorldService1;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * @author coalchan
 * @date 2020/02/18
 */
public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final int TIMEOUT = 30000;

    /**
     *
     * @param userName
     */
    public void startClient(String userName) {
        TTransport transport = null;
        try {
            transport = new TSocket(SERVER_IP, SERVER_PORT, TIMEOUT);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            HelloWorldService1.Client client = new HelloWorldService1.Client(protocol);
            String result = client.sayHello(userName);
            System.out.println("thrift remote call : " + result);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            if (null != transport) {
                transport.close();
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Client client = new Client();
        client.startClient("how-are-you");
    }
}
