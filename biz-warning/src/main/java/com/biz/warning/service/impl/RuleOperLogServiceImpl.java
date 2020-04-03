package com.biz.warning.service.impl;

import com.biz.warning.dao.RuleOperLogDAO;
import com.biz.warning.domain.RuleOperLog;
import com.biz.warning.service.IRuleOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleOperLogServiceImpl implements IRuleOperLogService {

    @Autowired
    private RuleOperLogDAO dao;

    @Override
    public long addRuleOperLog(RuleOperLog ruleOperLog) {
        return dao.addRuleOperLog(ruleOperLog);
    }

    @Override
    public List<RuleOperLog> findRuleOperLog(Long ruleId) {
        return dao.findRuleOperLog(ruleId);
    }

}
