package com.biz.warning.service;

import com.biz.warning.vo.RuleSetVO;
import com.biz.warning.vo.StrategyVO;

import java.util.List;

/**
 * 策略接口
 */
public interface IStrategyService {
    /**
     * 新增策略模板(包含：策略、策略规则集、规则集、规则)
     * @param strategyVO
     * @return
     */
    long addStrategy(StrategyVO strategyVO, String key);


    /**
     * 根据策略编号，修改策略表数据(包含：策略、策略规则集、规则集、规则)
     * @param strategyVO
     * @return
     */
    long updateStrategy(StrategyVO strategyVO, String key);

    /**
     * 根据策略编号，修改策略状态
     * @param strategyId
     * @param strategyStatus
     * @return
     */
    long updateStrategyState(Long strategyId, Long strategyStatus);


    /**
     * 根据策略编号读取策略信息(返回策略表信息 单表)
     * @param strategyId
     * @return
     */
    StrategyVO findStrategy(Long strategyId, Long userId, String apiCode);

    /**
     * 根据策略编号读取策略信息(返回策略表信息 单表)
     * @param strategyId
     * @return
     */
    StrategyVO findRuleSetsByStrategy(Long strategyId, Long userId, String apiCode);

    /**
     * 读取所有策略模板信息
     * @param Strategy
     * @return
     */
    List<StrategyVO> findAllStrategy(StrategyVO Strategy);

    /**
     * 修改策略模板状态
     * @param strategyId
     * @param strategyTemplateStatus
     * @return
     */
    long updateStrategyTemplateState(Long strategyId, Long strategyTemplateStatus);

    List<RuleSetVO> findRuleSetListByStrategyId(Long strategyId);

    List<StrategyVO> findStrategyByParam(StrategyVO strategy, String param);

    /**
     * 查询不和任务关联的所有策略
     * @param strategy
     * @return
     */
    List<StrategyVO> findAllStrategyNotTask(StrategyVO strategy);

    boolean isRelationByOther(StrategyVO strategy);
}
