package com.biz.credit.domain;


import lombok.Data;

import java.io.Serializable;

@Data
public class VariablePool implements Serializable {

  private Long variableCode;
  private String variableName;
  private Long variableTypeCode;
  private Double version;
  private String dataType;
  private String defaultThreshold;
  private String dataSource;
  private Long userId;
  private Integer valid;
  private String apiProdCode;
  private Double apiVersion;
  private String description;
  private String lastUpdateTime;
  private String createTime;
  private String prodCode;
  private String queryUrl;
  private String resultUrl;
  private Integer priority;


}
