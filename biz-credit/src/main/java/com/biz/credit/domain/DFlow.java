package com.biz.credit.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class DFlow implements Serializable {

  private Long flowId;
  private String flowName;
  private String flowDesc;
  private Long radarId;
  private Integer relatedP;
  private Integer platformId;
  private Integer bizId;
  private Integer linkId;
  private String apiCode;
  private Long userId;
  private Integer status;
  private String updateTime;
  private String createTime;

}
