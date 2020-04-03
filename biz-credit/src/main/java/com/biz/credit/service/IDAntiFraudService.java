package com.biz.credit.service;

import com.biz.credit.domain.RespEntity;
import com.biz.credit.vo.DAntiFraudVO;
import com.biz.credit.vo.DNodeAntiFraudConfig;

import java.util.List;

public interface IDAntiFraudService {
    RespEntity saveAntiFraudConfig(Long nodeId, DNodeAntiFraudConfig nodeAntiFraudConfig);

    List<DAntiFraudVO> getAntiFraudList(DAntiFraudVO antiFraudVO);

    DNodeAntiFraudConfig getAntiFraudConfig(DAntiFraudVO antiFraudVO);
}
