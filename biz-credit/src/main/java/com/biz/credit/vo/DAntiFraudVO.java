package com.biz.credit.vo;

import com.biz.credit.domain.DAntiFraud;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DAntiFraudVO extends DAntiFraud {
    private Long nodeId;

    private Integer modelType;

    private Boolean choose;

    private String apiCode;
}
