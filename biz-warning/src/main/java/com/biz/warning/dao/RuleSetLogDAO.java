package com.biz.warning.dao;

import com.biz.warning.domain.RuleSetLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleSetLogDAO {
    int addRuleSetLogList(List<RuleSetLog> ruleSetLogList) throws Exception;
}
