package com.xiaoyuer.error.common;


/**
 * 统一服务异常
 */
public class ServiceException extends RuntimeException {

    private ErrorCode code;

    public ErrorCode getCode() {
        return code;
    }

    private Object[] args;

    private OriginRpcExceptionStack originRpcExceptionStack;

    ServiceException(Throwable cause, ErrorCode code, Object... args) {
        super(cause);
        this.code = code;
        this.args = args;
    }

    ServiceException(Throwable cause, ErrorCode code, OriginRpcExceptionStack originRpcExceptionStack, Object... args) {
        super(cause);
        this.code = code;
        this.originRpcExceptionStack = originRpcExceptionStack;
        this.args = args;
    }

    @Override
    public String getMessage() {
        String s = code.message();
        if (args != null) {
            s = String.format(s, args);
        }
        return s;
    }

    public String getErrorMessage(String lan) {
        String s = code.message(lan);
        if (args != null) {
            s = String.format(s, args);
        }
        return s;
    }

    public String getErrorSysMessage(String lan) {
        String s = code.sysmessage(lan);
        if (args != null) {
            s = String.format(s, args);
        }
        return s;
    }

    public OriginRpcExceptionStack getOriginRpcExceptionStack() {
        return originRpcExceptionStack;
    }

}
