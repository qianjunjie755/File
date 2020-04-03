package com.biz.credit.interceptor;

import com.biz.credit.utils.Constants;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class BairongSuperAdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("请求地址2"+request.getRequestURI());
        Object apiCode = request.getSession().getAttribute(Constants.API_CODE);
        Object userId = request.getSession().getAttribute(Constants.USER_ID);
        if(null==apiCode || !StringUtils.equals(Constants.BAIRONG_API_CODE,apiCode.toString())
                ||null==userId || !StringUtils.equals(Constants.BAIRONG_SUPER_ADMIN,userId.toString()))
            return false;
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
