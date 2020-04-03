package com.biz.credit.dao;

import com.biz.credit.vo.RuleVO;
import com.biz.credit.vo.VariableVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleDAO {
    /**
     * 新增规则
     * @param  rule
     * @return
     */
    long addRule(@Param("rule") RuleVO rule);

    /**
     * 新增规则
     * @param  ruleList
     * @return
     */
    long addRuleList(List<RuleVO> ruleList);

    /**
     * 修改规则
     * @param  rule
     * @return
     */
    long updateRule(@Param("rule") RuleVO rule);

    /**
     * 删除规则
     * @param  rule
     * @return
     */
    long deleteRule(@Param("rule") RuleVO rule);

    /**
     * 修改规则状态
     * @param  ruleId
     * @param  ruleState
     * @return
     */
    long updateRuleState(@Param("ruleId") Long ruleId, @Param("ruleState") Long ruleState);
    /**
     * 修改规则逻辑
     * @param  rule
     * @return
     */
    long updateRuleCalcLogic(@Param("rule") RuleVO rule);
    /**
     * 查找规则实例
     * @param
     * @return
     */
    List<RuleVO> findAllRule(@Param("rule") RuleVO rule);

    /**
     * 查找规则实例
     * @param  ruleId
     * @return
     */
    RuleVO findRuleById(@Param("ruleId") Long ruleId, @Param("userId") Long userId, @Param("apiCode") String apiCode);



    /**
     * 按规则编号查找所有版本
     * @param  ruleId
     * @return
     */
    List<RuleVO> findRuleVersion(@Param("ruleId") Long ruleId, @Param("apiCode") String apiCode);

    /**
     * 查询所有规则（不分版本）
     * @param
     * @return
     */
    List<RuleVO> findRules(@Param("rule") RuleVO rule);

    /**
     * 查找规则关联的变量数
     * @param  ruleId
     * @return
     */
    Integer findRelatedVariables(@Param("ruleId") Long ruleId);

    List<VariableVO> findVariableByRuleId(@Param("ruleId") Long ruleId);

    List<RuleVO> findRuleVersionByRuleId(@Param("ruleId") Long ruleId);

    Long findMaxRuleVersion(@Param("ruleCode") String ruleCode);
    long updateRuleForCopyStrategy(@Param("rule") RuleVO rule);
}
