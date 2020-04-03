package com.biz.credit.vo;

import com.biz.credit.domain.DNodeParam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DNodeParamsVO extends DNodeParam {
    private Long order;
    private Integer paramType;
}
