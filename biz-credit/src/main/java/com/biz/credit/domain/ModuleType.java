package com.biz.credit.domain;


import lombok.Data;

import java.io.Serializable;

@Data
public class ModuleType implements Serializable {

  private Integer moduleTypeId;
  private String moduleTypeName;
  private String htmlTemplateName;
  private String description;
  private String excelTemplateName;
  private Integer reportType;
  private String columnHead;
  private String columnHeadPerson;
  private String lastUpdateTime;
  private String createTime;
  private Integer isTemplate;
  private Long strategyId = -1l;
  private Long radarModelId;
  private String prodCode;
  private String prodName;
  private Long flowId;

  public void setStrategyId(Long strategyId) {
    if (null == strategyId)
      return;
    this.strategyId = strategyId;
  }
}
