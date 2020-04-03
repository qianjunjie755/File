package com.biz.credit.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ModuleTypeApi implements Serializable {
    private Integer moduleTypeId;
    private String apiCode;
    private Integer reportType;
    private Integer status;
    private String validEnd;

    public ModuleTypeApi(Integer reportType,String apiCode,Integer status){
        this.reportType = reportType;
        this.apiCode = apiCode;
        this.status = status;
    }

    public ModuleTypeApi(Integer reportType,String apiCode,Integer status,String validEnd){
        this.reportType = reportType;
        this.apiCode = apiCode;
        this.status = status;
        this.validEnd = validEnd;
    }
}
