package com.biz.warning.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class TaskLog implements Serializable {

  private Long taskLogId;
  private String execTime;
  private Long strategyId;
  private Long taskId;
  private Long entityId;
  private String description;
  private String lastUpdateTime;
  private String createTime;

}
