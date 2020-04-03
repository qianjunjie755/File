package com.biz.credit.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class DNodeModel implements Serializable {

  private Long modelCode;
  private Integer modelType;
  private Long modelId;
  //private Long modelStatus;
  private Long nodeId;
  private Long apiCode;
  private Integer status;
  private String updateTime;
  private String createTime;


}
