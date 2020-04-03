package com.biz.credit.vo;

import com.biz.credit.domain.IndustryInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class IndustryInfoVO extends IndustryInfo implements Serializable {
    private String apiCode;
    private Integer moduleTypeId;
    private String apiProdCode;
    private String apiVersion;
    public IndustryInfoVO (String apiCode){
        this.apiCode = apiCode;
    }
    public IndustryInfoVO(){
        super();
    }
    public IndustryInfoVO(Integer industryId){
        setIndustryId(industryId);
    }
}
