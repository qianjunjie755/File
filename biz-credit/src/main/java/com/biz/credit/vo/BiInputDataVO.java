package com.biz.credit.vo;

import com.biz.credit.domain.BiInputData;
import lombok.Data;

import java.io.Serializable;

@Data
public class BiInputDataVO extends BiInputData implements Serializable {
    private String interval;//查询周期
    private String startDate;
    private String endDate;
    private String startMonth;
    private String endMonth;
    private String startYear;
    private String endYear;
    private String refuseScore;//拒绝评分区间
    private String reconsideScore;//复议
    private String aggreeScore;//通过





}
