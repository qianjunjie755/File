package com.biz.credit.vo;

import com.biz.credit.domain.DTree;
import com.biz.credit.domain.DTreeVar;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DTreeVO extends DTree {
    private String judge;
    private boolean choose;
    private Integer type;
    private String projectName;
    private List<DTreeVar> treeData;
   // private List<DTreeCond> treeVarList;
}
