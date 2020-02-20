package com.luckypeng.study.thrift.hs2;

import com.luckypeng.study.thrift.hs2.util.HandleIdentifier;
import com.luckypeng.study.thrift.hs2.util.Hiveserver2Helper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.hive.service.cli.thrift.*;
import org.apache.impala.thrift.ImpalaHiveServer2Service;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.Scanner;

/**
 * Impala HiveServer2 方式连接
 * @author coalchan
 * @date 2020/02/19
 */
public class ImpalaHS2Client {
    private static int MAX_RESULT_CACHE_SIZE = 1000;

    protected final String host;
    protected final int port;
    protected final int timeout;
    protected final String userName;
    protected final String password;

    public ImpalaHS2Client(String host, int port, int timeout, String userName, String password) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.userName = userName;
        this.password = password;
    }

    public void testClient() {
        Hiverserver2 hiverserver2 = getClient();
        TSessionHandle session = Hiveserver2Helper.getSession(hiverserver2.getClient(), userName);

        Scanner sc = new Scanner(System.in);
        String line;
        System.out.println(">>>>>>>>Input Statement Line or enter `quit` to exit<<<<<<<<<\n");
        while((line = sc.nextLine()) != null) {
            if(line.trim().equalsIgnoreCase("quit")) {
                System.out.println("Bye!");
                break;
            }
            try {
                executeQuery(hiverserver2.getClient(), session, line);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(">>>>>>>>Input Statement Line or enter `quit` to exit<<<<<<<<<\n");
        }

        Hiveserver2Helper.closeSession(hiverserver2.getClient(), session);
        Hiveserver2Helper.close(hiverserver2.getTTransport());
    }

    public Hiverserver2 getClient() {
        TTransport transport = getTransport(host, port, timeout);
        try {
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        TBinaryProtocol protocol = new TBinaryProtocol(transport);
        TCLIService.Client client = new ImpalaHiveServer2Service.Client(protocol);

        return new Hiverserver2(transport, client);
    }

    public TTransport getTransport(String host, int port, int timeout) {
        return new TSocket(host, port, timeout);
    }

    public void executeQuery(TCLIService.Client client, TSessionHandle session, String sql) throws Exception {
        TExecuteStatementResp execResp = Hiveserver2Helper.submitQuery(client, session, sql, MAX_RESULT_CACHE_SIZE);

        TStatus status = execResp.getStatus();
        if (status.getStatusCode() == TStatusCode.ERROR_STATUS ||
                status.getStatusCode() == TStatusCode.INVALID_HANDLE_STATUS) {
            String msg = status.getErrorMessage();
            System.out.println("query error: " + msg);
            return;
        }

        TOperationHandle tOperationHandle = execResp.getOperationHandle();

        TOperationState queryHandleStatus;
        while (true) {
            queryHandleStatus = Hiveserver2Helper.getQueryHandleStatus(client, tOperationHandle);
            if (queryHandleStatus == TOperationState.PENDING_STATE) {
                System.out.println("pending...");
            } else if (queryHandleStatus == TOperationState.RUNNING_STATE) {
                Hiveserver2Helper.getQueryLog(client, tOperationHandle);
            } else if (queryHandleStatus == TOperationState.FINISHED_STATE) {
                break;
            } else if (queryHandleStatus == TOperationState.ERROR_STATE) {
                System.out.println("Query caused exception !");
                break;
            }
            Thread.sleep(100);
        }

        HandleIdentifier handleIdentifier = new HandleIdentifier(tOperationHandle.getOperationId());
        System.out.println("queryId is: " + handleIdentifier.getPublicId() + ":" + handleIdentifier.getSecretId());

        boolean done = false;
        boolean startOver = true;
        boolean cached = false;
        if (MAX_RESULT_CACHE_SIZE > 0) {
            cached = true;
        }

        while (queryHandleStatus == TOperationState.FINISHED_STATE && !done) {
            done = !hasMore(client, tOperationHandle, startOver, cached);
            if (startOver) {
                startOver = false;
            }
        }

        Hiveserver2Helper.closeOperation(client, tOperationHandle);
    }

    public boolean hasMore(TCLIService.Client client,
                            TOperationHandle tOperationHandle,
                            boolean startOver,
                            boolean cached) throws TException {
        TFetchResultsResp resultsResp = Hiveserver2Helper.getResult(client, tOperationHandle, startOver, cached);
        return resultsResp.hasMoreRows;
    }

    @Data
    @AllArgsConstructor
    public static class Hiverserver2 {
        private TTransport tTransport;
        private TCLIService.Client client;
    }

}
