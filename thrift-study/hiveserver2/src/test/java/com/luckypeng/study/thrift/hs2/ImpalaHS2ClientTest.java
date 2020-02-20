package com.luckypeng.study.thrift.hs2;

import com.luckypeng.study.thrift.hs2.util.HandleIdentifier;
import com.luckypeng.study.thrift.hs2.util.Hiveserver2Helper;
import org.apache.hive.service.cli.thrift.THandleIdentifier;
import org.apache.hive.service.cli.thrift.TOperationHandle;
import org.apache.hive.service.cli.thrift.TOperationType;
import org.junit.Test;

import java.util.Scanner;
import java.util.UUID;

public class ImpalaHS2ClientTest {

    @Test
    public void testClient() {
        ImpalaHS2Client impalaHS2Client =
                new ImpalaHS2Client("kuber02", 21050, 60,"thrift-test", "");
        impalaHS2Client.testClient();
    }

    @Test
    public void testAsync() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Guid: ");
        String guid = scanner.nextLine();
        System.out.println("Secret: ");
        String secret = scanner.nextLine();

        ImpalaHS2Client impalaHS2Client =
                new ImpalaHS2Client("kuber02", 21050, 60,"thrift-test", "");
        ImpalaHS2Client.Hiverserver2 hiverserver2 = impalaHS2Client.getClient();

        THandleIdentifier operationId = new HandleIdentifier(UUID.fromString(guid), UUID.fromString(secret))
                .toTHandleIdentifier();
        TOperationHandle operationHandle = new TOperationHandle(operationId, TOperationType.EXECUTE_STATEMENT, true);

        System.out.println(Hiveserver2Helper.getQueryHandleStatus(hiverserver2.getClient(), operationHandle));
        Hiveserver2Helper.getResult(hiverserver2.getClient(), operationHandle, true, true);
    }
}