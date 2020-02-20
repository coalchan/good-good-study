package com.luckypeng.study.thrift.hs2;

import com.luckypeng.study.thrift.hs2.util.Hiveserver2Helper;
import com.luckypeng.study.thrift.hs2.util.PlainSaslHelper;
import org.apache.hive.service.cli.thrift.TCLIService;
import org.apache.hive.service.cli.thrift.TFetchResultsResp;
import org.apache.hive.service.cli.thrift.TOperationHandle;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import javax.security.sasl.SaslException;

/**
 * Impala HiveServer2 方式连接
 * @author coalchan
 * @date 2020/02/19
 */
public class HiveHS2Client extends ImpalaHS2Client {
    private static int MAX_RESULT_CACHE_SIZE = 1000;

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
    public boolean hasMore(TCLIService.Client client,
                           TOperationHandle tOperationHandle,
                           boolean startOver,
                           boolean cached) throws TException {
        TFetchResultsResp resultsResp = Hiveserver2Helper.getResult(client, tOperationHandle, startOver, cached);
        return resultsResp.getResults().getRows().size() < MAX_RESULT_CACHE_SIZE;
    }
}
