package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PortraitTask {
    private Integer taskId;

    private String taskName;

    private Integer taskType;

    private Integer moduleId;

    private String interval;

    private String startDate;

    private String endDate;

    private String lastRunTime;

    private Integer status;

    private Integer updateUser;

    private String updateTime;

    private Integer createUser;

    private String createTime;

}