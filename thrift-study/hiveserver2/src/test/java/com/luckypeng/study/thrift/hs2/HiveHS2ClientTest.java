package com.luckypeng.study.thrift.hs2;

import com.luckypeng.study.thrift.hs2.util.HandleIdentifier;
import com.luckypeng.study.thrift.hs2.util.Hiveserver2Helper;
import org.apache.hive.service.cli.thrift.THandleIdentifier;
import org.apache.hive.service.cli.thrift.TOperationHandle;
import org.apache.hive.service.cli.thrift.TOperationType;
import org.junit.Test;

import java.util.Scanner;
import java.util.UUID;

public class HiveHS2ClientTest {
    @Test
    public void testClient() {
        HiveHS2Client hiveHS2ClientTest =
                new HiveHS2Client("kuber02", 10000, 6000,"thrift-test", "123");
        hiveHS2ClientTest.testClient();
    }

    @Test
    public void testAsync() throws Exception {
        HiveHS2Client hiveHS2Client =
                new HiveHS2Client("kuber02", 10000, 360,"thrift-test", "123");
        ImpalaHS2Client.Hiverserver2 hiverserver2 = hiveHS2Client.getClient();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Guid: ");
        String guid = scanner.nextLine();
        System.out.println("Secret: ");
        String secret = scanner.nextLine();

        THandleIdentifier operationId = new HandleIdentifier(UUID.fromString(guid), UUID.fromString(secret))
                .toTHandleIdentifier();
        TOperationHandle operationHandle = new TOperationHandle(operationId, TOperationType.EXECUTE_STATEMENT, true);

        System.out.println(Hiveserver2Helper.getQueryHandleStatus(hiverserver2.getClient(), operationHandle));
        Hiveserver2Helper.getResult(hiverserver2.getClient(), operationHandle, true, true, hiveHS2Client.maxCacheSize);
    }
}