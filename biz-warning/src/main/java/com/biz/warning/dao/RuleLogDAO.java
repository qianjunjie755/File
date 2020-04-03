package com.biz.warning.dao;

import com.biz.warning.domain.RuleLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleLogDAO {
    int addRuleLogList(List<RuleLog> ruleLogList)throws Exception;
}
