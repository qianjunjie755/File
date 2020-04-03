package com.biz.warning.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@NoArgsConstructor
public class RulesetRule implements Serializable {

  private Long rulesetRuleId;
  private Long ruleSetId;
  private Long ruleId;
  private String description;
  private String lastUpdateTime;
  private String createTime;

  public RulesetRule(Long ruleSetId,Long ruleId){
    this.ruleSetId = ruleSetId ; this.ruleId=ruleId;
  }
}
