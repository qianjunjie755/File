package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class BiReportData implements Serializable {
    private Long id;
    private Long inputFileDetailId;
    private Integer dataType;
    private String date;
    private Integer month;
    private String datetime;
    private Integer year;
    private Integer moduleTypeId;
}
