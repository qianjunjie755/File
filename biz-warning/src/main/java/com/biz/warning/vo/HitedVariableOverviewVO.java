package com.biz.warning.vo;


import com.biz.warning.domain.Rule;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.List;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class HitedVariableOverviewVO implements Serializable {

  private Rule rule;
  private List<WarnResultVariableCountVO> warnResultVariableCountVOList;

  public Rule getRule() {
    return rule;
  }

  public void setRule(Rule rule) {
    this.rule = rule;
  }

  public List<WarnResultVariableCountVO> getWarnResultVariableCountVOList() {
    return warnResultVariableCountVOList;
  }

  public void setWarnResultVariableCountVOList(List<WarnResultVariableCountVO> warnResultVariableCountVOList) {
    this.warnResultVariableCountVOList = warnResultVariableCountVOList;
  }
}
