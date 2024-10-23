package com.xiaoyuer.error.client;

import com.xiaoyuer.error.common.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * 集成ErrorCode，定性为客户端校验性异常
 *
 */
public interface ClientError extends ErrorCode {
	/**
	 * httpStatus
	 * @return
	 */
	@Override
	default HttpStatus httpStatus() {
		return HttpStatus.BAD_REQUEST;
	}
}
