package com.biz.credit.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TreeData {
    private Long id;
    private Integer varId;
    private String varName;
    private String type;
    private Long parentId;
    private List<TreeData> children;
}
