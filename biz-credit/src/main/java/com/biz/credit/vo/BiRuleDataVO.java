package com.biz.credit.vo;

import com.biz.credit.domain.BiRuleData;
import lombok.Data;

import java.io.Serializable;

@Data
public class BiRuleDataVO extends BiRuleData implements Serializable {
    private String apiCode;
    private Integer groupId;
    private Integer userId;
    private Integer industryId;
    private Integer reportType;
    private Integer moduleTypeId;
    private String startDate;
    private String endDate;
    private String startMonth;
    private String endMonth;
    private String startYear;
    private String endYear;
    private String prodName;
    private String interval;//查询周期
    private String threshold;
    private String describe;
    private Long tRuleId;
    private Long count;
}
