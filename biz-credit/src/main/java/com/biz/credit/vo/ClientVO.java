package com.biz.credit.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClientVO implements Serializable {
    private String apiCode;
    private String prodCode;
    private String industryId;
    private Double version;
    private Integer reportType;
    private String jsonData;
    private String endDate;
    private String publicKey;
}
