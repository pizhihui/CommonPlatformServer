package com.yonyou.datafin.netty.param;

public class ResponseParam {
	
	private String code="00000";
	
	private Object result;
	
	private String msg;

	
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
	
	

}
