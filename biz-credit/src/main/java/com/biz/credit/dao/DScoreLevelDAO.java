package com.biz.credit.dao;

import com.biz.credit.domain.DNodeParam;
import com.biz.credit.domain.DScoreLevel;
import com.biz.credit.vo.DScoreLevelVO;
import com.biz.credit.vo.ScoreApiVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DScoreLevelDAO {

    int insert(@Param("scoreLevel") DScoreLevel scoreLevel);

    void deleteByCondition(@Param("apiCode")String apiCode,
                           @Param("modelCode")Integer modelCode,
                           @Param("modelType")Integer modelType);

    void insertBatch(@Param("list") List<DScoreLevel> list);

    List<DScoreLevel> findByCondition(@Param("apiCode")String apiCode,
                                  @Param("modelCode")Integer modelCode,
                                  @Param("modelType")Integer modelType);
}
