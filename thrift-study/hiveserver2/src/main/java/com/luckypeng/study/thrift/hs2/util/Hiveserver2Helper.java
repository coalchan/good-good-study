package com.luckypeng.study.thrift.hs2.util;

import org.apache.hive.service.cli.thrift.*;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import java.util.List;

/**
 * @author coalchan
 * @date 2020/02/20
 */
public class Hiveserver2Helper {
    private Hiveserver2Helper() {}

    public static TSessionHandle getSession(TCLIService.Client client, String userName) {
        TOpenSessionReq openReq = new TOpenSessionReq(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V1);
        openReq.setUsername(userName);
        TOpenSessionResp openResp = null;
        try {
            openResp = client.OpenSession(openReq);
        } catch (TException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("server protocol version is: " + openResp.getServerProtocolVersion());
        return openResp.getSessionHandle();
    }

    public static TExecuteStatementResp submitQuery(TCLIService.Client client, TSessionHandle session, String sql, int maxCacheSize)
            throws TException {
        TExecuteStatementReq execReq = new TExecuteStatementReq(session, sql);
        //设置异步
        execReq.setRunAsync(true);
        if (maxCacheSize > 0) {
            // 设置结果缓存，以便再次从头获取结果集
            execReq.putToConfOverlay("impala.resultset.cache.size", String.valueOf(maxCacheSize));
        }
        TExecuteStatementResp execResp = client.ExecuteStatement(execReq);
        return execResp;
    }

    public static Object getValue(TColumnValue field) {
        if (field.isSetStringVal()) {
            return field.getStringVal().getValue();
        } else if (field.isSetDoubleVal()) {
            return field.getDoubleVal().getValue();
        } else if (field.isSetI16Val()) {
            return field.getI16Val().getValue();
        } else if (field.isSetI32Val()) {
            return field.getI32Val().getValue();
        } else if (field.isSetI64Val()) {
            return field.getI64Val().getValue();
        } else if (field.isSetBoolVal()) {
            return field.getBoolVal().isValue();
        } else if (field.isSetByteVal()) {
            return field.getByteVal().getValue();
        } else {
            return field.getFieldValue();
        }
    }

    public static void getQueryLog(TCLIService.Client client, TOperationHandle tOperationHandle) throws Exception {
        TGetLogReq tGetLogReq = new TGetLogReq(tOperationHandle);
        TGetLogResp logResp = client.GetLog(tGetLogReq);
        String log = logResp.getLog();
        if (log != null) {
            System.out.println("GetLog: " + log);
        }
    }

    public static TOperationState getQueryHandleStatus(TCLIService.Client client, TOperationHandle tOperationHandle)
            throws Exception {
        TGetOperationStatusReq statusReq = new TGetOperationStatusReq(tOperationHandle);
        TGetOperationStatusResp statusResp = client.GetOperationStatus(statusReq);
        return statusResp.getOperationState();
    }

    public static void closeOperation(TCLIService.Client client, TOperationHandle tOperationHandle) throws TException {
        TCloseOperationReq closeReq = new TCloseOperationReq(tOperationHandle);
        client.CloseOperation(closeReq);
    }

    public static void closeSession(TCLIService.Client client, TSessionHandle session) {
        TCloseSessionReq closeConnectionReq = new TCloseSessionReq(session);
        try {
            client.CloseSession(closeConnectionReq);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void close(TTransport tTransport) {
        if (tTransport != null) {
            tTransport.close();
        }
    }

    public static TFetchResultsResp getResult(TCLIService.Client client,
                                              TOperationHandle tOperationHandle,
                                              boolean startOver,
                                              boolean cached,
                                              int maxCacheSize) throws TException {
        //获取列名
        TGetResultSetMetadataReq metadataReq = new TGetResultSetMetadataReq(tOperationHandle);
        TGetResultSetMetadataResp metadataResp = client.GetResultSetMetadata(metadataReq);
        List<TColumnDesc> columns = metadataResp.getSchema().getColumns();
        columns.forEach(column -> System.out.print(column.getColumnName() + "\t"));
        System.out.println();

        TFetchOrientation orientation = TFetchOrientation.FETCH_NEXT;
        if (startOver && cached) {
            // 如果设置了缓存，从头开始查询的话，这里需要设置下
            orientation = TFetchOrientation.FETCH_FIRST;
        }

        //获取数据
        TFetchResultsReq fetchReq = new TFetchResultsReq(tOperationHandle, orientation, maxCacheSize);

        TFetchResultsResp resultsResp = client.FetchResults(fetchReq);
        TStatus status = resultsResp.getStatus();
        if (status.getStatusCode() == TStatusCode.ERROR_STATUS) {
            String msg = status.getErrorMessage();
            System.out.println(msg + "," + status.getSqlState() + "," + status.getErrorCode() + "," + status.isSetInfoMessages());
        }
        TRowSet resultsSet = resultsResp.getResults();
        List<TRow> resultRows = resultsSet.getRows();

        for (TRow resultRow : resultRows) {
            List<TColumnValue> rows = resultRow.getColVals();
            rows.forEach(row -> System.out.print(Hiveserver2Helper.getValue(row) + "\t"));
            System.out.println();
        }

        return resultsResp;
    }
}
