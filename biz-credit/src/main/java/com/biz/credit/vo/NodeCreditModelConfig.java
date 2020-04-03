package com.biz.credit.vo;

import com.biz.credit.domain.DNodeThreshold;
import com.biz.credit.domain.IndustryInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NodeCreditModelConfig  {
    private List<IndustryInfo> creditModelData;
    private List<DNodeThreshold> nodeThresholdList;

}
