package com.biz.credit.dao;


import com.biz.credit.domain.DNodeModel;
import com.biz.credit.domain.ScoreApi;
import com.biz.credit.domain.ScoreBoundary;
import com.biz.credit.domain.ScoreCard;
import com.biz.credit.vo.ScoreCardVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreCardDAO {
    int insert(@Param("scoreCardVO") ScoreCardVO scoreCardVO);

    int update(@Param("scoreCardVO") ScoreCardVO scoreCardVO);

    ScoreCardVO queryById(@Param("scoreCardId") Long scoreCardId);

    int queryCountByCardName(@Param("scoreCardVO") ScoreCardVO scoreCardVO);

    String queryMaxVersionByCardName(@Param("scoreCardVO") ScoreCardVO scoreCardVO);

    List<ScoreCardVO> queryVersionListByCardName(@Param("scoreCardVO") ScoreCardVO scoreCardVO);

    List<ScoreCard> queryAllMaxVersionList(@Param("apiCode") String apiCode, @Param("projectId") Long projectId);

    List<ScoreCard> queryListByProjectId(@Param("projectId") Long projectId);
    //策略配置
    //评分卡
    List<ScoreCardVO> queryScoreCardList(@Param("apiCode") String apiCode);

    List<ScoreCardVO> findScoreCardList(@Param("dNodeModel") DNodeModel dNodeModel);
    List<ScoreApi> queryScoreApiList(@Param("scoreCardId") Long scoreCardId);
    List<ScoreBoundary> queryScoreBoundaryList(@Param("scoreApiId") Long scoreApiId);

}
