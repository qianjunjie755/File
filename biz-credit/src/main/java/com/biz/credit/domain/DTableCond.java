package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class DTableCond implements Serializable {
    private Long id;
    private Long varId;
    private String condJudge;
    private String left;
    private String leftExpr;
    private String rightExpr;
    private String right;
    private Boolean IsExist=false;
    private Boolean defaultValue=false;
    private Integer parentCondOrder;
    private Integer condOrder;
    private String outValue;
    private Long parentId;
    private Long tableId;
    private Integer status;
    private String updateTime;
    private String createTime;
    private List<DTableCond> tableCondList;
    private Integer condType;
    private Integer nextCondType=1;
}
