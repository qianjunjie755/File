package com.biz.warning.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class VariableTypePool implements Serializable {

  private Long variableTypeCode;
  private String firstTypeName;
  private String secondTypeName;
  private String description;
  private String lastUpdateTime;
  private String createTime;

}
