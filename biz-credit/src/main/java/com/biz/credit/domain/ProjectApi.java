package com.biz.credit.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class ProjectApi implements Serializable {

  private String apiCode;
  private String prodCode;
  private Double version;
  private String description;
  private String validStart;
  private String validEnd;
  private Long dailyMax;
  private Long freeCount;
  private String freeEnd;
  private Integer billingRules;
  private String stepwiseQuotation;
  private Double clientPrice;
  private String createTime;
  private String updateTime;
  private Integer chargeMode;
  private Integer chargeMode2;
  private Integer cached;
  private Integer valid;



}
