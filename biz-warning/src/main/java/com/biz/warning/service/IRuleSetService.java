package com.biz.warning.service;

import com.biz.warning.domain.RuleSet;
import com.biz.warning.vo.RuleSetVO;
import com.biz.warning.vo.RuleVO;

import java.util.List;

/**
 * 规则集模板接口
 */
public interface IRuleSetService {
    long addRuleSet(RuleSetVO ruleSet);

    List<RuleSetVO> findAllRuleSet(RuleSetVO ruleSet);

    List<RuleVO> findRulesByRuleSet(RuleSetVO ruleSetVO);

    List<RuleVO> findRulesByRuleSetForTask(RuleSetVO ruleSetVO);

    RuleSet findRuleSetByRuleId(Long ruleId, String apiCode);

    List<RuleSetVO> findRuleSetByParam(RuleSetVO ruleSet);

    List<RuleVO> findActiveRuleByRuleSet(RuleSetVO ruleSetVO);
}

