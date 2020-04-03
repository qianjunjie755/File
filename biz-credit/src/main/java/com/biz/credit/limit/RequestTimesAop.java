package com.biz.credit.limit;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RequestTimesAop {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //切面范围
    @Pointcut("execution(public * com.biz.credit.controller.ReportApiController.*(..))")
    public void WebPointCut() {
    }

    /**
     * JoinPoint对象封装了SpringAop中切面方法的信息,在切面方法中添加JoinPoint参数,就可以获取到封装了该方法信息的JoinPoint对象.
     */
    @Before("WebPointCut() && @annotation(times)")
    public void ifovertimes(final JoinPoint joinPoint, RequestTimes times) {
        try {
            /**
             * 另一种获取request
             */
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getRemoteAddr();
            String url = request.getRequestURL().toString();
            String key = "ifovertimes".concat(url).concat(ip);
            //访问次数加一
            long count = stringRedisTemplate.boundValueOps(key).increment( 1);//val做+1操作
            //如果是第一次，则设置过期时间
            if (count == 1) {
                stringRedisTemplate.expire(key, times.time(), TimeUnit.MILLISECONDS);//设置过期时间
            }
            if (count > times.count()) {
                request.setAttribute("ifovertimes", "true");
            } else {
                request.setAttribute("ifovertimes", "false");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
