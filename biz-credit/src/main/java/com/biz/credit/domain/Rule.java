package com.biz.credit.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Rule implements Serializable {

    private Long ruleId;
    private String ruleCode;
    private String ruleName;
    private String businessCode;
    private String version;
    private Long calcLogic;
    private Long userId;
    private String apiCode;
    private Long srcRuleId;
    private Long ruleState;
    private Long isTemplate;
    private String apiProdCode;
    private String apiVersion;
    private String description;
    private String lastUpdateTime;
    private String createTime;
    private Integer weight;

    public Rule(Long ruleId){
        this.ruleId=ruleId;
    }
}
