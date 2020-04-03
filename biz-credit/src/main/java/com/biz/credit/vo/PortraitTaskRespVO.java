package com.biz.credit.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PortraitTaskRespVO implements Serializable{
    private Integer taskId;

    private String taskName;

    private Integer taskType;

    private Integer moduleId;

    private String moduleName;

    private String taskInterval;

    private String lastRunTime;

    private Integer status;

    private String updateTime;
}
