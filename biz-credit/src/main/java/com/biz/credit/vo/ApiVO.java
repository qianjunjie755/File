package com.biz.credit.vo;

import com.biz.credit.domain.Api;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiVO extends Api implements Serializable {
        private String apiProdCode;
        private String apiVersion;
        private List<ApiRequestVO> requestParamList = new ArrayList<>();
        private List<ApiResponseVO> responseParamList = new ArrayList<>();
}
