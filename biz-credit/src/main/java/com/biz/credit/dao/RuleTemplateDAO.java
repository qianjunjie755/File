package com.biz.credit.dao;

import com.biz.credit.domain.RuleTemplate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleTemplateDAO {
    List<RuleTemplate> findListByModuleTypeId(@Param("moduleTypeId") Integer moduleTypeId) throws Exception;
    int addRuleTemplateByList(List<RuleTemplate> ruleTemplateList) throws Exception;
}
