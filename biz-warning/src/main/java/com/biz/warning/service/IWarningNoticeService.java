package com.biz.warning.service;

import com.biz.warning.vo.WarningNoticeVO;

import java.util.List;

public interface IWarningNoticeService {
    List<WarningNoticeVO> findList(WarningNoticeVO warningNoticeVO);
    int readWarnResultVariable(WarningNoticeVO warningNoticeVO);
    long findUnReadWarningNoticeCount(WarningNoticeVO warningNoticeVO);
    WarningNoticeVO findSingleWarningNotice(WarningNoticeVO warningNoticeVO);
    WarningNoticeVO findSingleWarningNoticeUnRead(WarningNoticeVO warningNoticeVO);
    void initWarningNotices(Long start, Long end) throws Exception;
}
