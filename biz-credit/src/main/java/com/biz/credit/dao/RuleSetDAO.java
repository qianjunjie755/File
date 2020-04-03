package com.biz.credit.dao;

import com.biz.credit.domain.RulesetRule;
import com.biz.credit.vo.RuleSetVO;
import com.biz.credit.vo.RuleVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleSetDAO {
    /**
     * 新增规则集接口
     * @param ruleSet
     * @return
     */
    long addRuleSet(@Param("ruleSet") RuleSetVO ruleSet);

    /**
     * 新增规则集接口
     * @param ruleSetList
     * @return
     */
    long addRuleSetList(List<RuleSetVO> ruleSetList);

    /**
     * 修改规则集接口
     * @param ruleSet
     * @return
     */
    long updateRuleSet(@Param("ruleSet") RuleSetVO ruleSet);

    /**
     * 修改规则集接口
     * @param ruleSet
     * @return
     */
    long deleteRuleSet(@Param("ruleSet") RuleSetVO ruleSet);

    /**
     * 查找所有规则集接口
     * @param
     * @return
     */
    List<RuleSetVO> findAllRuleSet(@Param("ruleSet") RuleSetVO ruleSet);

    /**
     * 关联规则集——规则对应关系
     * @param  rulesetRule
     * @return
     */
    long relateRule(@Param("rulesetRule") RulesetRule rulesetRule);

    /**
     * 关联规则集——规则对应关系
     * @param  rulesetRuleList
     * @return
     */
    long relateRuleList(List<RulesetRule> rulesetRuleList);


    /**
     * 删除规则集实例下面关联的所有规则实例
     * @param  ruleSet
     * @return
     */
    long deleteRulesByRuleSet(@Param("ruleSet") RuleSetVO ruleSet);
    long deleteRulesetRule(@Param("ruleSetRule") RulesetRule RulesetRule);

    /**
     * 删除规则集——规则对应关系（模板）
     * @param  rule
     * @return
     */
    long deleteRules(@Param("rule") RuleVO rule);

    /**
     * 按规则集查找所有规则模板接口
     * @param
     * @return
     */
    List<RuleVO> findRulesByRuleSet(@Param("ruleSetVO") RuleSetVO ruleSetVO);

    List<RuleSetVO> findRuleSetListForTaskByStrategyId(@Param("strategyId") Integer strategyId) throws Exception;

    RuleSetVO findRuleSetByRuleId(@Param("ruleId") Long ruleId);
    RuleSetVO findRuleSetByRuleSet(@Param("ruleSet") RuleSetVO ruleSet);

    List<RuleSetVO> findRuleSetByParam(@Param("ruleSet") RuleSetVO ruleSet);

    /**
     * 按规则集查找所有规则（规则模块）
     * @param
     * @return
     */
    List<RuleVO> findRuleListByRuleSet(@Param("ruleSetVO") RuleSetVO ruleSetVO);

    /**
     * 复制策略时更新规则集的src_rule_set_id为rule_set_id
     * @param ruleSetVO
     * @return
     */
    long updateRuleSetForCopyStrategy(@Param("ruleSetVO") RuleSetVO ruleSetVO);
}
