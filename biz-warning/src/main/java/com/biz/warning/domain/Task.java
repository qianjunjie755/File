package com.biz.warning.domain;


import lombok.Data;

import java.io.Serializable;

@Data
public class Task implements Serializable {

  private Long taskId;
  private String taskCode;
  private String businessCode;
  private String taskName;
  private String apiCode;
  private Long userId;
  private Long strategyId;
  private String strategyName;
  private String strategyDescription;
  private Integer taskStatus;
  private String description;
  private String lastUpdateTime;
  private String createTime;
  private String entityTemplateName;
  private Integer entityCount;

  @Override
  public String toString() {
    return "Task{" +
            "taskId=" + taskId +
            ", taskCode='" + taskCode + '\'' +
            ", businessCode='" + businessCode + '\'' +
            ", taskName='" + taskName + '\'' +
            ", apiCode='" + apiCode + '\'' +
            ", userId=" + userId +
            ", strategyId=" + strategyId +
            ", strategyName='" + strategyName + '\'' +
            ", strategyDescription='" + strategyDescription + '\'' +
            ", taskStatus=" + taskStatus +
            ", description='" + description + '\'' +
            ", lastUpdateTime='" + lastUpdateTime + '\'' +
            ", createTime='" + createTime + '\'' +
            ", entityTemplateName='" + entityTemplateName + '\'' +
            ", entityCount=" + entityCount +
            '}';
  }
}
