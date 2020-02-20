package com.luckypeng.study.thrift.beeswax;

import org.apache.hive.service.cli.thrift.THandleIdentifier;
import org.apache.hive.service.cli.thrift.TOperationHandle;
import org.apache.hive.service.cli.thrift.TOperationType;

import java.util.UUID;

/**
 * @author coalchan
 * @date 2020/02/20
 */
public class ImpalaHS2AsyncDo {
    private static final String GUID = "adc22919-105d-4940-0000-000058496ed6";
    private static final String SECRET = "adc22919-105d-4940-0000-000058496ed6";
    public static void main(String[] args) throws Exception {
        ImpalaHS2ClientTest.getClient();

        THandleIdentifier operationId = new HandleIdentifier(UUID.fromString(GUID), UUID.fromString(SECRET))
                .toTHandleIdentifier();
        TOperationHandle operationHandle = new TOperationHandle(operationId, TOperationType.EXECUTE_STATEMENT, true);

        System.out.println(ImpalaHS2ClientTest.getQueryHandleStatus(operationHandle));
        ImpalaHS2ClientTest.getResult(operationHandle, true, true);
    }
}
