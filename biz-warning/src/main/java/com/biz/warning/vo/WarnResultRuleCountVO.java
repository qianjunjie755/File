package com.biz.warning.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class WarnResultRuleCountVO implements Serializable {

  private Long srcRuleId;
  private String ruleCode;
  private String ruleName;
  private String description;
  private Long execCount;
  private Long hitCount;

}
