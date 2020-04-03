package com.biz.warning.dao;

import com.biz.warning.vo.WarningNoticeVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarningNoticeDAO {
    List<WarningNoticeVO> findList(@Param("wck") WarningNoticeVO warningNoticeVO);
    List<WarningNoticeVO> findWarnVariableResultList(@Param("wck") WarningNoticeVO warningNoticeVO);
    List<WarningNoticeVO> findWarnVariableResultListUnRead(@Param("wck") WarningNoticeVO warningNoticeVO);
    List<WarningNoticeVO> findWarnVariableResultListRead(@Param("wck") WarningNoticeVO warningNoticeVO);
    int addWarningNotice(@Param("wck") WarningNoticeVO warningNoticeVO);
    int addWarningNoticeUnRead(@Param("wck") WarningNoticeVO warningNoticeVO);
    int deleteWaringNotceUnread(@Param("wck") WarningNoticeVO warningNoticeVO);
    int updateWarningNotice(@Param("wck") WarningNoticeVO warningNoticeVO);
    int findWarningNoticeCount(@Param("wck") WarningNoticeVO warningNoticeVO);
    List<WarningNoticeVO> findListByWarnResultVariableIdList(List<Long> list);
    long findUnReadWarningNoticeCount(@Param("wck") WarningNoticeVO warningNoticeVO);
    WarningNoticeVO findSingleWarningNotice(@Param("wck") WarningNoticeVO warningNoticeVO);
    WarningNoticeVO findSingleWarningNoticeUnRead(@Param("wck") WarningNoticeVO warningNoticeVO);
    WarningNoticeVO findWarnVariableResultById(@Param("wck") WarningNoticeVO warningNoticeVO);
    List<WarningNoticeVO> findListFromStartToEnd(@Param("start") Long start, @Param("end") Long end);
}

