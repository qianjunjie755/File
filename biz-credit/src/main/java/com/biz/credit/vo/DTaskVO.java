package com.biz.credit.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DTaskVO implements Serializable {
    private List<DTaskGroupVO> rows = new ArrayList<>();
    private List<Integer> relatedPIndexList = new ArrayList<>();
    private Integer relatedP;
    private Boolean company;
    private List<DTaskParamVO> paramVOList = new ArrayList<>();
    private List<DTaskParamVO> relatedParamVOList = new ArrayList<>();

}
