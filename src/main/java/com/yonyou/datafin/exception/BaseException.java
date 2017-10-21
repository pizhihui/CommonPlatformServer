package com.yonyou.datafin.exception;



/**
 * 基础异常
 * @author jiwenlong
 * 2017-06-19
 */
public class BaseException extends Exception {

	private static final long serialVersionUID = 1L;

	public BaseException() {
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
