package com.biz.credit.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParamVO implements Cloneable {
    @JsonIgnore
    @JSONField(serialize=false)
    private Integer flowId;
    @JsonIgnore
    @JSONField(serialize=false)
    private String flowName;
    @JsonIgnore
    @JSONField(serialize=false)
    private Integer moduleTypeId;
    private String code;
    private String name;
    private Integer type;
    private Integer required;

    @Override
    public ParamVO clone() {
        try {
            ParamVO vo = (ParamVO) super.clone();
            vo.flowId = this.flowId;
            vo.flowName = this.flowName;
            vo.moduleTypeId = this.moduleTypeId;
            vo.code = this.code;
            vo.name = this.name;
            vo.type = this.type;
            vo.required = this.required;
            return vo;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone Not Supported: " + e.getMessage());
        }
    }
}
