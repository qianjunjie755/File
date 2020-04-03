package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

@Slf4j
@Setter
@Getter
public class ScoreApi implements Serializable {

  private Long scoreApiId;
  private String apiProdCode;
  private String apiVersion;
  private Long scoreCardId;
  private Integer status;
  private Long varId;
  private String varCode;
  private String varName;
  private String varVersion;
  private BigDecimal defaultValue;
  private Integer feq;
  private String feqUnit;
  private String modelParamName;
  private BigDecimal weight;
  private Integer conditionType;
  private String createTime;
  private String lastUpdateTime;
  private String dimensionName;

}
