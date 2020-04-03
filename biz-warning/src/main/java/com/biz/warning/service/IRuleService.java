package com.biz.warning.service;

import com.biz.warning.util.RespEntity;
import com.biz.warning.vo.RuleVO;
import com.biz.warning.vo.VariableVO;

import java.util.List;
import java.util.Map;

/**
 * 规则接口
 */
public interface IRuleService {
    RespEntity addRule(RuleVO ruleVO);

    RuleVO findRule(Long ruleId, Long userId, String apiCode);
    RuleVO findRuleForTask(RuleVO ruleVO);
    RespEntity updateRule(RuleVO ruleVO);
    long updateRuleForTask(RuleVO ruleVO);
    RespEntity updateRule(RuleVO rule, boolean cancelOperLog);

    long updateRuleState(Long ruleId, Long ruleState);
    long updateRuleCalcLogic(RuleVO ruleVO);

    //查找所有规则（不分版本）
    List<RuleVO> findRules(RuleVO rule);

    //查找所有规则（全部版本）
    List<RuleVO> findAllRule(RuleVO rule);

    long effectiveRule(RuleVO ruleVO);

    List<VariableVO> findVariableByRuleId(Long ruleId);

    List<RuleVO> findRuleVersionByRuleId(RuleVO ruleVO);

    Float findMaxRuleVersion(String ruleCode);

    List<RuleVO> findRuleVersionByRuleCode(String ruleCode, String apiCode);

    Map<String,List<RuleVO>> findAllRuleVersion();

    RuleVO findRuleByRuleCodeAndVersion(String ruleCode, Double version, String apiCode);

    boolean checkRuleNameByRuleSetId(Integer ruleSetId, String ruleCode, String ruleName);
}
