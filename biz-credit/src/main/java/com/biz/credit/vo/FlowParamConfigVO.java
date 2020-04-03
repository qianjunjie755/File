package com.biz.credit.vo;

import com.biz.credit.domain.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class FlowParamConfigVO  implements Serializable {
    private Long nodeId;
    //数据源api列表
    private List<DNodeApiVO> nodeApiList;
    //企业规则
    private List<DNodeRuleVO> companyRuleList;
    //自然人规则
    private List<DNodeRuleVO> perRuleList;
    //反欺诈
    private DAntiFraud antiFraudInfo;
    //信用评分模型
    private IndustryInfo industryInfo;
    //评分卡
    private ScoreCardVO scoreCardInfo;
    //决策表
    private DTable tableInfo;
    //决策树
    private DTree treeInfo;
}
