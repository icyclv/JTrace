package com.second.jtrace.server.controller.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //提取session 判断是否登录
        Object user = request.getSession().getAttribute("user");
        //输出请求地址
        System.out.println("request.getRequestURI():" + request.getRequestURI());
        System.out.println("user:" + user);
        if (user == null) {
            response.setStatus(401);
            return false;
        }

        return true;
    }


}
