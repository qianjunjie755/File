package com.biz.chart.entity.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChartVO {
    private Long id;
    private Integer type;
    private Integer depth;
    private String threshold;
    private String relations;
    private String apiCode;
    private Integer userId;
}
