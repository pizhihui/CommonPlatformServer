package com.yonyou.datafin.hbase.wrapper;

import java.io.Serializable;

/**
 * 值封装类
 * @author jiwenlong
 * 2017-06-19
 */
public class ValueWrapper implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String value;
	
	private Long ts;
	
	public ValueWrapper(String value) {
		super();
		this.value = value;
	}

	public ValueWrapper(String value, Long ts) {
		super();
		this.value = value;
		this.ts = ts;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getTs() {
		return ts;
	}

	public void setTs(Long ts) {
		this.ts = ts;
	}
	
	@Override
	public String toString() {
		return getValue()+"---" + getTs();
	}
	

}
