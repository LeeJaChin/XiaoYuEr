package com.xiaoyuer.error.server;

import com.xiaoyuer.error.common.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * 集成ErrorCode，定性为server异常
 * @author CAIFUCHENG3
 */
public interface ServerError extends ErrorCode {
	/**
	 * httpStatus
	 * @return
	 */
	@Override
	default HttpStatus httpStatus() {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
