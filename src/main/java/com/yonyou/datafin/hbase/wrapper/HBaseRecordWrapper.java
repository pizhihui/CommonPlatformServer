package com.yonyou.datafin.hbase.wrapper;

import java.io.Serializable;
import java.util.Map;

/**
 * 封装列族数据
 * @author jiwenlong
 * 2017-06-19
 */
public class HBaseRecordWrapper implements Serializable{
	private static final long serialVersionUID = 1L;
	// rowkey的值
	private String rowkey;
	//列族名称
	private String familyName;
	//列族下的<列名,值>
	private Map<String, Object> data;
	
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public String getRowkey() {
		return rowkey;
	}
	public void setRowkey(String rowkey) {
		this.rowkey = rowkey;
	}
	public HBaseRecordWrapper(String rowkey, String familyName, Map<String, Object> data) {
		super();
		this.rowkey = rowkey;
		this.familyName = familyName;
		this.data = data;
	}
	
	public HBaseRecordWrapper(Map<String, Object> data) {
		this.data = data;
	}
}
