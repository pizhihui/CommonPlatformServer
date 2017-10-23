package com.yonyou.datafin.netty.param;

public class ResponseUtil {
	

    public static ResponseParam createSuccessResult(){
        ResponseParam response = new ResponseParam();
        response.setCode("200");
        response.setMsg("SUCCESS");
        return response;
    }
	public static ResponseParam createSuccessResult(Object result){
		ResponseParam response = new ResponseParam();
        response.setCode("200");
        response.setMsg("SUCCESS");
        response.setResult(result);
		return response;
	}


    public static ResponseParam createFailResult(String msg){
        ResponseParam response = new ResponseParam();
        response.setCode("500");
        response.setMsg("FAIL");
        response.setMsg(msg);
        return response;
    }
	public static ResponseParam createFailResult(String code,String msg){
		ResponseParam response = new ResponseParam();
		response.setCode(code);
		response.setMsg(msg);
		return response;
	}


}
