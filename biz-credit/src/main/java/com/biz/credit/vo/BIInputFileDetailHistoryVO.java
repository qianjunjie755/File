package com.biz.credit.vo;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

@Data
public class BIInputFileDetailHistoryVO implements Serializable {
    private Integer inputFileDetailId;
    private Integer taskId;
    private String apiCode;
    private String username;
    private String realname;
    private Integer groupId;
    private String groupName;
    private Integer moduleTypeId;
    private Integer reportType;
    private String columnHead;
    private Integer status;
    private String keyNo;
    private String creditCode;
    private String idNumber;
    private String cellPhone;
    private String name;
    private String createTime;
    private String bankId;
    private String homeAddr;
    private String bizAddr;
    private String date;
    private Integer month;
    private Integer year;
    private String startDate;
    private String endDate;
    private String strategyResult;
    private String strategyResultCN;
    private Integer industryId;
    private String industryName;
    private Integer userType;
    private Integer userId;
    private String relatedPersonHead;
    private List<RelatedPersonVO> personVOList;
    private Integer inputType;

    public String getStrategyResultCN() {
        if(StringUtils.equals("1",strategyResult)){
            return "通过";
        }else if(StringUtils.equals("2",strategyResult)){
            return "拒绝";
        }else if(StringUtils.equals("3",strategyResult)){
            return "复议";
        } else if(StringUtils.equals("0",strategyResult)){
            return "无建议";
        }
        return StringUtils.EMPTY;
    }

    public String getGroupName(){
        if(null!=userType&&0==userType.intValue()){
            groupName = "超级管理员";
        }
        return groupName;
    }
}
