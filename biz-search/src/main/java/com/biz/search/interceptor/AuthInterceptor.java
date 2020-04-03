package com.biz.search.interceptor;

import com.biz.search.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Value("${biz.api-code}")
    private String apiCode;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (null == session.getAttribute(Constants.USER_ID) || null == session.getAttribute(Constants.API_CODE)) {
            log.warn("session为空, 初始化session信息!!");
            String userId = request.getParameter(Constants.USER_ID);
            if (StringUtils.isNotBlank(userId)) {
                session.setAttribute(Constants.USER_ID, userId);
            } else {
                session.setAttribute(Constants.USER_ID, Constants.BAIRONG_SUPER_ADMIN);
            }
            session.setAttribute(Constants.API_CODE, this.apiCode);
            session.setAttribute(Constants.USER_TYPE, Constants.USER_TYPE_SUPER_ADMIN);
            session.setAttribute(Constants.GROUP_ID, NumberUtils.INTEGER_ZERO.toString());
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
