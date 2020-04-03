package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class VariableTemplate implements Serializable {
    private Integer id;
    private String prodCode;
    private String pdfCode;
    private Double version;
    private String prodName;
    private String params;
    private String threshold;
    private Integer apiReqType;
    private String reqUrls;
    private String params2;
    private Integer detailApi;
}
