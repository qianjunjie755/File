package com.biz.credit.interceptor;

import com.alibaba.druid.util.StringUtils;
import com.biz.credit.utils.Constants;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SuperAdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object userType = request.getSession().getAttribute(Constants.USER_TYPE);
        if(null==userType||!StringUtils.equals(Constants.USER_TYPE_SUPER_ADMIN,userType.toString())) {
            response.setHeader("bairong-msg","userType_unValid");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
