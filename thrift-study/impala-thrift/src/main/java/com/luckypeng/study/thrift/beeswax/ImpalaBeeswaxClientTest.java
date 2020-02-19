package com.luckypeng.study.thrift.beeswax;

import com.cloudera.beeswax.api.Query;
import com.cloudera.beeswax.api.QueryHandle;
import com.cloudera.beeswax.api.QueryState;
import com.cloudera.beeswax.api.Results;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.impala.thrift.ImpalaService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.List;
import java.util.Scanner;

/**
 * @author coalchan
 */
public class ImpalaBeeswaxClientTest {
    private static String HOST = "kuber01";
    private static int PORT = 21000;
    private static int TIMEOUT = 60;

    public static void main(String [] args) {
        Scanner sc = new Scanner(System.in);
        testThriftClient(sc);
        sc.close();
    }
    
    private static ImpalaService.Client getClient() throws Exception {
    	//open connection
        TTransport transport = new TSocket(HOST, PORT);
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        //connect to client
        ImpalaService.Client client = new ImpalaService.Client(protocol);
        client.PingImpalaService();
        return client;
    }
    
    protected static void testThriftClient(Scanner sc) {
    	ImpalaService.Client client = null;
    	try {
    		client = getClient();
    	} catch (Exception e) {
    		e.printStackTrace();
    		return;
    	}
    	String line = null;
    	
    	System.out.println(">>>>>>>>Input Statement Line<<<<<<<<<");
    	while((line = sc.nextLine()) != null) {
    		if(line.trim().equalsIgnoreCase("quit")) {
    			System.out.println("Bye!");
    			break;
    		}
    		try {
    			executeAndOutput(client, line);
    		} catch(Exception e) {
    			System.err.println("Failed to execute sql : " + line);
    			e.printStackTrace();
    		}
        	System.out.println(">>>>>>>>Input Statement Line<<<<<<<<<");
    	}
    }
    	
    private static void executeAndOutput(ImpalaService.Client client, String statement)
    		throws Exception {
        Query query = new Query();
        query.setQuery(statement); 
            
        QueryHandle handle = client.query(query);
		System.out.println("Submit query " + statement + ", Query Id : " + handle.getId());
	            
		QueryState queryState = null;
		long start = System.currentTimeMillis();
		while(true) {
	        queryState = client.get_state(handle);
	        if(queryState == QueryState.FINISHED){
	        	break;
	        }
	        if(queryState == QueryState.EXCEPTION){
	          	System.err.println("Query caused exception !");
	           	break;
	        }
	        if(System.currentTimeMillis() - start > TIMEOUT * 1000) {
	            client.Cancel(handle);
	        }
			
	        Thread.sleep(1000);
		}

        boolean done = false;
        while(queryState == QueryState.FINISHED && done == false) {
            List<FieldSchema> schema = client.get_results_metadata(handle).getSchema().getFieldSchemas();
            System.out.println(schema.toString());

            Results results = client.fetch(handle,false,100);
            
            List<String> data = results.data;
               
            for(int i=0;i<data.size();i++) {
                System.out.println(data.get(i));
            }

            if(results.has_more==false) {
                done = true;
            }

        }
    }
}