package com.biz.warning.vo;

import lombok.Data;

@Data
public class StatitscVO {
    private Integer id;
    private String hitDate;
    private String type;
    private Integer sourceId;
    private Integer hitCount;
    private Integer entityId;
    private Integer userId;
    private String apiCode;
}
