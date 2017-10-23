package com.yonyou.datafin.netty.param;

public class ResponseParam {
    /** 返回的状态码 */
	private String code = "200";
	/** 返回的结果 */
	private Object result;
	/** 返回的信息 */
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
