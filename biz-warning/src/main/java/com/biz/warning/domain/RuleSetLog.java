package com.biz.warning.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class RuleSetLog implements Serializable {

  private Long ruleSetLogId;
  private String execTime;
  private Long ruleSetId;
  private Long srcRuleSetId;
  private Long strategyId;
  private Long taskId;
  private Long entityId;
  private String description;
  private String lastUpdateTime;
  private String createTime;

}
