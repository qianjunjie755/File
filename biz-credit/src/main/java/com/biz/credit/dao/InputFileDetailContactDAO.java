package com.biz.credit.dao;

import com.biz.credit.domain.InputFileDetailContact;
import com.biz.credit.vo.InputFileDetailContactVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InputFileDetailContactDAO {
    int addContact(@Param("inputFileDetailContact") InputFileDetailContact inputFileDetailContact);
    int addContacts(List<InputFileDetailContact> inputFileDetailContacts);
    int insertContactParams(@Param("id") Integer contactId, @Param("list") List<com.biz.credit.domain.Param> params);
    List<InputFileDetailContact> findInputFileDetailContactList(@Param("inputFileDetailId") Integer inputFileDetailId);
    List<InputFileDetailContactVO> findInputFileDetailContactVOList(Integer inputFileDetailId);
}
