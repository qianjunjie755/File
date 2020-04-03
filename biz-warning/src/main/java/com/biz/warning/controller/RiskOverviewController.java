package com.biz.warning.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.controller.BaseController;
import com.biz.service.IAuthService;
import com.biz.warning.domain.EntityCount;
import com.biz.warning.service.IRiskOverviewService;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import com.biz.warning.util.SysDict;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/riskOverview")
public class RiskOverviewController extends BaseController {

    @Autowired
    private IAuthService authService;
    @Autowired
    private IRiskOverviewService riskOverviewService;

    @GetMapping(value="/findHitEntityPointMostList")
    public RespEntity findHitEntityMostList(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String apiCode = session.getAttribute(SysDict.API_CODE).toString();
        String cookie = request.getHeader(SysDict.COOKIE);
        Integer userId = Integer.parseInt(session.getAttribute(SysDict.USER_ID).toString());
        String userType = session.getAttribute(SysDict.USER_TYPE).toString();
        RespEntity entity = new RespEntity();
        try {
            List<Integer> userIds = null;
            if (StringUtils.equals(userType, SysDict.USER_TYPE_NORMAL)) {
                userIds = new ArrayList<>();
                userIds.add(userId);
            } else if (StringUtils.equals(userType, SysDict.USER_TYPE_ADMIN)) {
                userIds = authService.getAdminUserIds(cookie);
            }
            Map<String, List> map = riskOverviewService.findHitEntityPointMostList(apiCode, userIds);
            entity.changeRespEntity(RespCode.SUCCESS, map);
        } catch (Exception e) {
            log.error("风险企业或风险点排行异常:" + e.getMessage(), e);
        }
        return entity;
    }
    @GetMapping(value="/countEntityAmount")
    public RespEntity countEntity(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String apiCode = session.getAttribute(SysDict.API_CODE).toString();
        String cookie = request.getHeader(SysDict.COOKIE);
        Integer userId = Integer.parseInt(session.getAttribute(SysDict.USER_ID).toString());
        String userType = session.getAttribute(SysDict.USER_TYPE).toString();

        RespEntity entity = new RespEntity();
        Map map=new HashMap<String,String>();
        map.put("historyEntity",0);
        map.put("currentEntity",0);
        //userType="2";
        try {
            List<Integer> userIds = null;
            if (StringUtils.equals(userType, SysDict.USER_TYPE_NORMAL)) {
                userIds = new ArrayList<>();
                userIds.add(userId);
            } else if (StringUtils.equals(userType, SysDict.USER_TYPE_ADMIN)) {
                userIds = authService.getAdminUserIds(cookie);
            }
            List<EntityCount> entityCountList = riskOverviewService.countEntityAmount(apiCode, userIds);
            if (entityCountList != null) {
                for (EntityCount entityCout : entityCountList) {
                    if (entityCout.getEntityStatus() == 0) {
                        map.put("historyEntity", entityCout.getEntityCount());
                    }
                    if (entityCout.getEntityStatus() == 1) {
                        map.put("currentEntity", entityCout.getEntityCount());
                    }
                }
            }
            entity.changeRespEntity(RespCode.SUCCESS, map);
        } catch (Exception e) {
            log.error("当前/历史监控企业查询异常：" + e.getMessage(), e);
        }

        return entity;
    }

    @GetMapping(value="/countHitTrend")
    public RespEntity countHitTrend(HttpServletRequest request,@RequestParam(value="type")String type) {
        HttpSession session = request.getSession(false);
        String apiCode = session.getAttribute(SysDict.API_CODE).toString();
        String cookie = request.getHeader(SysDict.COOKIE);
        Integer userId = Integer.parseInt(session.getAttribute(SysDict.USER_ID).toString());
        String userType = session.getAttribute(SysDict.USER_TYPE).toString();

        RespEntity entity = new RespEntity();
        try {
            List<Integer> userIds = null;
            if (StringUtils.equals(userType, SysDict.USER_TYPE_NORMAL)) {
                userIds = new ArrayList<>();
                userIds.add(userId);
            } else if (StringUtils.equals(userType, SysDict.USER_TYPE_ADMIN)) {
                userIds = authService.getAdminUserIds(cookie);
            }
            //暂时默认查询全部("0")
            JSONObject result = riskOverviewService.countHitTrend(apiCode, userIds, type);
            entity.changeRespEntity(RespCode.SUCCESS, result);
        } catch (Exception e) {
            log.error("预警趋势查询异常：" + e.getMessage(), e);
        }
        return entity;
    }

    @GetMapping(value="/monitorAndHitRuleCompanyTrend")
    public RespEntity monitorAndHitruleCompany(HttpServletRequest request,
                                               @RequestParam(value="type")String type) {
        HttpSession session = request.getSession(false);
        String apiCode = session.getAttribute(SysDict.API_CODE).toString();
        String cookie = request.getHeader(SysDict.COOKIE);
        Integer userId = Integer.parseInt(session.getAttribute(SysDict.USER_ID).toString());
        String userType = session.getAttribute(SysDict.USER_TYPE).toString();

        RespEntity entity = new RespEntity();
        try {
            List<Integer> userIds = null;
            if (StringUtils.equals(userType, SysDict.USER_TYPE_NORMAL)) {
                userIds = new ArrayList<>();
                userIds.add(userId);
            } else if (StringUtils.equals(userType, SysDict.USER_TYPE_ADMIN)) {
                userIds = authService.getAdminUserIds(cookie);
            }
            //暂时默认查询全部("0")
            JSONObject result = riskOverviewService.monitorAndHitCompTrend(apiCode, userIds, type);
            entity.changeRespEntity(RespCode.SUCCESS, result);
        } catch (Exception e) {
            log.error("当前监控企业和命中规则企业数趋势查询异常：" + e.getMessage(), e);
        }
        return entity;
    }

    @GetMapping(value="/sizerProcesser")
    public RespEntity sizerProcesser(HttpServletRequest request,
                            @RequestParam(value="type")String type,
                            @RequestParam(value="period")String period,
                            @RequestParam(value="sourceIds[]")String[] sourceIds) {
        HttpSession session = request.getSession(false);
        String apiCode = session.getAttribute(SysDict.API_CODE).toString();
        String cookie = request.getHeader(SysDict.COOKIE);
        Integer userId = Integer.parseInt(session.getAttribute(SysDict.USER_ID).toString());
        String userType = session.getAttribute(SysDict.USER_TYPE).toString();

        RespEntity entity = new RespEntity();
        try {
            List<Integer> userIds = null;
            if (StringUtils.equals(userType, SysDict.USER_TYPE_NORMAL)) {
                userIds = new ArrayList<>();
                userIds.add(userId);
            } else if (StringUtils.equals(userType, SysDict.USER_TYPE_ADMIN)) {
                userIds = authService.getAdminUserIds(cookie);
            }
            JSONObject jsonObject = riskOverviewService.findRiskHitSituaion(apiCode, userIds,type,period,sourceIds);
            entity.changeRespEntity(RespCode.SUCCESS, jsonObject);
        } catch (Exception e) {
            log.error("筛选条件sizer结果异常:" + e.getMessage(), e);
        }
        return entity;
    }

}
