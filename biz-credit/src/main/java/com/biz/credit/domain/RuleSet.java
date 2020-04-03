package com.biz.credit.domain;


import lombok.Data;

import java.io.Serializable;

@Data
public class RuleSet implements Serializable {

  private Long ruleSetId;
  private String ruleSetName;
  private String ruleSetCode;
  private Long calcLogic;
  private Long userId;
  private String apiCode;
  private Long srcRuleSetId;
  private Long isTemplate;
  private String apiProdCode;
  private Double apiVersion;
  private String description;
  private String lastUpdateTime;
  private String createTime;
  private Integer priority;

  public String getRuleSetName(){
    if(null!=ruleSetName){
        ruleSetName = ruleSetName.trim();
    }
    return ruleSetName;
  }
}
