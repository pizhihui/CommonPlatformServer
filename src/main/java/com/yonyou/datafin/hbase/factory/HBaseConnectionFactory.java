package com.yonyou.datafin.hbase.factory;

import com.yonyou.datafin.framework.SpringPropertiesUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Hbase连接的工厂类
 * @author jiwenlong
 * 2017-06-19
 */
public class HBaseConnectionFactory {

    static Logger logger = LoggerFactory.getLogger(HBaseConnectionFactory.class);

	private static Connection conn = null;
	public static final String ZOOKEEPER_QUORUM_KEY = "hbase.zookeeper.quorum";
	public static final String ZOOKEEPER_PORT_KEY = "hbase.zookeeper.property.clientPort";
	private static Configuration conf;
	
	static{
		conf = new Configuration();
        String property = SpringPropertiesUtil.getProperty(ZOOKEEPER_QUORUM_KEY);
        logger.error("hbase connection is: {}", property);
        conf.set(ZOOKEEPER_QUORUM_KEY, property);
		conf.setInt(ZOOKEEPER_PORT_KEY, Integer.valueOf(SpringPropertiesUtil.getProperty(ZOOKEEPER_PORT_KEY)));
	}

	public static synchronized Connection getConnection() throws IOException {
		if (conn == null || conn.isClosed() || (conn.isAborted())) {
			conn = ConnectionFactory.createConnection(conf);
		}
		return conn;
	}

}
