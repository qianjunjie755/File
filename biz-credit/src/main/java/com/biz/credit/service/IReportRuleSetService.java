package com.biz.credit.service;

/**
 * 规则集接口
 */
public interface IReportRuleSetService {
    /**
     * 通过策略编号查询进件excel表头
     * @param strategyId
     * @return
     * @throws Exception
     */
    String findColumnHeaderByStrategyId(Long strategyId) throws Exception;
}
