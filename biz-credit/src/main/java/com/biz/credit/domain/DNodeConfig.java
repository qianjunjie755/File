package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DNodeConfig implements Serializable {
    //private Long nodeId;
    private Long treeId;
    private Long tableId;
    private Long scoreCardId;
    private Integer type;
    private String judge;
    private String createTime;
    private Integer status;
}
