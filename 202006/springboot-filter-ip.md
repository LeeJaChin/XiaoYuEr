#springboot增加白名单
```java
package com.config;

import com.elsshellapi.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by lijiaqi by 2020/6/8.
 *
 * @author lijiaqi
 */
@WebFilter(filterName = "ipFilter",urlPatterns = {"/*"})
@Slf4j
@Component
public class IpFilter implements Filter {

    @Value("${shellapi:whiteList}")
    private String whiteList;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String clientId = IPUtil.getClientIpAddress(request);
        log.info("client request id is {}",clientId);
        if(!Arrays.asList(whiteList.split(",")).contains(clientId)){
            response.getWriter().print("Illegal request");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


}

```
- 注意如果没有增加@Component或者Configuration之类的，启动类需要增加@ServletComponentScan，可以使用order多个filter进行排序