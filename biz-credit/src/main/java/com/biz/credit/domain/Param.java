package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Param {
    private String code;
    private String name;
    private Integer type;
    private Integer required;
    private String value;
}
