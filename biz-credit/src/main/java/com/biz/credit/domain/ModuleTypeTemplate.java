package com.biz.credit.domain;


import lombok.Data;

import java.io.Serializable;

@Data
public class ModuleTypeTemplate implements Serializable {

  private Integer moduleTypeId;
  private String moduleTypeName;
  private Integer parentId;
  private Integer typeCode;
  private Integer parentCode;
  private Integer  isLast;
  private Integer  orderNo;
  private Integer  moduleId;
  private String lastUpdateTime;
  private String createTime;
  private String reqUrl;
  private String prodCode;
  private  int reportType;
}
