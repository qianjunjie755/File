package com.biz.credit.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ReqParam {
    private Integer flowId;
    private Integer moduleTypeId;
    private Integer taskType; //1-批量进件 0-单条进件
    private String taskName;
    private JSONObject params; //企业及法人参数
    private List<JSONObject> relatedParams; //关联人参数
}
