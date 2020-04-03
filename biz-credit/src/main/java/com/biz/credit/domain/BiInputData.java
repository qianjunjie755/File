package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BiInputData implements Serializable {
    private String taskName;
    private Long inputFileDetailId;
    private String apiCode;
    private Integer groupId;
    private Integer userId;
    private Integer industryId;
    private Integer reportType;
    private Integer moduleTypeId;
    private String date;
    private Integer hit;
    private Integer month;
    private Double score;
    private String datetime;
    private Integer year;
    private Integer reportStatus=1;
    private Integer strategyResult;
    private Integer scoreResult;

    public void setReportStatus(Integer reportStatus){
        if(null!=reportStatus)
            this.reportStatus = reportStatus;
    }

    public static void main(String[] args) {
        System.out.println(new Date(1569801600000l));
    }
}
