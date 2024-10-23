package com.xiaoyuer.develop;


import com.xiaoyuer.error.common.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: BaseController用于统一异常处理
 */

@Slf4j
public class BaseController {

    /**
     * 统一异常处理类
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({RuntimeException.class, Exception.class})
    public Object exp(HttpServletRequest request, Exception ex) {

        if (ex instanceof ServiceException) {
            // TODO: 2024/10/23
        }

        return new Object();
    }

}
