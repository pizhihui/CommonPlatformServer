package com.yonyou.datafin.hbase.wrapper;

import java.io.Serializable;
import java.util.Map;

/**
 * Hbase的封装类
 * @author jiwenlong
 * 2017-06-19
 */
public class HbaseWrapper implements Serializable{

	private static final long serialVersionUID = 1L;
	private String tableName;
	private String rowKey;
	private String family;
	private String type;
	private Map<String,Object> typeFamily;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getRowKey() {
		return rowKey;
	}
	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public Map<String, Object> getTypeFamily() {
		return typeFamily;
	}
	public void setTypeFamily(Map<String, Object> typeFamily) {
		this.typeFamily = typeFamily;
	}
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public HbaseWrapper(String tableName, String rowKey, String family, Map<String, Object> typeFamily) {
		super();
		this.tableName = tableName;
		this.rowKey = rowKey;
		this.family = family;
		this.typeFamily = typeFamily;
	}
	
}
