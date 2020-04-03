package com.biz.credit.vo;

import com.biz.credit.domain.DTreeVar;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DTreeVarVO extends DTreeVar implements Serializable{
    private String apiProdCode;
    private String apiVersion;
}
