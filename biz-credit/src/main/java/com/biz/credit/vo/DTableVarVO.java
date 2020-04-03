package com.biz.credit.vo;

import com.biz.credit.domain.DTableVar;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DTableVarVO extends DTableVar implements Serializable{
    private String apiProdCode;
    private String apiVersion;
}
