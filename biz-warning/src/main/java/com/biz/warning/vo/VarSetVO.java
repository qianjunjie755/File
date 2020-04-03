package com.biz.warning.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VarSetVO implements Serializable {
    private List<VarVO> varVOList = new ArrayList<>();
    private Integer setId;
    private String name;

}
