package com.biz.warning.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class VariableSource implements Serializable {
    private Integer sourceId;
    private String sourceName;
    public VariableSource(Integer sourceId,String sourceName){
        this.sourceId = sourceId;
        this.sourceName=sourceName;
    }
}
