package com.biz.credit.dao;

import com.biz.credit.vo.BIInputFileDetailHistoryVO;
import com.biz.credit.vo.RelatedPersonVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BIInputFileDetailHistoryDAO {
    List<BIInputFileDetailHistoryVO> findListByPage(@Param("biQuery") BIInputFileDetailHistoryVO biInputFileDetailHistoryVO);
    List<BIInputFileDetailHistoryVO> findListByPageForApiInput(@Param("biQuery") BIInputFileDetailHistoryVO biInputFileDetailHistoryVO);
    String findRelatedModuleTypeRelatedPeronHeadByApiCode(@Param("biQuery") BIInputFileDetailHistoryVO biInputFileDetailHistoryVO);
    List<RelatedPersonVO> findRaltedPersonListByInputFileDetailId(@Param("inputFileDetailId")Integer inputFileDetailId);
    List<RelatedPersonVO> findRaltedPersonListByInputFileDetailIdForApiInput(@Param("inputFileDetailId")Integer inputFileDetailId);
    List<String> findColumnHeadList(@Param("biQuery") BIInputFileDetailHistoryVO biInputFileDetailHistoryVO);
}
