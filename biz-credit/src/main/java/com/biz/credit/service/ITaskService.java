package com.biz.credit.service;

import com.biz.credit.vo.StrategyVO;

public interface ITaskService {
    long copyStrategyTemplate(StrategyVO source, StrategyVO target) throws Exception;
    long updateSrcIdForCopyStrategyTemplate(StrategyVO target) throws Exception;
    long copyModuleTypeByStrategyId(StrategyVO source, StrategyVO target) throws Exception;
}
