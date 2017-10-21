package com.yonyou.datafin.hbase.wrapper;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * HBaseTable的封装类
 * @author jiwenlong
 * 2017-06-19
 */
public class HBaseTableWrapper implements Closeable{
	private final Table tab;
	private BufferedMutator mutator = null;
	private boolean autoFlush = true;
	private final Connection conn;
	private long writeBufferSize;
	private Logger logger = LogManager.getLogger(HBaseTableWrapper.class);

	public HBaseTableWrapper(Connection conn, String tableName) throws IOException {
		this(conn, tableName, -1);
	}
	
	public HBaseTableWrapper(Connection conn, String tableName, long writeBufferSize) throws IOException {
		this.conn = conn;
		this.tab = conn.getTable(TableName.valueOf(tableName));
		this.writeBufferSize = writeBufferSize;
		
	}

	private synchronized BufferedMutator getBufferedMutator() throws IOException {
		if (mutator == null) {
			final BufferedMutator.ExceptionListener listener = new BufferedMutator.ExceptionListener() {  
		            @Override  
		            public void onException(RetriesExhaustedWithDetailsException e, BufferedMutator mutator) {  
		                for (int i = 0; i < e.getNumExceptions(); i++) {  
		                	logger.error("Failed to sent put " + e.getRow(i) + ".");  
		                }  
		            }  
		       }; 
			BufferedMutatorParams bmParams = new BufferedMutatorParams(tab.getName());
			bmParams.listener(listener);
			if (writeBufferSize > 0){
				bmParams.writeBufferSize(writeBufferSize);// tab.setWriteBufferSize( bufferSize ); Deprecated
			}
			mutator = conn.getBufferedMutator(bmParams);
		}
		return mutator;
	}

	public void setAutoFlush(boolean autoFlush) throws IOException {
		this.autoFlush = autoFlush;
	}

	public boolean isAutoFlush() throws IOException {
		return autoFlush;
	}

	public void flushCommits() throws IOException {
		getBufferedMutator().flush();
	}

	public void put(HBasePutWrapper putWrapper) throws IOException {
		if (putWrapper == null) {
			throw new NullPointerException("NULL Put passed");
		}
		getBufferedMutator().mutate(putWrapper.getPut());
		if (autoFlush) {
			getBufferedMutator().flush();
		}
	}
	
	public void delete(Delete toDel) throws IOException {
		getBufferedMutator().mutate(toDel);
		if (autoFlush) {
			getBufferedMutator().flush();
		}
	}

	public void delete(List<Delete> toDelLst) throws IOException {
		getBufferedMutator().mutate(toDelLst);
		if (autoFlush) {
			getBufferedMutator().flush();
		}
	}
	
	public ResultScanner getResultScanner(Scan s) throws IOException {
		return tab.getScanner(s);
	}

	public Result get(Get toGet) throws IOException {
		return tab.get(toGet);
	}
	
	public void close() throws IOException {
		tab.close();
		if (mutator != null) {
			mutator.close();
		}
	}
}
