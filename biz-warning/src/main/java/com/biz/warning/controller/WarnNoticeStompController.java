package com.biz.warning.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.service.IWarningNoticeService;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import com.biz.warning.util.SysDict;
import com.biz.warning.vo.WarningNoticeVO;
import com.biz.service.IAuthService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Controller
public class WarnNoticeStompController {

    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private IAuthService authService;
    @Autowired
    private IWarningNoticeService warningNoticeService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Value("${spring.profiles.active}")
    private String env;


    @RequestMapping("/warningNotice/test/{warningResultId}/{userId}")
    @ResponseBody
    public JSONObject read(@PathVariable("warningResultId")Long warningResultId,@PathVariable("userId")Integer userId, HttpSession session){
/*        log.info("/warnResult/read/ userDestPrefix:"+template.getUserDestinationPrefix());
        User user = new User();
        user.setUserId(Long.parseLong(session.getAttribute("userId").toString()));
        user.setUsername(session.getAttribute("username").toString());
        user.setInstitutionId(warnResultId.longValue());*/
        //template.setUserDestinationPrefix("/warnResult/subscribe");
        String sessionUserId = userId.toString();
        WarningNoticeVO warningNoticeVO = new WarningNoticeVO();
        warningNoticeVO.setUserId(userId);
        warningNoticeVO.setWarnResultVariableId(warningResultId);
        warningNoticeVO.setCompanyName("google");
        log.info("wbs_user_destination_prefix:"+template.getUserDestinationPrefix());
        template.convertAndSendToUser("",sessionUserId,warningNoticeVO);
        return new JSONObject();
    }

    /*@MessageMapping("/warnResult/read/{warnResultId}")
    public void queue(@PathVariable("warnResultId")Integer warnResultId) {
        System.out.println("进入方法");
        for(int i =1;i<=20;i++) {
            *//*广播使用convertAndSendToUser方法，第一个参数为用户id，此时js中的订阅地址为
            "/user/" + 用户Id + "/message",其中"/user"是固定的*//*
            template.convertAndSendToUser(wvo.getWarnResultVariableId().toString(),"/message",wvo.getWarnResultVariableId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }*/

    @GetMapping("/warningNotice/warningNotice")
    @ResponseBody
    public RespEntity warningNotice(HttpServletRequest request){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        String apiCode = request.getSession().getAttribute(SysDict.API_CODE).toString();
        Integer userType = Integer.parseInt(request.getSession().getAttribute(SysDict.USER_TYPE).toString());
        Integer userId = Integer.parseInt(request.getSession().getAttribute(SysDict.USER_ID).toString());
        List<Integer> userIdList = authService.getAdminUserIds(request.getHeader(SysDict.COOKIE));
        WarningNoticeVO warningNoticeVO = new WarningNoticeVO();
        warningNoticeVO.setUserIds(userIdList);
        warningNoticeVO.setUserType(userType);
        warningNoticeVO.setApiCode(apiCode);
        warningNoticeVO.setUserId(userId);
        //warningNoticeService.findList(warningNoticeVO);
        Long total = warningNoticeService.findUnReadWarningNoticeCount(warningNoticeVO);
        WarningNoticeVO retWarningNotice = total>0?warningNoticeService.findSingleWarningNoticeUnRead(warningNoticeVO):warningNoticeService.findSingleWarningNotice(warningNoticeVO);
        JSONObject jsonObject = new JSONObject();
        if(null!= retWarningNotice){
            retWarningNotice.setUserId(userId);
            //log.info(retWarningNotice.toString());
            jsonObject =  JSONObject.parseObject(JSONObject.toJSONString(retWarningNotice));
            jsonObject.put("total",total);
            jsonObject.put("query","1");
        }else{
            jsonObject.put("total",total);
            jsonObject.put("query","0");
        }
        respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        return respEntity;
    }

