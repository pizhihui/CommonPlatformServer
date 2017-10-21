package com.yonyou.datafin.hbase.wrapper;

import java.io.Serializable;

/**
 * 键值对封装类
 * @author jiwenlong
 * 2017-06-19
 */
public class KeyValueWrapper implements Serializable{

	private static final long serialVersionUID = 1L;
	private String key;
	private String value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public KeyValueWrapper(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
	
}
