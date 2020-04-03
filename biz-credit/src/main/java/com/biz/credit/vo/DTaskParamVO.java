package com.biz.credit.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class DTaskParamVO implements Serializable {
    private String key;
    private String value = StringUtils.EMPTY;
    private String required;
    private Integer paramType;
    private Long order;

    public DTaskParamVO(DNodeParamsVO dNodeParamsVO){
        this.key=dNodeParamsVO.getName();
        this.required = null!=dNodeParamsVO.getRequired()?dNodeParamsVO.getRequired().toString():"0";
        this.order = dNodeParamsVO.getOrder();
        this.paramType = dNodeParamsVO.getParamType();
    }

    public void setValue(String value){
        if(StringUtils.isNotEmpty(value)){
            this.value = value;
        }
    }
}
