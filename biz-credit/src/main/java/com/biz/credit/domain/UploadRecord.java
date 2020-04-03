package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadRecord implements Serializable {
    private Integer id;
    private String apiCode;
    private Integer userId;
    private Integer uploadCount;
    private String datetime;
    private String createT;
}
