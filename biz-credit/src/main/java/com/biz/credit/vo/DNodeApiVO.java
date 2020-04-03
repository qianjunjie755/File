package com.biz.credit.vo;

import com.biz.credit.domain.DNodeApi;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DNodeApiVO extends DNodeApi {

    private String prodName;

    private Integer sourceId;

    private String apiCode;

    private Boolean choose;
}
