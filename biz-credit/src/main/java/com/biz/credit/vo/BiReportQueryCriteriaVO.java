package com.biz.credit.vo;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.io.Serializable;

@Data
public class BiReportQueryCriteriaVO implements Serializable {
    private Integer pageSize;
    private Integer pageNo;
    private JSONArray rows;
    private Long total;
    private String apiCode;
    private Integer userId;
    private Integer groupId;
    private Integer reportType;
    private Integer moduleTypeId;
    private Integer industryId;
    private String interval;
    private String startDate;
    private String endDate;
    private Integer userType;
}
