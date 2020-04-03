package com.biz.warning.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class RuleOperLog implements Serializable {

  private Long logId;
  private Long ruleId;
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private java.sql.Timestamp operTime;
  private Long userId;
  private String apiCode;
  private String operContent;
  private String description;
  private String lastUpdateTime;
  private String createTime;
  private String userName;

}
