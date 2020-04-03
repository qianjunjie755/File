package com.biz.warning.dao;

import com.biz.warning.domain.WarnResultRule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarnResultRuleDAO {
    int addWarnResultRuleList(List<WarnResultRule> warnResultRuleList) throws Exception;
}
