package com.biz.credit.dao;

import com.biz.credit.domain.UploadRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadRecordDAO {
    int saveUploadRecord(List<UploadRecord> uploadRecordList)throws Exception;
}
