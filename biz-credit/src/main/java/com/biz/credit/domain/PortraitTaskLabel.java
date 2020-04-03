package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortraitTaskLabel {
    private Integer taskId;

    private Integer labelId;

    private Integer status;

    private Integer updateUser;

    private String updateTime;

    private Integer createUser;

    private String createTime;

}