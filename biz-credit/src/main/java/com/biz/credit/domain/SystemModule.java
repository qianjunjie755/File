package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemModule {
    private Integer moduleId;

    private String moduleName;

    private Integer status;

    private String updateTime;

    private String createTime;
}