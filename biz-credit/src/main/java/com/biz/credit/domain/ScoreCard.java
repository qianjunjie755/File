package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

@Slf4j
@Setter
@Getter
public class ScoreCard implements Serializable {

  private Long scoreCardId;
  private Long projectId;
  private String cardCode;
  private String cardName;
  private String cardVersion;
  private String createTime;
  private BigDecimal scoreBase;
  private Integer calculateType;
  private Integer settingWeight;
  private Integer status;
  private Integer publish;
  private String cardDescription;
  private String lastUpdateTime;
  private Integer scoreBoundaryType;
  private Integer scoreCardType;
}
