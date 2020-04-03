package com.biz.warning.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class VariableParamPool implements Serializable {

  private Long paramCode;
  private String paramName;
  private String paramTemplateCode;
  private String handler;
  private String description;
  private String lastUpdateTime;
  private String createTime;

}
