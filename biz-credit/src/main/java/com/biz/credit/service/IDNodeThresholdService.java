package com.biz.credit.service;

import com.biz.credit.domain.DNodeThreshold;
import com.biz.credit.domain.RespEntity;

import java.util.List;

public interface IDNodeThresholdService {
    RespEntity saveNodeThresholdList(Long modelId, List<DNodeThreshold> nodeThresholdList);

    List<DNodeThreshold> getListByNodeIdAndType(Long nodeId, int modelType);
}
