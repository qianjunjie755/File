package com.biz.credit.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BiInputByGroupVO implements Serializable {
    private Integer groupId;
    private String groupName;
    private String date;
    private String count;
    private String apiCode;

    @Override
    public String toString() {
        return "BiInputByGroupVO{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", date='" + date + '\'' +
                ", count='" + count + '\'' +
                ", apiCode='" + apiCode + '\'' +
                '}';
    }
}
