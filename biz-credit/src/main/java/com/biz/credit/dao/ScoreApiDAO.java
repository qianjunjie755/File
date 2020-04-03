package com.biz.credit.dao;

import com.biz.credit.domain.DNodeParam;
import com.biz.credit.vo.ScoreApiVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreApiDAO {

    int insert(@Param("scoreApiVO") ScoreApiVO scoreApiVO);

    int updateStatusByScoreCardId(@Param("scoreCardId") Long scoreCardId, @Param("status") Integer status);

    List<DNodeParam> queryByScoreCardId(@Param("scoreCardId") Long scoreCardId);
}
