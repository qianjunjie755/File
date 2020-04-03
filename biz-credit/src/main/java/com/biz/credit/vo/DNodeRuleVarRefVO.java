package com.biz.credit.vo;

import com.biz.credit.domain.DNodeRuleVar;
import com.biz.credit.domain.DNodeRuleVarRef;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Objects;

@Data
public class DNodeRuleVarRefVO extends DNodeRuleVarRef {

    private Long varPId;
    @JsonIgnore
    private Integer srcVarPeriod;
    @JsonIgnore
    private String srcPeriodUnit;
    @JsonIgnore
    private String srcVarThreshold;

    public void convertSrc2Instance() {
        this.setVarPeriod(srcVarPeriod);
        this.setPeriodUnit(srcPeriodUnit);
        this.setVarThreshold(srcVarThreshold);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DNodeRuleVar that = (DNodeRuleVar) o;
        return Objects.equals(getSrcVarId(), that.getSrcVarId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSrcVarId());
    }
}
