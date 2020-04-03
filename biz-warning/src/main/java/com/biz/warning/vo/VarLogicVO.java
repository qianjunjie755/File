package com.biz.warning.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VarLogicVO implements Serializable {
    private Integer logic = 1;
    private List<VarThresholdVO> varThresholdVOList = new ArrayList<>();
}
