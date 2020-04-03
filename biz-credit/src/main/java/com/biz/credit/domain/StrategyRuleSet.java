package com.biz.credit.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
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
