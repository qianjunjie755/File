package com.biz.warning.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class VariableParam implements Serializable {

  private Long variableParamId;
  private Long variableCode;
  private Long paramId;

}
