package com.biz.credit.domain;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@Data
public class ApiInputFileDetailContact implements Serializable {
    private Integer contactId;
    private Long inputFileDetailId;
    private String idNumber = StringUtils.EMPTY;
    private String cellPhone = StringUtils.EMPTY;
    private String name = StringUtils.EMPTY;
    private String lastUpdateTime;
    private String createTime;
    private String homeAddr = StringUtils.EMPTY;
    private String bizAddr = StringUtils.EMPTY;

}
