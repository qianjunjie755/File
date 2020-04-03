package com.biz.warning.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.config.rest.RestTemplateFactory;
import com.biz.strategy.common.Constants;
import com.biz.strategy.common.EVarType;
import com.biz.strategy.entity.EntityBasic;
import com.biz.utils.BizUtils;
import com.biz.utils.ZipUtils;
import com.biz.warning.dao.StatisticDAO;
import com.biz.warning.domain.RiskSource;
import com.biz.warning.domain.TimeScope;
import com.biz.warning.domain.VariableDetail;
import com.biz.warning.service.IStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class StatisticServiceImpl implements IStatisticService {

    @Value("${biz.warning.query-detail-url}")
    private String apiQueryDetailUrl;

    @Autowired
    private StatisticDAO dao;

    @Autowired
    private RestTemplateFactory restTemplateFactory;

    @Override
    public List<TimeScope> getTimeScope() {
        return dao.queryTimeScope();
    }

    @Override
    @Transactional(isolation = Isolation.DEFAULT, timeout = 3600, rollbackFor = {Exception.class})
    public void statisticD(String date) {
        dao.deleteVarDaily(date);
        dao.statisticVarDaily(date);
    }

    @Override
    @Transactional(isolation = Isolation.DEFAULT, timeout = 3600, rollbackFor = {Exception.class})
    public void statisticN(String type) {
        dao.deleteVarNearly(type);
        String startTime = null;
        String endTime = null;
        //不是全部
        if (!"0".equals(type)) {
            LocalDate now = LocalDate.now();
            LocalDate startDate = null;
            int n = Integer.valueOf(type.substring(0, type.length() - 1));
            char u = type.charAt(type.length() - 1);
            if (u == 'd' || u == 'D') {
                startDate = now.minusDays(n);
            } else if (u == 'w' || u == 'W') {
                startDate = now.minusWeeks(n);
            } else if (u == 'm' || u == 'M') {
                startDate = now.minusMonths(n);
            } else if (u == 'y' || u == 'Y') {
                startDate = now.minusYears(n);
            } else {
                throw new RuntimeException("未知统计类型单位[" + type + "]");
            }
            startTime = startDate.plusDays(1).toString() + " 00:00:00";
            endTime = now.toString() + " 23:59:59";
        }
        dao.statisticVarNearly(type, startTime, endTime);
    }

    @Override
    public List<RiskSource> riskSourceCount(String apiCode, List<Integer> userIds, String type) {
        String startTime = null;
        String endTime = null;
        //不是全部
        if (!"0".equals(type)) {
            LocalDate now = LocalDate.now();
            LocalDate startDate = null;
            int n = Integer.valueOf(type.substring(0, type.length() - 1));
            char u = type.charAt(type.length() - 1);
            if (u == 'd' || u == 'D') {
                startDate = now.minusDays(n);
            } else if (u == 'w' || u == 'W') {
                startDate = now.minusWeeks(n);
            } else if (u == 'm' || u == 'M') {
                startDate = now.minusMonths(n);
            } else if (u == 'y' || u == 'Y') {
                startDate = now.minusYears(n);
            } else {
                throw new RuntimeException("未知统计类型单位[" + type + "]");
            }
            startTime = startDate.plusDays(1).toString() + " 00:00:00";
            endTime = now.toString() + " 23:59:59";
        }
        //查询风险统计
        return dao.queryRiskStatistic(apiCode, userIds, startTime, endTime);
    }

    @Override
    public List<RiskSource> queryAllRiskByCompanyName(String apiCode, List<Integer> userIds, String companyName) {
        List<Integer> entityIds = dao.queryEntityIdByName(companyName);
        //查询风险统计
        return dao.queryAllRiskByCompanyName(apiCode, userIds, entityIds);
    }

    @Override
    public JSONObject riskDetails(Long warnId, Integer pageNo, Integer pageSize) {
        //判断变量类型
        EVarType type = EVarType.valueOf(dao.queryVariableType(warnId));
        //增量类型变量采用分页查询历史,其他类型变量则查询当前记录
        long total;
        List<VariableDetail> result;
        if (type == EVarType.U_VALUE) {
            result = dao.queryRiskDetails(warnId, (pageNo - 1) * pageSize, pageSize);
            total = dao.countRiskDetails(warnId);
        } else {
            result = dao.queryRiskDetails(warnId, 0, 1);
            total = result == null ? 0 : result.size();
        }
        JSONArray array = new JSONArray();
        if (result != null) {
            result.forEach(v -> {
                JSONObject object = new JSONObject();
                object.put("hitDate", v.getHitDate());
                object.put("varDetail", JSON.parseArray(ZipUtils.gunzip(v.getVarDetail())));
                array.add(object);
            });
        }
        JSONObject data = new JSONObject();
        data.put("total", total);
        data.put("rows", array);
        return data;
    }

    @Override
    public EntityBasic entityBasicInfo(Long warnId) {
        EntityBasic entityBasic = dao.queryEntityBasicInfoById(warnId);
        if (entityBasic == null) {
            entityBasic = dao.queryEntityBasicInfoByName(warnId);
        }
        return entityBasic;
    }

    @Override
    public JSONArray getCaseDetail(Long warnId, String caseId, String apiCode) {
        //先查询数据库
        String content = dao.queryCaseDetail(warnId, caseId);
        if (StringUtils.isNotBlank(content)) {
            return JSONArray.parseArray(content);
        }

        String appId = dao.queryAppId(warnId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf(Constants.APPLICATION_FORM_URLENCODED));
        //调用同步接口查询数据
        JSONObject apiParams = new JSONObject();
        apiParams.put("api", "BizJudgmentDocDetail");
        apiParams.put("id", caseId);
        apiParams.put("version", "1.0");
        JSONObject plateForm = new JSONObject();
        plateForm.put(Constants.APP_ID, appId);
        apiParams.put(Constants.PLATFORM, plateForm);
        //组装请求数据
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(Constants.API_CODE, apiCode);
        map.add(Constants.API_DATA, apiParams.toJSONString());
        //API接口调用
        log.info("调用查询接口请求数据[{}][{}]", warnId, caseId);
        ResponseEntity<String> responseEntity = restTemplateFactory.getRestTemplate().postForEntity(apiQueryDetailUrl, new HttpEntity<>(map, httpHeaders), String.class);
        String respResult = responseEntity.getBody();
        log.info("调用查询接口响应数据[{}][{}]: {}", warnId, caseId, respResult);
        JSONObject resp = JSONObject.parseObject(respResult);
        int code = resp.getIntValue(Constants.CODE);
        //调用成功, 保存基础数据
        if (code != Constants.SUCCESS) {
            return null;
        }

        JSONArray array = new JSONArray();
        JSONObject result = resp.getJSONObject(Constants.RESULT);
        if (result != null) {
            String judgeReason = result.getString("JudgeReason");
            JSONObject object = new JSONObject();
            object.put("key", "裁判理由");
            object.put("value", judgeReason == null ? StringUtils.EMPTY : BizUtils.trimSpecialChar(judgeReason));
            array.add(object);

            String judgementProcess = result.getString("JudgementProcess");
            object = new JSONObject();
            object.put("key", "审判程序");
            object.put("value", judgementProcess == null ? StringUtils.EMPTY : BizUtils.trimSpecialChar(judgementProcess));
            array.add(object);

            String caseDescription = result.getString("CaseDescription");
            object = new JSONObject();
            object.put("key", "案件描述");
            object.put("value", caseDescription == null ? StringUtils.EMPTY : BizUtils.trimSpecialChar(caseDescription));
            array.add(object);
        }
        //如果读取到数据则保存
        if (!array.isEmpty()) {
            try {
                dao.insertCaseDetail(warnId, caseId, array.toJSONString());
            } catch (Exception e) {
                log.warn("插入案件[{}][{}]详情信息失败: {}", warnId, caseId, e.getMessage());
            }
        }
        return array;
    }
}
