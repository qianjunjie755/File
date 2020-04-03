package com.biz.credit.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ApiRequest implements Serializable {

  private Long id;
  private Long apiId;
  private String paramCode;
  private String paramType;
  private Integer required;
  private String paramName;
  private String paramDesc;
  private long status;
  private String createTime;
  private String updateTime;

}
