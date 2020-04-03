package com.biz.credit.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Api implements Serializable {

  private Long id;
  private String prodCode;
  private Double version;
  private String prodName;
  private Integer apiType;
  private Integer advertising;
  private String createT;
  private String updateT;
  private Integer query;
  private String introduction;
  private String dataDescription;
  private String description;
  private String reqParams;
  private String respParams;
  private Double price;
  private Double cost;
  private Integer testCount;
  private String updateP;
  private String createP;
  private Integer apiReqType;
  private Integer platform;
  private Integer isDeleted;
  private String prodType;
  private String varsetProdCode;
  private Double varsetVersion;
  private String sourceQueryJsonData;
  private String varsetQueryJsonData;
  private String sourceResultJsonData;
  private String varsetResultJsonData;
  private Integer hasVarset;

}
