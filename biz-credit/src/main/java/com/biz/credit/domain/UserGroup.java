package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserGroup implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private String createT;
    private String updateT;
    private Integer institutionId;
    private Integer interval;
    private Integer uploadLimit;
    private Integer limitStatus;
    private Integer status;
}
