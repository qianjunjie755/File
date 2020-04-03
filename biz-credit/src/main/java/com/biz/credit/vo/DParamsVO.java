package com.biz.credit.vo;

import com.biz.credit.domain.DNodeParam;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DParamsVO {
    private Long nodeId;
    private List<DNodeParam> nodeParamList;
}
