package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortraitLabel {
    private Integer labelId;

    private String labelCode;

    private String labelName;

    private String labelDesc;

    private Integer labelType;

    private String labelEnum;

    private Integer typeId;

    private String tableName;

    private String calcLogic;

    private Integer status;

    private Integer updateUser;

    private String updateTime;

    private Integer createUser;

    private String createTime;

}