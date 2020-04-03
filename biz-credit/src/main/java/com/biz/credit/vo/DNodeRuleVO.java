package com.biz.credit.vo;

import com.biz.credit.domain.DNodeRule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@Setter
public class DNodeRuleVO extends DNodeRule {

    private Long nodeId;
    private Integer modelType;
    private Boolean choose;
    private Integer instance;

    private String apiCode;

    private List<DNodeRuleVarVO> nodeRuleVarVOList;

    public void buildVarThreadValue() {
        if(!CollectionUtils.isEmpty(nodeRuleVarVOList)){
            for (DNodeRuleVarVO nodeRuleVarVO : nodeRuleVarVOList) {
                nodeRuleVarVO.buildThresholdValue();
            }
        }
    }
}
