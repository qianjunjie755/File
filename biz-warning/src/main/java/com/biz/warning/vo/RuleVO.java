package com.biz.warning.vo;

import com.biz.warning.domain.Rule;
import com.biz.warning.domain.RuleSet;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 新增规则页面传参
 */
@Data
@NoArgsConstructor
public class RuleVO extends Rule implements Serializable {
    //规则
    //private Rule rule;
    //规则集List
    private List<Long> ruleSetIdList;
    private String userName;
    //变量List
    private List<VariableVO> variableList;
    //规则集
    private RuleSet ruleSet;
    private Long ruleSetId;
    private List<RuleVO> versionList;
    private Long strategyId;
    private RuleVO recommendRule;//推荐的规则
    private Long accHitCount;
    public RuleVO (Long ruleId){
        super.setRuleId(ruleId);
    }
    public RuleVO (Long userId,String apiCode){
        setUserId(userId);setApiCode(apiCode);
    }
}
