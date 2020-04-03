package com.biz.warning.domain;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class WarnResultVariable implements Serializable {

  private Long warnResultVariableId;
  private String hitTime;
  private Long ruleId;
  private Long srcRuleId;
  private Long ruleSetId;
  private Long strategyId;
  private Long variableId;
  private Long variableCode;
  private String threshold;
  private String triggerThreshold;
  private Long taskId;
  private Long entityId;
  private String description;
  private String lastUpdateTime;
  private String createTime;
  private Integer period;
  private String periodUnit;
  private JSON detail;
  private Integer detailExisted = 1;

  public void setDetailExisted(Integer detailExisted) {
    if(null!=detailExisted)
      this.detailExisted = detailExisted;
  }
}
