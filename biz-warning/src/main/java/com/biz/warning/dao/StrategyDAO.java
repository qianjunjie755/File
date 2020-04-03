package com.biz.warning.dao;

import com.biz.warning.domain.StrategyRuleSet;
import com.biz.warning.vo.RuleSetVO;
import com.biz.warning.vo.StrategyVO;
import com.biz.warning.vo.TaskVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyDAO {

    long addStrategy(@Param("strategy") StrategyVO strategy);
    long deleteStrategy(@Param("strategyId") Long strategyId);

    long relateRuleSet(@Param("strategyRuleSet") StrategyRuleSet strategyRuleSet);
    long deleteRelateRuleSet(@Param("strategyRuleSet") StrategyRuleSet strategyRuleSet);
    long relateRuleSetList(List<StrategyRuleSet> strategyRuleSetList);

    long updateStrategy(@Param("strategy") StrategyVO strategy);

    long updateStrategyState(@Param("strategyId") Long strategyId, @Param("strategyStatus") Long strategyStatus);
    long updateStrategyForCopyStrategy(@Param("strategy") StrategyVO strategy);
    StrategyVO findStrategy(@Param("strategyId") Long strategyId, @Param("userId") Long userId, @Param("apiCode") String apiCode);

    //批量删除策略、规则集关联关系
    long deleteRuleSetByStrategy(@Param("strategy") StrategyVO strategy);

    //根据策略查找下属规则集
    List<RuleSetVO> findRuleSetByStrategy(@Param("strategy") StrategyVO strategy);
    //查找所有策略（策略标注为模板，当前用户）
    List<StrategyVO> findAllStrategy(@Param("strategy") StrategyVO strategy);


    long updateStrategyTemplateState(@Param("strategyId") Long strategyId, @Param("strategyTemplateState") Long strategyTemplateStatus);

    /**
     * 根据策略编号/策略名/规则集查询策略列表
     * @param strategy
     * @param param
     * @return
     */
    List<StrategyVO> findStrategyByParam(@Param("strategy") StrategyVO strategy, @Param("param") String param);

    List<StrategyVO> findAllStrategyNotTask(@Param("strategy") StrategyVO strategy);


    List<TaskVO> findTaskBySrcStrategy(@Param("strategy") StrategyVO strategy);
}
