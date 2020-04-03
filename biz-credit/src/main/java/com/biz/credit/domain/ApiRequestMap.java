package com.biz.credit.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ApiRequestMap implements Serializable {
  private String paramCode;
  private String paramName;
  private Integer paramType;
  private Long paramOrder;
  private String createTime;
}
