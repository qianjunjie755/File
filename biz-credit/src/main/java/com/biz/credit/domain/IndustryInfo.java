package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class IndustryInfo implements Serializable {
    private Integer industryId;
    private String industryCode;
    private String industryName;
    private String lastUpdateTime;
    private String createTime;
    private String modelCode;
    private Double modelVersion;
    private String rejectInterval;
    private String reconsiderInterval;
    private String passInterval;
    private String industryType;
    private String refuseStartScore;
    private String reconsideStartScore;
    private String agreeStartScore;
    private String agreeEndScore;
    private Integer boundaryType;
    private String industryDescription;
}
