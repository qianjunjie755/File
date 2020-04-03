package com.biz.credit.dao;

import com.biz.credit.vo.ScoreBoundaryVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreBoundaryDAO {

    int insertList(List<ScoreBoundaryVO> scoreBoundaryVOList);

    int batchUpdateStatus(@Param("scoreBoundaryIdList") List<Long> scoreBoundaryIdList, @Param("status") Integer status);
}