    @GetMapping("/warningNotice/warningNotices")
    @ResponseBody
    public RespEntity warningNotices(HttpServletRequest request,
                                     @RequestParam(value = "pageNo",defaultValue = "1")Integer pageNo,
                                     @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                     @RequestParam(value = "read",defaultValue = "-1")Integer read){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        String apiCode = request.getSession().getAttribute(SysDict.API_CODE).toString();
        Integer userId = Integer.parseInt(request.getSession().getAttribute(SysDict.USER_ID).toString());
        Integer userType = Integer.parseInt(request.getSession().getAttribute(SysDict.USER_TYPE).toString());
        Page<WarningNoticeVO> pageHelper = PageHelper.startPage(pageNo,pageSize);
        List<Integer> userIdList = authService.getAdminUserIds(request.getHeader(SysDict.COOKIE));
        WarningNoticeVO warningNoticeVO = new WarningNoticeVO();
        warningNoticeVO.setUserIds(userIdList);
        warningNoticeVO.setUserId(userId);
        warningNoticeVO.setUserType(userType);
        warningNoticeVO.setApiCode(apiCode);
        warningNoticeVO.setRead(read);
        //log.info("/warningNotice/warningNotices warningNoticeVO:"+warningNoticeVO);
        warningNoticeService.findList(warningNoticeVO);
        Long total = pageHelper.getTotal();
        List<WarningNoticeVO> list = pageHelper.getResult();
        LinkedHashMap<String,List<WarningNoticeVO>> linkedHashMap = new LinkedHashMap<>();
        if(!CollectionUtils.isEmpty(list)){
            //log.info(list.toString());
            list.forEach(wnv->{
                if(!linkedHashMap.containsKey(wnv.getHitTime())){
                    linkedHashMap.put(wnv.getHitTime(),new ArrayList<>());
                }
                linkedHashMap.get(wnv.getHitTime()).add(wnv);
            });
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",total);
        jsonObject.put("rows",linkedHashMap);
        respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        return respEntity;
    }
    @PutMapping("/warningNotice/{warningResultVariableId}")
    @ResponseBody
    public RespEntity readWarningNotice(HttpServletRequest request,
                                          @PathVariable("warningResultVariableId")Long warningResultVariableId){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        Integer userId = Integer.parseInt(request.getSession().getAttribute(SysDict.USER_ID).toString());
        String apiCode = request.getSession().getAttribute(SysDict.API_CODE).toString();
        Integer userType = Integer.parseInt(request.getSession().getAttribute(SysDict.USER_TYPE).toString());
        WarningNoticeVO warningNoticeVO = new WarningNoticeVO();
        warningNoticeVO.setUserId(userId);
        warningNoticeVO.setApiCode(apiCode);
        warningNoticeVO.setWarnResultVariableId(warningResultVariableId);
        int count = warningNoticeService.readWarnResultVariable(warningNoticeVO);
        warningNoticeVO.setWarnResultVariableId(null);
        Long unReadCount = warningNoticeService.findUnReadWarningNoticeCount(warningNoticeVO);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count",count);
        jsonObject.put("warnResultVariableId",warningNoticeVO.getWarnResultVariableId());
        jsonObject.put("userId",userId);
        jsonObject.put("apiCode",apiCode);
        jsonObject.put("unReadCount",unReadCount);
        respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        return respEntity;
    }
    @GetMapping("/warningNotice/init")
    @ResponseBody
    public RespEntity initWarningNotice(@RequestParam("start")Long start,@RequestParam("end")Long end){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        try {
            warningNoticeService.initWarningNotices(start,end);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("start",start);
        jsonObject.put("end",end);
        respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        return respEntity;
    }

    @PostMapping("/warningNotice/test/{userId}")
    @ResponseBody
    public RespEntity WarningNotice(@PathVariable("userId") Integer userId,WarningNoticeVO warningNoticeVO){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        if(!StringUtils.equals("prod",env)){
            log.info("send_wbs_test["+userId+"]:"+warningNoticeVO);
            simpMessagingTemplate.convertAndSendToUser(StringUtils.EMPTY,userId.toString(),warningNoticeVO );
            respEntity.changeRespEntity(RespCode.SUCCESS,warningNoticeVO);
        }
        return respEntity;
    }


}
