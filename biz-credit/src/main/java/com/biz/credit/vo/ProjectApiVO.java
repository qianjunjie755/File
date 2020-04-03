package com.biz.credit.vo;

import com.biz.credit.domain.ProjectApi;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class ProjectApiVO extends ProjectApi implements Serializable {
    public ProjectApiVO(String apiCode,String prodCode,Double version){
        setApiCode(apiCode);setProdCode(prodCode);setVersion(version);
    }
}
