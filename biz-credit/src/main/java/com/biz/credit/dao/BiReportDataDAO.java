package com.biz.credit.dao;

import com.biz.credit.domain.BiReportData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiReportDataDAO {
    int addBiReportDataList(List<BiReportData> biReportDataList) throws Exception;
}
