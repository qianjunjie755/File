package com.biz.warning.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@NoArgsConstructor
public class StrategyRuleSet implements Serializable {

  private Long strategyRuleSetId;
  private Long strategyId;
  private Long ruleSetId;
  private String description;
  private String lastUpdateTime;
  private String createTime;

  public StrategyRuleSet (Long strategyId,Long ruleSetId){
    this.strategyId = strategyId;this.ruleSetId = ruleSetId;
  }

}
