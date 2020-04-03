package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Project {

    private Long id;
    private String name;
    private Integer platformId;
    private Integer status;
    private Long userId;
    private String apiCode;
    private String description;
}
