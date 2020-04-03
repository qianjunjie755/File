package com.biz.credit.vo;

import com.biz.credit.domain.RuleSet;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class RuleSetVO extends RuleSet implements Serializable {
    //按规则名模糊搜索
    private String ruleName;
    //按规则code搜索
    private String ruleCode;
    private Long strategyId;

    private List<RuleVO> ruleVOList;

    private Integer instRuleSetCount=0;
    private Integer tempRuleSetCount=0;

    private Boolean addRuleSetInst=false;


    public void setInstRuleSetCount(Integer instRuleSetCount) {
        if(null!=instRuleSetCount)
            this.instRuleSetCount = instRuleSetCount;
    }

    public void setTempRuleSetCount(Integer tempRuleSetCount) {
        if(null!=tempRuleSetCount)
            this.tempRuleSetCount = tempRuleSetCount;
    }

    public Boolean getAddRuleSetInst() {
        addRuleSetInst=tempRuleSetCount>instRuleSetCount;
        return addRuleSetInst;
    }


    public RuleSetVO(Long ruleSetId){
        super.setRuleSetId(ruleSetId);
    }
    public RuleSetVO(Long userId,String apiCode){
        setUserId(userId);
        setApiCode(apiCode);
    }


}
