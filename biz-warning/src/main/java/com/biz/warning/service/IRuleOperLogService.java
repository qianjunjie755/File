package com.biz.warning.service;

import com.biz.warning.domain.RuleOperLog;

import java.util.List;

/**
 * 规则操作日志接口
 */
public interface IRuleOperLogService {
    long addRuleOperLog(RuleOperLog ruleOperLog);
    List<RuleOperLog> findRuleOperLog(Long ruleId);
}
