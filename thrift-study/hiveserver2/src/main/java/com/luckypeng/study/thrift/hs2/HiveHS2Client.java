package com.luckypeng.study.thrift.hs2;

import com.luckypeng.study.thrift.hs2.util.Hiveserver2Helper;
import com.luckypeng.study.thrift.hs2.util.PlainSaslHelper;
import org.apache.hive.service.cli.thrift.*;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import javax.security.sasl.SaslException;
import java.util.HashMap;
import java.util.Map;

/**
 * Impala HiveServer2 方式连接
 * @author coalchan
 * @date 2020/02/19
 */
public class HiveHS2Client extends ImpalaHS2Client {
    public HiveHS2Client(String host, int port, int timeout, String userName, String password) {
        super(host, port, timeout, userName, password);
    }

    @Override
    public TTransport getTransport(String host, int port, int timeout) {
        TSocket transport = new TSocket(host, port, timeout);

        try {
            return PlainSaslHelper.getPlainTransport(userName, password, transport);
        } catch (SaslException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getSessionConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("hive.server2.proxy.user", userName);
        return config;
    }

    @Override
    public void getQueryLog(TCLIService.Client client, TOperationHandle tOperationHandle) throws Exception {
        TFetchResultsReq fetchReq = new TFetchResultsReq(tOperationHandle, TFetchOrientation.FETCH_NEXT, maxCacheSize);
        fetchReq.setFetchType((short)1);
        Hiveserver2Helper.fetchResults(client, fetchReq);
    }

    @Override
    public boolean hasMore(TCLIService.Client client,
                           TOperationHandle tOperationHandle,
                           boolean startOver,
                           boolean cached) throws TException {
        TFetchResultsResp resultsResp = Hiveserver2Helper.getResult(client, tOperationHandle, startOver, cached, maxCacheSize);
        return !resultsResp.getResults().getRows().isEmpty() &&
                resultsResp.getResults().getRows().size() >= maxCacheSize ;
    }
}
