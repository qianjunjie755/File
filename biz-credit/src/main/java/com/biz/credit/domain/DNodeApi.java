package com.biz.credit.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class DNodeApi implements Serializable {

  private Long id;
  private String apiProdCode;
  private String apiVersion;
  private Long nodeId;
  private Integer status;
  private String updateTime;
  private String createTime;

}
