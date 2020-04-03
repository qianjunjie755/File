package com.biz.credit.service;

import com.biz.credit.vo.BIInputFileDetailHistoryVO;

import java.util.List;

public interface IBIInputFileDetailHistoryService {
    List<BIInputFileDetailHistoryVO> findListByPage(BIInputFileDetailHistoryVO biInputFileDetailHistoryVO);
    String findColumnHead(BIInputFileDetailHistoryVO biInputFileDetailHistoryVO);
}
