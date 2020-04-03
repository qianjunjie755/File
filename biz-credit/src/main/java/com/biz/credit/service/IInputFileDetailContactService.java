package com.biz.credit.service;

import com.biz.credit.domain.InputFileDetailContact;
import com.biz.credit.vo.InputFileDetailContactVO;

import java.util.List;

public interface IInputFileDetailContactService {
    int addInputFileDetailContact(InputFileDetailContact inputFileDetailContact);
    int addInputFileDetailContactList(List<InputFileDetailContact> list);
    List<InputFileDetailContact> findInputFileDetailContactList(Integer inputFileDetailId);
    List<InputFileDetailContactVO> findInputFileDetailContactVOList(Integer inputFileDetailId);
}
