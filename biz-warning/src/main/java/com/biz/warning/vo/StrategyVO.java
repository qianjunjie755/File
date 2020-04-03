package com.biz.warning.vo;

import com.biz.warning.domain.Strategy;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 策略页面传参
 */
@Data
@NoArgsConstructor
public class StrategyVO extends Strategy implements Serializable {
    // 策略规则集关联关系List
    private List<RuleSetVO> ruleSetVOList;

    //是否有任务关联
    private Boolean isRelated;

    public StrategyVO(Long strategyId){
        setStrategyId(strategyId);
    }

    public StrategyVO(Long strategyId,String apiCode,Long userId){
        setStrategyId(strategyId);setApiCode(apiCode); setUserId(userId);
    }
}
