package com.biz.credit.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Setter
@Getter
@NoArgsConstructor
public class DTree implements Serializable {
  private Long treeId;
  private String treeName;
  private Double treeVersion=1.0;
  private Integer versionType;
  private String treeDesc;
  private Integer returnType=1;
  private String returnField;
  private String fieldType;
  private String apiCode;
  private Long projectId;
  private Long userId;
  private Integer status;
  private String updateTime;
  private String createTime;
  private Integer hasPeriod = 1;

  public String getTreeVersion(){
    return new BigDecimal(treeVersion).setScale(1, RoundingMode.HALF_UP).toString();
  }

  public void setHasPeriod(Integer hasPeriod) {
    if(null!=hasPeriod)
    this.hasPeriod = hasPeriod;
  }
}
