package com.biz.credit.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PortraitTaskLabelRespVO implements Serializable{
    private Integer labelId;

    private String labelCode;

    private String labelName;
}
