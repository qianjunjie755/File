package com.biz.credit.vo;

import com.biz.credit.domain.DFlow;
import com.biz.credit.domain.DNodeParam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DFlowVO extends DFlow {
    private List<DNodeVO> nodeList;
    private List<DNodeParam> nodePrarmList;
    private List<DNodeVO> nodeVOList;


    public DFlowVO (String apiCode){
        this.setApiCode(apiCode);
    }
    public DFlowVO (String apiCode,Long flowId,Long userId){
        setApiCode(apiCode);
        setFlowId(flowId);
        setUserId(userId);
    }
}
