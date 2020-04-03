package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class CrmApi implements Serializable {
    private Integer id;
    private String prodCode;
    private String prodName;
    private Double version;
    private String description;
    private Integer moduleTypeId;
    private Integer industryId;
    private String createTime;
    private String lastUpdateTime;
    private Integer apiType;
    private Integer reportType;
    private String apiCode;
    private Long flowId;

    @Override
    public String toString() {
        return "CrmApi{" +
                "id=" + id +
                ", prodCode='" + prodCode + '\'' +
                ", version=" + version +
                ", description='" + description + '\'' +
                ", moduleTypeId=" + moduleTypeId +
                ", industryId=" + industryId +
                ", createTime='" + createTime + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", apiType=" + apiType +
                ", reportType=" + reportType +
                ", apiCode='" + apiCode + '\'' +
                ", flowId='" + flowId + '\'' +
                '}';
    }
}
