package com.biz.warning.dao;

import com.biz.warning.domain.RuleOperLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleOperLogDAO {
    long addRuleOperLog(@Param("ruleOperLog") RuleOperLog ruleOperLog);
    List<RuleOperLog> findRuleOperLog(@Param("ruleId") Long ruleId);
}
