package com.biz.credit.vo;

import com.biz.credit.domain.DTableCond;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DTableCondVO extends DTableCond {
    private List<DTableCond> tableCondList;
}
