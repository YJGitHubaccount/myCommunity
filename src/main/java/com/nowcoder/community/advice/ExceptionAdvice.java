package com.nowcoder.community.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 在视图层（Controller）统一处理异常
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletResponse response, HttpServletRequest
                                request) throws IOException {
        logger.error("服务器发生异常"+e.getMessage());
        for (StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }

        //判断异步请求还是普通请求,异步返回json字符串，普通则重定向
        String xRequestedWith = request.getHeader("x-requested-with");
        if (xRequestedWith != null && xRequestedWith.equals("XMLHttpRequest")){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.write(CommunityUtil.getJsonString(1,"服务器异常!"));
        }else{
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
