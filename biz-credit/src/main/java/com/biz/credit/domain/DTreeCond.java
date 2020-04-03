package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class DTreeCond implements Serializable {
    private Long id;
    private Long varId;
    private String type="condition";
    private String condJudge;
    private String left;
    private String leftExpr;
    private String rightExpr;
    private String right;
    private Integer condOrder;
    private String outValue;
    private Long parentId;
    private Long treeId;
    private Long nextVarId;
    private Integer status;
    private String updateTime;
    private String createTime;
    private List<DTreeVar> children;
}
