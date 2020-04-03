package com.biz.credit.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SystemGroupVO implements Serializable {
    private Integer groupId;
    private String groupName;
    private Integer status;
    private String updateTime;
    private  String createTime;
}
