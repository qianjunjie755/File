package com.biz.warning.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class VariableLog implements Serializable {

  private Long variableLogId;
  private String execTime;
  private Long ruleId;
  private Long srcRuleId;
  private Long ruleSetId;
  private Long strategyId;
  private Long variableId;
  private Long variableCode;
  private String threshold;
  private Long taskId;
  private Long entityId;
  private String description;
  private String lastUpdateTime;
  private String createTime;
  private Integer hit;
  private String triggerThreshold;
  private Integer period;
  private String periodUnit;


}
