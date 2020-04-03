package com.biz.warning.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.controller.BaseController;
import com.biz.strategy.entity.EntityBasic;
import com.biz.warning.domain.RiskSource;
import com.biz.warning.domain.TimeScope;
import com.biz.warning.service.IStatisticService;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/statistic")
public class StatisticController extends BaseController {

    @Autowired
    private IStatisticService service;

    /**
     * 获取风险来源
     * @return
     */
    @GetMapping("/riskType")
    public RespEntity riskType() {
        RespEntity entity = new RespEntity();
        try {
            List<TimeScope> data = service.getTimeScope();
            entity.changeRespEntity(RespCode.SUCCESS, data);
        } catch (Exception e) {
            log.error("查询风险来源失败: " + e.getMessage(), e);
        }
        return entity;
    }

    /**
     * 风险来源统计
     * @param type
     * @return
     */
    @GetMapping("/riskSourceCount")
    public RespEntity riskSourceCount(@RequestParam("scope") String type) {
        RespEntity entity = new RespEntity();
        try {
            String apiCode = getApiCode();
            List<Integer> userIds = getUserIdList();
            List<RiskSource> data = service.riskSourceCount(apiCode, userIds, type);
            entity.changeRespEntity(RespCode.SUCCESS, data);
        } catch (Exception e) {
            log.error("查询风险来源统计信息失败: " + e.getMessage(), e);
        }
        return entity;
    }

    /**
     * 风险来源统计(全部，根据公司名)
     * @param companyName
     * @return
     */
    @GetMapping("/allRiskByCompanyName")
    public RespEntity allRiskByCompanyName(@RequestParam("entityName") String companyName) {
        String apiCode = getApiCode();
        List<Integer> userIdList = getUserIdList();
        List<RiskSource> allRiskByCompanyName = service.queryAllRiskByCompanyName(apiCode, userIdList, companyName);
        RespEntity entity = new RespEntity();
        entity.changeRespEntity(RespCode.SUCCESS, allRiskByCompanyName);
        return entity;
    }


    /**
     * 风险详情信息
     * @param warnId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/riskDetails")
    public RespEntity riskDetails(@RequestParam("warnId") Long warnId,
                                  @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                  @RequestParam(value = "pageSize", defaultValue = "1") Integer pageSize) {
        RespEntity entity = new RespEntity();
        try {
            JSONObject data = service.riskDetails(warnId, pageNo, pageSize);
            //JSONArray data = service.riskDetails(warnId);
            entity.changeRespEntity(RespCode.SUCCESS, data);
        } catch (Exception e) {
            log.error("查询风险详情信息失败: " + e.getMessage(), e);
        }
        return entity;
    }

    /**
     * 进件基础信息
     * @param warnId
     * @return
     */
    @GetMapping("/entityBasicInfo")
    public RespEntity entityBasicInfo(@RequestParam("warnId") Long warnId) {
        RespEntity entity = new RespEntity();
        try {
            EntityBasic data = service.entityBasicInfo(warnId);
            entity.changeRespEntity(RespCode.SUCCESS, data);
        } catch (Exception e) {
            log.error("查询进件基础信息失败: " + e.getMessage(), e);
        }
        return entity;
    }

    /**
     * 获取裁判文书详情
     * @param warnId
     * @param caseId
     * @return
     */
    @GetMapping("/caseDetail")
    public RespEntity caseDetail(@RequestParam("warnId") Long warnId,
                                 @RequestParam("caseId") String caseId) {
        RespEntity entity = new RespEntity();
        try {
            String apiCode = getApiCode();
            JSONArray data = service.getCaseDetail(warnId, caseId, apiCode);
            entity.changeRespEntity(RespCode.SUCCESS, data);
        } catch (Exception e) {
            log.error("查询裁判文书详情信息失败: " + e.getMessage(), e);
        }
        return entity;
    }
}
