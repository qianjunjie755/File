package com.biz.credit.vo;

import com.biz.credit.domain.ApiRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ApiRequestVO extends ApiRequest implements Serializable {
    private String apiProdCode;
    private Double apiVersion;
}
