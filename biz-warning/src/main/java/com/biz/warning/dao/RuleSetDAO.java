package com.biz.warning.dao;

//import com.biz.warning.domain.Rule;
//import com.biz.warning.domain.RuleSet;
import com.biz.warning.domain.RulesetRule;
import com.biz.warning.vo.RuleSetVO;
import com.biz.warning.vo.RuleVO;
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

    RuleSetVO findRuleSetByRuleId(@Param("ruleId") Long ruleId, @Param("apiCode") String apiCode);
    RuleSetVO findRuleSetByRuleSet(@Param("ruleSet") RuleSetVO ruleSet);

    List<RuleSetVO> findRuleSetByParam(@Param("ruleSet") RuleSetVO ruleSet);

    /**
     * 按规则集查找所有规则（规则模块）
     * @param
     * @return
     */
    List<RuleVO> findRuleListByRuleSet(@Param("ruleSetVO") RuleSetVO ruleSetVO);

    RuleSetVO findRuleSetById(@Param("ruleSetId") long ruleSetId);

    /**
     * 复制策略时更新规则集的src_rule_set_id为rule_set_id
     * @param ruleSetVO
     * @return
     */
    long updateRuleSetForCopyStrategy(@Param("ruleSetVO") RuleSetVO ruleSetVO);

    RuleSetVO findOriginRuleSet(@Param("ruleSetVO") RuleSetVO ruleSetVO);

    List<RuleVO> findActiveRuleByRuleSet(@Param("ruleSetVO") RuleSetVO ruleSetVO);
}
