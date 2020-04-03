package com.biz.credit.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.config.rest.RestTemplateFactory;
import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
public class UserController {
    @Value("${user_center_url}")
    private String userCenterUrl;
    @Resource
    private RestTemplateFactory restTemplateFactory;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @GetMapping("/user/getBasicInfo")
    public RespEntity getBasicInfo(HttpServletRequest request){
        RespEntity respEntity = new RespEntity(RespCode.WARN,null);
        //String centerUrl = request.getScheme().concat("://biz-auth").concat(request.getContextPath().substring(request.getServerName().indexOf(".")+1)).concat("/auth/index.html/UserManagement");
        Object realName = request.getSession().getAttribute("realname");
        Object userId =  request.getSession().getAttribute("userId");
        if(null!=realName){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("realname",realName.toString());
            jsonObject.put("userId",userId.toString());
            jsonObject.put("userCenterUrl",userCenterUrl);
            respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        }
        return respEntity;
    }

    @GetMapping("/user/menu/second")
    public JSONObject findSecondMenuList(@RequestParam(value = "menuName",required = true) String menuName, HttpSession session){
        String prods="{code:\"02\",msg:\"invalid\",data:\"invalid_request\"}";
        Object userIdObj = session.getAttribute("userId");
        if(null!= userIdObj) {
            String userId = session.getAttribute("userId").toString();
            String token = stringRedisTemplate.opsForHash().get("biz_credit:auth:cross_token", "/user/menu/second").toString();
            prods = restTemplateFactory.getRestTemplate().getForEntity("http://AUTH-SERVICE/auth/user/menu/second?menuName=" + menuName.concat("&token=").concat(token).concat("&userId=").concat(userId), String.class).getBody();
        }
        return JSONObject.parseObject(prods);
    }

    @GetMapping("/logout")
    public RespEntity logout(HttpServletRequest request){
        request.getSession().invalidate();
        return new RespEntity(RespCode.SUCCESS,null);
    }


    @GetMapping("/user/menu/first")
    public JSONObject findFirstMenuList(HttpSession session){
        String prods="{code:\"02\",msg:\"invalid\",data:\"invalid_request\"}";
        Object userIdObj = session.getAttribute("userId");
        if(null!= userIdObj){
            String userId = session.getAttribute("userId").toString();
            String token = stringRedisTemplate.opsForHash().get("biz_credit:auth:cross_token","/user/menu/first").toString();
            prods = restTemplateFactory.getRestTemplate().getForEntity("http://AUTH-SERVICE/auth/user/menu/first?userId=".concat(userId).concat("&token=").concat(token),String.class).getBody();
        }

        //Integer userId = Integer.parseInt(session.getAttribute("userId").toString());
/*        try {
            JSONObject jsonObject = new JSONObject();
            List<JSONObject> menuList = resourceService.findFirstMenuList(userId);
            jsonObject.put("menuList",menuList);
            ret.changeRespEntity(RespCode.SUCCESS,jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return JSONObject.parseObject(prods);
    }
}
