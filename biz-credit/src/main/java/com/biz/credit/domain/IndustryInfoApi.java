package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class IndustryInfoApi implements Serializable {
    private Integer id;
    private Integer industryId;
    private String apiCode;
    private Integer moduleTypeId;
}
