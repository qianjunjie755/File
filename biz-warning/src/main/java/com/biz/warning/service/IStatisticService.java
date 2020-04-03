package com.biz.warning.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.warning.domain.RiskSource;
import com.biz.warning.domain.TimeScope;
import com.biz.strategy.entity.EntityBasic;

import java.util.List;

public interface IStatisticService {

    /**
     * 获取时间范围
     * @return
     */
    List<TimeScope> getTimeScope();

    /**
     * 统计每日变量命中情况
     * @param date 前一天日期yyyy-MM-dd
     */
    void statisticD(String date);

    /**
     * 统计近段时间变量命中情况
     * @param type
     */
    void statisticN(String type);

    /**
     * 风险来源统计
     * @param apiCode
     * @param userIds
     * @param type
     * @return
     */
    List<RiskSource> riskSourceCount(String apiCode, List<Integer> userIds, String type);

    /**
     * 根据公司名查询全部风险
     * @param apiCode
     * @param userIds
     * @param companyName
     * @return
     */
    List<RiskSource> queryAllRiskByCompanyName(String apiCode, List<Integer> userIds, String companyName);

    /**
     * 获取风险详情
     * @param warnId
     * @param pageNo
     * @param pageSize
     * @return
     */
    JSONObject riskDetails(Long warnId, Integer pageNo, Integer pageSize);

    /**
     * 进件基础信息
     * @param warnId
     * @return
     */
    EntityBasic entityBasicInfo(Long warnId);

    /**
     * 裁判文书详情
     * @param warnId
     * @param caseId
     * @param apiCode
     * @return
     */
    JSONArray getCaseDetail(Long warnId, String caseId, String apiCode);
}
