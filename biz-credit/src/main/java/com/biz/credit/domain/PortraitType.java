package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortraitType {
    private Integer typeId;

    private String typeCode;

    private String typeName;

    private Integer moduleId;

    private Integer status;

    private Integer parentId;

    private Integer updateUser;

    private String updateTime;

    private Integer createUser;

    private String createTime;

}