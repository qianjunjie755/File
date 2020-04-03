package com.biz.warning.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParamVO {
    @JsonIgnore
    @JSONField(serialize=false)
    private Integer taskId;
    @JsonIgnore
    @JSONField(serialize=false)
    private String taskName;
    private String code;
    private String name;
    private Integer type;
    private Integer required;
}
