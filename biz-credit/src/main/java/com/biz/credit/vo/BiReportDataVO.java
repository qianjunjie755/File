package com.biz.credit.vo;

import com.biz.credit.domain.BiReportData;
import lombok.Data;

import java.io.Serializable;

@Data
public class BiReportDataVO extends BiReportData implements Serializable {
    private String apiCode;
    private Integer groupId;
    private Integer userId;
    private Integer industryId;
    private Integer reportType;
}
