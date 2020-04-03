package com.biz.credit.domain;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class InputFileDetailContact implements Serializable {
    private Integer contactId;
    private Long inputFileDetailId;
    private String idNumber = StringUtils.EMPTY;
    private String cellPhone = StringUtils.EMPTY;
    private String name = StringUtils.EMPTY;
    private String lastUpdateTime;
    private String createTime;
    private String homeAddr = StringUtils.EMPTY;
    private String bizAddr = StringUtils.EMPTY;
    private List<Param> params;

    public boolean checkEmpty(){
        if(StringUtils.isEmpty(idNumber)
                &&StringUtils.isEmpty(cellPhone)
                &&StringUtils.isEmpty(name)
                &&StringUtils.isEmpty(homeAddr)
                &&StringUtils.isEmpty(bizAddr))
            return true;
        return false;
    }

    public void addParam(Param param) {
        if (param == null) {
            return;
        }
        if (this.params == null) {
            this.params = new ArrayList<>();
        }
        this.params.add(param);
    }
}
