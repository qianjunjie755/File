package com.biz.credit.service;

import com.biz.credit.vo.DScoreLevelVO;

public interface IDScoreLevelService {
    void saveScoreLevel(DScoreLevelVO scoreLevel);

    DScoreLevelVO findByCondition(String apiCode, Integer modelCode, Integer modelType);
}
