package com.biz.credit.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class DNodeRule extends DRule implements Serializable {

  private Integer status;
  private Long srcRuleId;
  private Long modelId;

}
