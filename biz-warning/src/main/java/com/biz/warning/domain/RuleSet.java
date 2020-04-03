package com.biz.warning.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
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
  private Integer priority = 1;
  private Integer origin;

  public String getRuleSetName(){
    if(null!=ruleSetName){
        ruleSetName = ruleSetName.trim();
    }
    return ruleSetName;
  }

  public void setPriority(Integer priority){
    if(null!=priority)
      this.priority = priority;
  }
}
