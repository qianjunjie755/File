package com.biz.warning.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@NoArgsConstructor
public class Rule implements Serializable {

  private Long ruleId;
  private String ruleCode;
  private String ruleName;
  private String businessCode = StringUtils.EMPTY;
  private String version;
  private Long calcLogic;
  private Long userId;
  private String apiCode;
  private Long srcRuleId;
  private String srcRuleCode;
  private Long ruleState;
  private Long isTemplate;
  private String apiProdCode;
  private String apiVersion;
  private String description;
  private String lastUpdateTime;
  private String createTime;

  public Rule(Long ruleId){
    this.ruleId=ruleId;
  }

  public void setBusinessCode(String businessCode) {
    if(StringUtils.isNotEmpty(businessCode))
    this.businessCode = businessCode;
  }
}


