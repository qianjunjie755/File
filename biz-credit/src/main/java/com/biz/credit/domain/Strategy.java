package com.biz.credit.domain;


import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@Data
public class Strategy implements Serializable {

  private Long strategyId;
  private String strategyCode;
  private String version = "1.0";
  private String businessCode;
  private String strategyName;
  private Long strategyStatus;
  private String taskDescription;
  private Long calcLogic;
  private Long userId;
  private String apiCode;
  private Long sourceStrategyId;
  private Long isTemplate;
  private String description = "";
  private String lastUpdateTime;
  private String createTime;
  private Integer industryId;
  private Integer doScore;

  public void setDescription(String description) {
    if(description != null)
        this.description = description;
  }

  public void setVersion(String version){
    if(StringUtils.isNotEmpty(version))
      this.version=version;
  }
}
