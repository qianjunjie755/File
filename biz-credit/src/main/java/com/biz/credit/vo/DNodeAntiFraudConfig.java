package com.biz.credit.vo;

import com.biz.credit.domain.DAntiFraud;
import com.biz.credit.domain.DNodeThreshold;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DNodeAntiFraudConfig {
    private List<DAntiFraud> antiFraud;

    private List<DAntiFraudVO> antiFraudVOList;

    private List<DNodeThreshold> nodeThresholdList;
}
