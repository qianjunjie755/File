package com.biz.warning.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Period {

  private Long id;
  private Integer period;
  private String periodUnit;
  private Integer order;
  private String content;
}
