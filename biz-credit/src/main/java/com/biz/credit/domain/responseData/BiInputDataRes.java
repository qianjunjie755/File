package com.biz.credit.domain.responseData;

import lombok.Data;

import java.io.Serializable;

@Data
public class BiInputDataRes implements Serializable {
    private String date_;//日期维度 天-20180907  月-20808 年-2018
    private int count_;//数量
}
