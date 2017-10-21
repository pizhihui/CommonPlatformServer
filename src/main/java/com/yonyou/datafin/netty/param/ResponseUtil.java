package com.yonyou.datafin.netty.param;

public class ResponseUtil {
	
	public static ResponseParam createSuccessResult(){
		return new ResponseParam();
	}

	public static ResponseParam createSuccessResult(Object result){
		ResponseParam response = new ResponseParam();
		response.setResult(result);
		return response;
	}

	public static ResponseParam createFailResult(String code,String msg){
		ResponseParam response = new ResponseParam();
		response.setCode(code);
		response.setMsg(msg);
		return response;
	}
}
