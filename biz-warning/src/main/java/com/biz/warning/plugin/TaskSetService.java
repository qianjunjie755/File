package com.biz.warning.plugin;

import com.alibaba.fastjson.JSON;
import com.biz.service.IAuthService;
import com.biz.strategy.common.EHit;
import com.biz.strategy.common.EVersion;
import com.biz.strategy.entity.Task;
import com.biz.strategy.entity.*;
import com.biz.strategy.plugins.Plugin;
import com.biz.strategy.utils.EngineUtils;
import com.biz.utils.ZipUtils;
import com.biz.warning.dao.*;
import com.biz.warning.domain.*;
import com.biz.warning.redis.RedisNotice;
import com.biz.warning.vo.StatitscVO;
import com.biz.warning.vo.WarningNoticeVO;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TaskSetService extends Plugin {
    @Autowired
    private WarnResultVariableDAO warnResultVariableDAO;
    @Autowired
    private WarnResultRuleDAO warnResultRuleDAO;
    @Autowired
    private VariableLogDAO variableLogDAO;
    @Autowired
    private RuleLogDAO ruleLogDAO;
    @Autowired
    private RuleSetLogDAO ruleSetLogDAO;
    @Value("${biz.task.rank-rule-queue-name}")
    private String rankRuleQueueName;
    @Value("${biz.task.rank-entity-queue-name}")
    private String rankEntityQueueName;
    @Autowired
    private StatisticDAO statisticDAO;
    @Autowired
    private WarningNoticeDAO warningNoticeDAO;
    @Autowired
    private IAuthService authService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${biz.warning.ws.warn-notes-key}")
    private String warnNoticeListKey;
    @Autowired
    private RedisNotice redisNotice;

    @Override
    public void execute(Task task, TaskStrategy strategy, TaskInput taskInput) {
        //进件未成功处理直接返回
        if (!taskInput.getResult().ok()) {
            return;
        }
        //工商1.0策略规则命中结果记录
        if (task.getVersion() == EVersion.V_10) {
            // Do Nothing
        }
        //工商2.0策略规则命中结果记录
        else if (task.getVersion() == EVersion.V_20) {
            saveWarnHitResult(task, taskInput);
        }
    }

    private void saveWarnHitResult(Task task, TaskInput taskInput) {
        //整理所有的规则集
        List<TaskRules> ruleSets = new ArrayList<>();
        //企业+法人强规则
        List<TaskApi> ruleApis = taskInput.getRuleApis();
        if (ruleApis != null) {
            for (TaskApi api : ruleApis) {
                List<TaskRules> rules = api.getApiRules();
                if (rules != null && !rules.isEmpty()) {
                    ruleSets.addAll(rules);
                }
            }
        }
        List<RuleSetLog> ruleSetLogList = new ArrayList<>();
        List<RuleLog> ruleLogList = new ArrayList<>();
        List<WarnResultRule> warnResultRuleList = new ArrayList<>();
        List<VariableLog> variableLogList = new ArrayList<>();
        List<WarnResultVariable> warnResultVariableList = new ArrayList<>();
        String execTime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
        String hitDate = LocalDate.now().toString();
        List<Long> warnResultVariableIdList = new ArrayList<>();
        //收集规则及变量命中信息
        if(!CollectionUtils.isEmpty(ruleSets)){
            Long taskId = Long.valueOf(task.getTaskId());
            Long entityId = Long.valueOf(taskInput.getInputId());
            Long strategyId = Long.valueOf(task.getStrategyId());
            ruleSets.forEach(ruleSet->{
                RuleSetLog ruleSetLog = new RuleSetLog();
                Long ruleSetId = Long.valueOf(ruleSet.getRulesId());
                ruleSetLog.setTaskId(taskId);
                ruleSetLog.setStrategyId(strategyId);
                ruleSetLog.setExecTime(execTime);
                ruleSetLog.setEntityId(entityId);
                ruleSetLog.setRuleSetId(ruleSetId);
                ruleSetLog.setSrcRuleSetId(Long.valueOf(ruleSet.getSrcRulesId()));
                ruleSetLogList.add(ruleSetLog);
                if(!CollectionUtils.isEmpty(ruleSet.getRulesRule())){
                    ruleSet.getRulesRule().forEach(rule->{
                        Long ruleId = Long.valueOf(rule.getRuleId());
                        Long srcRuleId = Long.valueOf(rule.getSrcRuleId());
                        Integer ruleWeight = rule.getRuleWeight();
                        RuleLog ruleLog = new RuleLog();
                        ruleLog.setTaskId(taskId);
                        ruleLog.setStrategyId(strategyId);
                        ruleLog.setExecTime(execTime);
                        ruleLog.setEntityId(entityId);
                        ruleLog.setRuleSetId(ruleSetId);
                        ruleLog.setRuleId(ruleId);
                        ruleLog.setSrcRuleId(Long.valueOf(rule.getSrcRuleId()));
                        ruleLog.setHit(rule.getRuleHit().value());
                        ruleLog.setWeight(ruleWeight);
                        ruleLogList.add(ruleLog);
                        if(EHit.HIT==rule.getRuleHit()){
                            WarnResultRule warnResultRule = new WarnResultRule();
                            warnResultRule.setTaskId(taskId);
                            warnResultRule.setStrategyId(strategyId);
                            warnResultRule.setHitTime(execTime);
                            warnResultRule.setEntityId(entityId);
                            warnResultRule.setRuleSetId(ruleSetId);
                            warnResultRule.setRuleId(ruleId);
                            warnResultRule.setWeight(ruleWeight);
                            warnResultRule.setSrcRuleId(Long.valueOf(rule.getSrcRuleId()));
                            warnResultRuleList.add(warnResultRule);
                        }
                        if(!CollectionUtils.isEmpty(rule.getRuleVars())){
                            rule.getRuleVars().forEach(variable->{
                                Long variableId = variable.getVarId().longValue();
                                Long variableCode = Long.valueOf(variable.getSrcVarId());
                                String periodUnit = EngineUtils.timeUnitToString(variable.getVarTimeUnit());
                                VariableLog variableLog = new VariableLog();
                                variableLog.setTaskId(taskId);
                                variableLog.setVariableId(variableId);
                                variableLog.setRuleId(ruleId);
                                variableLog.setSrcRuleId(srcRuleId);
                                variableLog.setRuleSetId(ruleSetId);
                                variableLog.setStrategyId(strategyId);
                                variableLog.setExecTime(execTime);
                                variableLog.setEntityId(entityId);
                                variableLog.setPeriod(variable.getVarPeriod());
                                variableLog.setPeriodUnit(periodUnit);
                                variableLog.setThreshold(variable.getVarThresholdJson());
                                variableLog.setVariableCode(variableCode);
                                variableLog.setHit(variable.getVarHit().value());
                                variableLog.setTriggerThreshold(variable.getVarValue());
                                if(EHit.HIT == variable.getTaskRule().getRuleHit() &&
                                        EHit.HIT == variable.getVarHit()){
                                    WarnResultVariable warnResultVariable = new WarnResultVariable();
                                    warnResultVariable.setTaskId(taskId);
                                    warnResultVariable.setVariableId(variableId);
                                    warnResultVariable.setStrategyId(strategyId);
                                    warnResultVariable.setHitTime(execTime);
                                    warnResultVariable.setEntityId(entityId);
                                    warnResultVariable.setRuleId(ruleId);
                                    warnResultVariable.setSrcRuleId(srcRuleId);
                                    warnResultVariable.setRuleSetId(ruleSetId);
                                    warnResultVariable.setThreshold(variable.getVarThresholdJson());
                                    warnResultVariable.setVariableCode(variableCode);
                                    warnResultVariable.setTriggerThreshold(variable.getVarValue());
                                    warnResultVariable.setPeriod(variable.getVarPeriod());
                                    warnResultVariable.setPeriodUnit(periodUnit);
                                    warnResultVariable.setDetail(variable.getVarDetail());
                                    warnResultVariable.setDetailExisted(null!=variable.getVarDetail()?1:0);
                                    warnResultVariableList.add(warnResultVariable);

                                }
                                variableLogList.add(variableLog);
                            });
                        }
                    });
                }
            });
            List<VariableDetail> variableDetailList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(warnResultVariableList)){
                try {
                    warnResultVariableDAO.addWarnResultVariableList(warnResultVariableList);
                    //打包detail信息
                    warnResultVariableList.forEach(result -> {
                        warnResultVariableIdList.add(result.getWarnResultVariableId());
                        JSON detail = result.getDetail();
                        if (detail != null) {
                            VariableDetail variableDetail = new VariableDetail();
                            variableDetail.setWarnId(result.getWarnResultVariableId());
                            variableDetail.setVariableId(result.getVariableId());
                            variableDetail.setEntityId(result.getEntityId());
                            variableDetail.setHitDate(hitDate);
                            //压缩数据
                            variableDetail.setVarDetail(ZipUtils.gzip(detail.toJSONString()));
                            variableDetailList.add(variableDetail);
                        }
                    });
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }

            if(!CollectionUtils.isEmpty(variableDetailList)) {
                try {
                    warnResultVariableDAO.insertVariableDetail(variableDetailList);
                    variableDetailList.forEach(variableDetail -> {

                    });
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
            if(!CollectionUtils.isEmpty(variableLogList)){
                try {
                    variableLogDAO.addVariableLogList(variableLogList);
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
            if(!CollectionUtils.isEmpty(warnResultRuleList)){
                try {
                    warnResultRuleDAO.addWarnResultRuleList(warnResultRuleList);
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
            if(!CollectionUtils.isEmpty(ruleLogList)){
                try {
                    ruleLogDAO.addRuleLogList(ruleLogList);
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
            if(!CollectionUtils.isEmpty(ruleSetLogList)){
                try {
                    ruleSetLogDAO.addRuleSetLogList(ruleSetLogList);
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        }

        //更新进件当日和近段时间风险来源
        if(!CollectionUtils.isEmpty(warnResultVariableList)) {
            try {
                updateDailyByEntityId(hitDate, taskInput.getInputId());
                List<TimeScope> timeScope = statisticDAO.queryTimeScope();
                timeScope.forEach(time -> {
                    try {
                        updateNearlyByEntityId(time.getScope(), taskInput.getInputId());
                    } catch (Exception e) {
                        log.error("进件[" + taskInput.getInputId() + "]统计[" + time.getName() + "]变量命中情况失败: " + e.getMessage(), e);
                    }
                });
                log.info("进件[{}]当日和近段时间风险来源统计更新完成!", taskInput.getInputId());
            } catch (Exception e) {
                log.error("进件[" + taskInput.getInputId() + "]当日和近段时间风险来源统计失败: " + e.getMessage(), e);
            }
        }
        if (warnResultVariableIdList.size() > 0) {
            String publicKey = stringRedisTemplate.opsForValue().get("biz_credit:publicKey");
            List<Integer> userList = authService.getRelatedUsers(publicKey, task.getApiCode(), task.getUserId());
            if (userList == null) {
                userList = new ArrayList<>();
            }
            if (userList.isEmpty()) {
                userList.add(task.getUserId());
            }
            log.info("wbs_userList: {}", userList);
            List<WarningNoticeVO> warningNoticeVOList = warningNoticeDAO.findListByWarnResultVariableIdList(warnResultVariableIdList);
            for (WarningNoticeVO warningNoticeVO : warningNoticeVOList) {
                userList.forEach(userId -> {
                    warningNoticeVO.setUserId(userId);
                    warningNoticeDAO.addWarningNoticeUnRead(warningNoticeVO);
                    //推送消息
                    redisNotice.push(userId, warningNoticeVO);
                });
            }
        }
    }

    private void updateDailyByEntityId(String date, Integer entityId) {
        List<StatitscVO> data = statisticDAO.queryVarDailyByEntityId(date, entityId);
        if (data == null || data.isEmpty()) {
            return;
        }
        List<StatitscVO> updateData = new ArrayList<>();
        List<StatitscVO> insertData = new ArrayList<>();
        for (StatitscVO vo : data) {
            if (vo.getId() == null) {
                insertData.add(vo);
            } else {
                updateData.add(vo);
            }
        }
        if (!updateData.isEmpty()) {
            for (StatitscVO vo : updateData) {
                statisticDAO.updateVarDailyByEntity(vo);
            }
        }
        if (!insertData.isEmpty()) {
            statisticDAO.insertVarDailyByEntity(insertData);
        }
    }

    private void updateNearlyByEntityId(String type, Integer entityId) {
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
        List<StatitscVO> data = statisticDAO.queryVarNearlyByEntityId(type, entityId, startTime, endTime);
        if (data == null || data.isEmpty()) {
            return;
        }
        List<StatitscVO> updateData = new ArrayList<>();
        List<StatitscVO> insertData = new ArrayList<>();
        for (StatitscVO vo : data) {
            if (vo.getId() == null) {
                insertData.add(vo);
            } else {
                updateData.add(vo);
            }
        }
        if (!updateData.isEmpty()) {
            for (StatitscVO vo : updateData) {
                statisticDAO.updateVarNearlyByEntity(vo);
            }
        }
        if (!insertData.isEmpty()) {
            statisticDAO.insertVarNearlyByEntity(insertData);
        }
    }
}
