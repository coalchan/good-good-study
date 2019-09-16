package com.luckypeng.study.avatica.hello;


import org.apache.calcite.avatica.*;
import org.apache.calcite.avatica.remote.MockJsonService;
import org.apache.calcite.avatica.remote.Service;
import org.apache.calcite.avatica.server.AvaticaJsonHandler;
import org.apache.calcite.avatica.server.HttpServer;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.mockito.Answers;
import org.mockito.Mockito;

import java.util.Properties;

/**
 * @author coalchan
 */
public class Test {
    protected static final class TestDriver extends UnregisteredDriver {

        @Override protected DriverVersion createDriverVersion() {
            return new DriverVersion("test", "test 0.0.0", "test", "test 0.0.0", false, 0, 0, 0, 0);
        }

        @Override protected String getConnectStringPrefix() {
            return "jdbc:test";
        }

        @Override public Meta createMeta(AvaticaConnection connection) {
            return Mockito.mock(Meta.class, Answers.RETURNS_DEEP_STUBS);
        }

        @Override public AvaticaFactory createFactory() {
            return super.createFactory();
        }
    }

    public static void main(String[] args) {
        TestDriver driver = new TestDriver();
        AvaticaConnection connection = new AvaticaConnection(driver, driver.createFactory(),
                "jdbc:avatica", new Properties()) {
        };
        Service service = new MockJsonService.Factory().create(connection);

        AvaticaJsonHandler handler = new AvaticaJsonHandler(service);

        HttpServer server = new HttpServer(8282, handler);
        System.out.println("Started Avatica server on port: " + server.getPort());
        server.start();
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
