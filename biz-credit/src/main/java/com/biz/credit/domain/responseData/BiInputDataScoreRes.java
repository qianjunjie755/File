package com.biz.credit.domain.responseData;

import lombok.Data;

import java.io.Serializable;

@Data
public class BiInputDataScoreRes implements Serializable {
    private String count_;
    private String interval_;//评分区间
    private String interval1;//评分区间
    private String interval2;//评分区间
}
