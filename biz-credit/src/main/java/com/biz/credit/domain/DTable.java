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
public class DTable implements Serializable {

  private Long tableId;
  private String tableName;
  private Double tableVersion=1.0;
  private String tableDesc;
  private Integer returnType=1;
  private String returnField;
  private String fieldType;
  private Long projectId;
  private String apiCode;
  private Long userId;
  private Integer status;
  private String updateTime;
  private String createTime;

  public String getTableVersion(){
    return new BigDecimal(tableVersion).setScale(1, RoundingMode.HALF_UP).toString();
  }

}
