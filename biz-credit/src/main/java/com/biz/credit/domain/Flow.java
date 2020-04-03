package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
public class Flow {
    private Integer flowId;
    private String flowName;
    private Integer moduleTypeId;
    private List<Param> companyParams;
    private List<Param> personParams;

    public Flow() {
        companyParams = new ArrayList<>();
        personParams = new ArrayList<>();
    }

    public void addParam(Param param) {
        if (param == null) {
            return;
        }
        //企业参数
        if (Objects.equals(param.getType(), 1)) {
            companyParams.add(param);
        }
        //自然人参数
        else if (Objects.equals(param.getType(), 2)) {
            personParams.add(param);
        }
    }
}
