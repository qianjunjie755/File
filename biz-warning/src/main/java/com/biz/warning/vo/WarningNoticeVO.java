package com.biz.warning.vo;

import com.biz.warning.domain.WarnResultVariable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Data
@Slf4j
public class WarningNoticeVO extends WarnResultVariable implements Serializable {
    private Integer userId;
    private Integer userType;
    private String apiCode;
    private String dataSource;
    private String companyName;
    private Integer read;
    private List<Integer> userIds;
    private String variableName;

    @Override
    public String toString() {
        return "WarningNoticeVO{" +
                "userId=" + userId +
                ", userType=" + userType +
                ", apiCode='" + apiCode + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", companyName='" + companyName + '\'' +
                ", read=" + read +
                ", userIds=" + userIds +
                ",warnResultVariableId=" + getWarnResultVariableId() +
                ", hitTime='" + getHitTime() + '\'' +
                ", ruleId=" + getRuleId() +
                ", srcRuleId=" + getSrcRuleId() +
                ", ruleSetId=" + getRuleSetId() +
                ", strategyId=" + getStrategyId() +
                ", variableId=" + getVariableId() +
                ", variableCode=" + getVariableCode() +
                ", threshold='" + getThreshold() + '\'' +
                ", triggerThreshold='" + getTriggerThreshold() + '\'' +
                ", taskId=" + getTaskId() +
                ", entityId=" + getEntityId() +
                ", description='" + getDescription() + '\'' +
                ", lastUpdateTime='" + getLastUpdateTime() + '\'' +
                ", createTime='" + getCreateTime() + '\'' +
                ", period=" + getPeriod() +
                ", periodUnit='" + getPeriodUnit() + '\'' +
                ", detail=" + getDetail() +
                ", detailExisted=" + getDetailExisted() +
                '}';
    }

    /*public static void main(String[] args) {
        System.out.println(new Date(1547013457530l));
        System.out.println(new Date(1554768000000l));
    }*/
}
