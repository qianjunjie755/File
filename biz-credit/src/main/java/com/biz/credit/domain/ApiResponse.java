package com.biz.credit.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse implements Serializable {

  private Long id;
  private Long apiId;
  private String paramCode;
  private String paramType;
  private Integer length;
  private String paramName;
  private String paramDesc;
  private Long parentId;
  private Integer status;
  private String createTime;
  private String updateTime;

}
