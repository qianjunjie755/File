package com.biz.credit.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.vo.ParamVO;
import com.biz.decision.entity.EntityBasic;

import java.util.List;
import java.util.Map;

public interface IStrategyService {

    /**
     * 获取决策流
     *
     * @param apiCode
     * @return
     */
    JSONObject getDecideFlows(String apiCode);

    /**
     * 开启决策流
     *
     * @param request
     * @return
     */
    JSONObject startDecideFlow(Map<String, Object> request);

    /**
     * 获取决策流参数
     *
     * @param apiCode
     * @param flowId
     * @return
     */
    List<ParamVO> getFlowParams(String apiCode, Integer flowId);

    /**
     * 获取基础新
     * @param companyName
     * @return
     */
    EntityBasic getBasicInfo(String companyName);
}
