package com.biz.credit.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class IndustryInfoBoundaryViewVO implements Serializable {
    private String title;
    private Integer boundaryType;
    private String left;
    private String right;
}
