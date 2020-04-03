package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DRule {
    private Long ruleId;
    private String ruleCode;
    private String ruleName;
    private String ruleVersion;
    private Integer ruleType;
    private String ruleDesc;
    private Integer ruleWeight;
    private Integer calcLogic;
    private String apiProdCode;
    private String apiVersion;
    private Integer userId;
}
