package com.biz.warning.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.domain.EntityCount;

import java.util.List;
import java.util.Map;

public interface IRiskOverviewService {

    Map findHitEntityPointMostList(String apiCode, List<Integer> userIds);

    List<EntityCount> countEntityAmount(String apiCode, List<Integer> userIds);

    JSONObject countHitTrend(String apiCode, List<Integer> userIds, String type);

    JSONObject countMonitorAndHitTrend(String apiCode, List<Integer> userIds, String type);

    JSONObject findRiskHitSituaion(String apiCode, List<Integer> userIds, String type, String period, String[] sourceIds);

    JSONObject monitorAndHitCompTrend(String apiCode, List<Integer> userIds, String type);
}
