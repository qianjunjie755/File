package com.biz.credit.vo;

import com.biz.credit.domain.DNodeRuleVar;
import com.biz.credit.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class DNodeRuleVarVO extends DNodeRuleVar{

    private String prodCode;
    private String variableName;
    private Integer variableTypeCode;
    private String version;
    private String dataType;
    private String defaultThreshold;
    private String dataSource;
    private String apiProdCode;
    private String apiVersion;
    private String description;
    private Integer selected = NumberUtils.INTEGER_ONE.intValue();

    private Long srcRuleId;
    private Long varPId;

    private List<DNodeRuleVarRefVO> refRuleVarList;

    @Override
    public void buildThresholdValue(){
        if(!CollectionUtils.isEmpty(refRuleVarList)){
            for (DNodeRuleVarRefVO refVar : refRuleVarList) {
                refVar.buildThresholdValue();
            }
        }
        super.buildThresholdValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DNodeRuleVar that = (DNodeRuleVar) o;
        return Objects.equals(getSrcVarId(), that.getSrcVarId());
    }

    public void setSelected(Integer selected) {
        if(Objects.equals(Constants.COMMON_STATUS_VALID,selected)||Objects.equals(Constants.COMMON_STATUS_INVALID,selected))
            this.selected = selected;
    }

    public Integer getSelected() {
        if(Objects.equals(Constants.COMMON_STATUS_INVALID,getStatus())){
            setSelected(Constants.COMMON_STATUS_INVALID);
        }
        return selected;
    }

    @Override
    public int hashCode() {

        return Objects.hash(getSrcVarId());
    }

}
