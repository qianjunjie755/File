package com.biz.warning.dao;

import com.biz.warning.domain.HitProcessr;
import com.biz.warning.vo.RuleVO;
import com.biz.warning.vo.VariableVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * @param  ruleId
     * @param  ruleState
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
    List<RuleVO> findRuleVersion(@Param("ruleId") Long ruleId, @Param("apiCode") String apiCode, @Param("srcRuleCode") String srcRuleCode, @Param("version") String version);

    /**
     * 查询所有规则（不分版本）
     * @param
     * @return
     */
    List<RuleVO> findRules(@Param("rule") RuleVO rule);

    List<VariableVO> findVariableByRuleId(@Param("ruleId") Long ruleId);

    Float findMaxRuleVersion(@Param("ruleCode") String ruleCode);

    long updateRuleForCopyStrategy(@Param("rule") RuleVO rule);

    List<RuleVO> findRuleVersionByRuleCode(@Param("ruleCode") String ruleCode, @Param("apiCode") String apiCode);

    List<RuleVO> findAllRuleVersion();

    int updateHitCountByRuleId(@Param("ruleId") Long ruleId, @Param("hitCount") Integer hitCount);

    List<RuleVO> findHitRuleMostListByUserId(@Param("userId") Integer userId);

    List<RuleVO> findHitRuleMostList(@Param("apiCode") String apiCode);

    List<RuleVO> findHitRuleMostListByUserIds(@Param("apiCode") String apiCode, @Param("list") List<Integer> userIds);

    RuleVO findRuleByRuleCodeAndVersion(@Param("ruleCode") String ruleCode, @Param("version") Double version, @Param("apiCode") String apiCode);
    RuleVO findRuleForChangeVersion(@Param("ruleVO") RuleVO ruleVO);

    List<RuleVO> findActiveRuleVersionByRuleCode(@Param("ruleCode") String ruleCode, @Param("apiCode") String apiCode);

    RuleVO findRuleByRuleSetAndRuleName(@Param("ruleSetId") Integer ruleSetId, @Param("ruleCode") String ruleCode, @Param("ruleName") String ruleName);

    List<HitProcessr> findRiskHitSituaion(@Param("apiCode") String apiCode, @Param("list") List<Integer> userIds, @Param("sourceIds") String[] sourceIds, @Param("beginDay") LocalDate beginDay, @Param("toDay") LocalDateTime toDay);

    List<HitProcessr> findRiskCompany(@Param("apiCode") String apiCode, @Param("list") List<Integer> userIds, @Param("sourceIds") String[] sourceIds, @Param("beginDay") LocalDate localDate, @Param("toDay") LocalDateTime toDay);

    List<HitProcessr> findRiskRuleSet(@Param("apiCode") String apiCode, @Param("list") List<Integer> userIds, @Param("sourceIds") String[] sourceIds, @Param("beginDay") LocalDate localDate, @Param("toDay") LocalDateTime toDay);

    List<HitProcessr> findRiskRule(@Param("apiCode") String apiCode, @Param("list") List<Integer> userIds, @Param("sourceIds") String[] sourceIds, @Param("beginDay") LocalDate localDate, @Param("toDay") LocalDateTime toDay);

}
