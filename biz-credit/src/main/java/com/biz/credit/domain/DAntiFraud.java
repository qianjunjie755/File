package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DAntiFraud {
    private Long id;
    private String name;
    private String version;
    private String apiProdCode;
    private String apiVersion;
    private String apiJson;
    private Integer status;
}
