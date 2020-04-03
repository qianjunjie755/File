package com.biz.credit.vo;

import com.biz.credit.domain.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponseVO extends ApiResponse implements Serializable {
    private String apiProdCode;
    private Double apiVersion;
    private List<ApiResponseVO> children = new ArrayList<>();
    private Integer depth;
    private ApiResponseVO parent;
}
