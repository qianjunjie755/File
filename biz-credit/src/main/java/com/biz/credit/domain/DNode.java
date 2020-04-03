package com.biz.credit.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class DNode implements Serializable{

  private Long nodeId;
  private String nodeName;
  private String nodeDesc;
  private Long nodeOrder;
  private Long flowId;
  private Long execNextNode;
  private Integer status;
  private String updateTime;
  private String createTime;

}
