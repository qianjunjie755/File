package com.biz.credit.dao;

import com.biz.credit.vo.RuleVariableVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleVariableDAO  {
    int addRuleVariableByRuleTempalte(@Param("ruleVariableVO") RuleVariableVO ruleVariableVO);
}
