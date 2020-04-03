package com.biz.credit.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRuleSetDAO {
    List<String> findApiListByStrategyId(@Param("strategyId") Long strategyId) throws Exception;
}
