package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Expression {
    private String left;
    private String leftExpr;
    private String right;
    private String rightExpr;
}
