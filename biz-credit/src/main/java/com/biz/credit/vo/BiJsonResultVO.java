package com.biz.credit.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BiJsonResultVO implements Serializable {
    private Integer pageSize;
    private Integer pageNo;
    private JSONArray xAxisData;
    private JSONArray yAxisData;
    private List<JSONObject> biDataList;
    private Long total;

    @Override
    public String toString() {
        return "BiJsonResultVO{" +
                "pageSize=" + pageSize +
                ", pageNo=" + pageNo +
                ", xAxisData=" + (null!=xAxisData?xAxisData.toJSONString():null) +
                ", yAxisData=" + (null!=yAxisData?yAxisData.toJSONString():null) +
                ", biDataList=" + biDataList +
                ", total=" + total +
                '}';
    }
}
