package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class BiRuleData implements Serializable {
    private Long id;
    private Long ruleId;
    private Long inputFileDetailId;
    private String date;
    private Integer hit;
    private Integer weight;
    private String threshold;
    private String interval;
    private String resultValue;
    private Integer month;
    private String datetime;
    private Integer year;
}
