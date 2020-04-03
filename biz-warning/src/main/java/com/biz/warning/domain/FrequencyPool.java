package com.biz.warning.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class FrequencyPool {

  private Long frequencyCode;
  private String frequencyName;
  private Long interval;
  private String description;
  private String lastUpdateTime;
  private String createTime;

}
