package com.biz.credit.domain.responseData;

import lombok.Data;

import java.io.Serializable;

@Data
public class BiRuleIdDataRes implements Serializable {
    private String ruleName;//日期维度 天-20180907  月-20808 年-2018
    private int count_;//数量
}