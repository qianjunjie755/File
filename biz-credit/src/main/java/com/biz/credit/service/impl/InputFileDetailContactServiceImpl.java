package com.biz.credit.service.impl;

import com.biz.credit.dao.InputFileDetailContactDAO;
import com.biz.credit.domain.InputFileDetailContact;
import com.biz.credit.service.IInputFileDetailContactService;
import com.biz.credit.vo.InputFileDetailContactVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InputFileDetailContactServiceImpl implements IInputFileDetailContactService {
    @Autowired
    private InputFileDetailContactDAO inputFileDetailContactDAO;

    @Override
    public int addInputFileDetailContact(InputFileDetailContact inputFileDetailContact) {
        return inputFileDetailContactDAO.addContact(inputFileDetailContact);
    }

    @Override
    public int addInputFileDetailContactList(List<InputFileDetailContact> list) {
        return inputFileDetailContactDAO.addContacts(list);
    }

    @Override
    public List<InputFileDetailContact> findInputFileDetailContactList(Integer inputFileDetailId) {
        return inputFileDetailContactDAO.findInputFileDetailContactList(inputFileDetailId);
    }

    @Override
    public List<InputFileDetailContactVO> findInputFileDetailContactVOList(Integer inputFileDetailId) {
        return inputFileDetailContactDAO.findInputFileDetailContactVOList(inputFileDetailId);
    }
}
