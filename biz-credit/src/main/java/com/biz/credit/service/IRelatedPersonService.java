package com.biz.credit.service;

import com.biz.credit.vo.BIInputFileDetailHistoryVO;

public interface IRelatedPersonService {
    void findRelatedPersons(BIInputFileDetailHistoryVO biInputFileDetailHistoryVO);
    String findRelatedPersonHead(BIInputFileDetailHistoryVO biInputFileDetailHistoryVO);
}
