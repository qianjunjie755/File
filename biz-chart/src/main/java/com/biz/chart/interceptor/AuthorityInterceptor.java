package com.biz.chart.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.biz.chart.utils.RespCode;
import com.biz.chart.utils.RespEntity;
import com.biz.service.IAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Value("${spring.profiles.active}")
    private String env;
    @Resource
    private IAuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        Object authority = request.getSession().getAttribute("res1");
        if(null!=authority){
            //用户userType变更时,登录过期
            //登录是否有效标识 loginValid
            boolean loginValid = true;
            try {
                Integer userType = authService.getUserType(request.getHeader("cookie"),Integer.valueOf(request.getSession().getAttribute("userId").toString()));
                if(!Integer.valueOf(request.getSession().getAttribute("userType").toString()).equals(userType)){
                    request.getSession().invalidate();
                    loginValid = false;
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }

            if(loginValid){
                List<String> reqUris = Arrays.asList(authority.toString().split(","));
                for(String reqUri:reqUris){
                    if(requestUri.length()>=reqUri.length()&&StringUtils.equals(reqUri,requestUri.substring(0,reqUri.length()))){
                        return true;
                    }
                }
            }
        }
        log.info("请求地址1"+request.getRequestURI()+",权限:"+authority);
        response.setHeader("biz-interceptor","1");
        String loginUrl = "http://127.0.0.1/auth/index.html#/UserManagement";
        if(Objects.equals("uat",env)){
            loginUrl = loginUrl.replaceAll("127.0.0.1","biz-auth.100credit2.cn");
        }else if(Objects.equals("prod",env)){
            loginUrl = loginUrl.replaceAll("127.0.0.1","biz-auth.100credit.cn");
        }
        JSONObject data = new JSONObject();
        data.put("loginUrl",loginUrl);
        RespEntity respEntity = new RespEntity(RespCode.REQ_VALID,data);
        response.getWriter().write(JSONObject.toJSONString(respEntity));
        return false;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
