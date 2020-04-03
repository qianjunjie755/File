package com.biz.credit.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReportProductVO implements Serializable {
    private String prodCode;
    private String industryId;
    private Double version;
    private Integer reportType;
}
