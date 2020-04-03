package com.biz.warning.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Variable implements Serializable {

  private Long variableId;
  private Long variableCode;
  private Long userId;
  private String apiCode;
  private Long isTemplate;
  private Long ruleId;
  private Long variableTypeCode;
  private Long frequencyCode;
  private String threshold;
  private Long period;
  private String apiProdCode;
  private String apiVersion;
  private String description;
  private String lastUpdateTime;
  private String createTime;
  private  Long modified;
  private String periodUnit = "m";


  public void setPeriodUnit(String periodUnit) {
    if(StringUtils.isNotEmpty(periodUnit))
      this.periodUnit = periodUnit;
  }
}
