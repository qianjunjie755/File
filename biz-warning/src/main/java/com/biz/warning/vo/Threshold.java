package com.biz.warning.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Threshold {
    private String type;
    private String lt;
    private String gt;
    private String eq;
    private String unit;
    private String judge;
    private String time;
}
