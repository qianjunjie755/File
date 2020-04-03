package com.biz.warning.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VarVO implements Serializable {
    private List<VarLogicVO> varLogicVOList = new ArrayList<>();
    private Integer logic = 1;
}
