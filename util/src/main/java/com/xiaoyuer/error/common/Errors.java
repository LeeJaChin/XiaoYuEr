package com.xiaoyuer.error.common;


import com.xiaoyuer.error.CommonError;

/**
 * 异常统一抛出格式化：Errors.wrap()
 *
 */
public class Errors {

	public static ServiceException wrap(ErrorCode code, Object... args) {
		//return wrap(code, null, null);
		return wrap(null, code, args);
	}

	public static ServiceException wrap(ErrorCode code,OriginRpcExceptionStack originRpcExceptionStack, Object... args) {
		//return wrap(code, null, null);
		return wrap(null, code, originRpcExceptionStack,args);
	}

	public static ServiceException wrap(Throwable cause) {
		if (cause instanceof ServiceException) {
			/**
			 *  避免重复处理已经处理过的错误
			 */
			return (ServiceException) cause;
		}
		return wrap(cause, CommonError.UNEXPECTED, cause.toString());
	}

	public static ServiceException wrap(Throwable cause, ErrorCode code) {
		ServiceException e = new ServiceException(cause, code, null);
		return e;
	}

	public static ServiceException wrap(Throwable cause, ErrorCode code, Object... args) {
		ServiceException e = new ServiceException(cause, code, args);
		return e;
	}

	public static ServiceException wrap(Throwable cause, ErrorCode code, OriginRpcExceptionStack originRpcExceptionStack,Object... args) {
		ServiceException e = new ServiceException(cause, code,originRpcExceptionStack, args);
		return e;
	}
}
