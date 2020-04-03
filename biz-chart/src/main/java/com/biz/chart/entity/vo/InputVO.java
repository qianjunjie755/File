package com.biz.chart.entity.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InputVO {
    private String apiCode;
    private Integer chartType;
    private Integer chartDepth;
    private String chartThreshold;
    private String chartRelations;
    private String companyName;
    private String name;
    private String idNo;
}
