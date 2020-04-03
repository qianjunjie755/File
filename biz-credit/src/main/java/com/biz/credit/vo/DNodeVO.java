package com.biz.credit.vo;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.DNode;
import com.biz.credit.domain.DNodeConfig;
import com.biz.credit.domain.DNodeParam;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DNodeVO extends DNode {

    private String userId;

    private Boolean modified;

    private NodeRuleConfig companyNodeRuleConfig;

    private NodeRuleConfig personalNodeRuleConfig;

    private DNodeAntiFraudConfig nodeAntiFraudConfig;

    private List<DNodeApiVO> nodeApiList;

    private DNodeConfig nodeTreeConfig;

    private DNodeConfig nodeTableConfig;

    private DNodeConfig nodeScoreCardConfig;

    private NodeCreditModelConfig nodeCreditModelConfig;

    private List<DNodeParam> nodeParamList;

    private List<Integer> chooseTabList;

    private JSONObject nodeConfig;
}
