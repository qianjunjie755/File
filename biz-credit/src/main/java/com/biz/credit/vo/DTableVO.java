package com.biz.credit.vo;

import com.biz.credit.domain.DTable;
import com.biz.credit.domain.DTableCond;
import com.biz.credit.domain.DTableVar;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DTableVO extends DTable{
    private String judge;
    private boolean choose;
    private Integer type;
    private String projectName;
    private List<DTableVar> tableVarList;
    private List<DTableCond> tableCondList;
}
