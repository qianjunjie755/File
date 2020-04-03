package com.biz.credit.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ApiParamCfg implements Serializable {
  private String paramCode;
  private String paramName;
  private Integer paramType;
  private Long paramOrder;
  private Integer riskParamType;
  private Integer status;
  private String createTime;
}
