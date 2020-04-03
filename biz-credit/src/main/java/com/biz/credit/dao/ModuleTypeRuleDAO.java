package com.biz.credit.dao;

import com.biz.credit.domain.ModuleTypeRule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleTypeRuleDAO {
    int addModuleRuleList(List<ModuleTypeRule> moduleTypeRuleList) throws Exception;
}
