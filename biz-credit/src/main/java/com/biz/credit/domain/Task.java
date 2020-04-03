package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
public class Task implements Serializable {

  private Integer taskId;
  private String taskName;
  private String taskCode;
  private Integer taskStatus;
  private String apiCode;
  private Integer userId;
  private Integer userType;
  private String description;
  private String lastUpdateTime;
  private String createTime;
  private List<InputFileDetail> inputFileDetails;
  private Integer moduleTypeId;
  private Integer taskType;
  private Integer industryId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Task task = (Task) o;
    return Objects.equals(taskId, task.taskId);
  }
  @Override
  public int hashCode() {
    return Objects.hash(taskId);
  }
}
