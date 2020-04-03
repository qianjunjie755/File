package com.biz.warning.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class WarnResultVariableCountVO implements Serializable {

  private Long variableCode;
  private String variableName;
  private String threshold;
  private Long taskId;
  private Long execCount;
  private Long hitCount;

}
