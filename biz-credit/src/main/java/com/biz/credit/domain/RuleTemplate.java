package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class RuleTemplate implements Serializable {
    private Integer id;
    private String ruleName;
    private Double version;
    private Integer logic;
    private Integer weight;
    private Integer sourceId;
}
