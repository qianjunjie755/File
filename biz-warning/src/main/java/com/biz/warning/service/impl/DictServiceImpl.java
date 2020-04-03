package com.biz.warning.service.impl;

import com.biz.warning.dao.DictDAO;
import com.biz.warning.domain.Dict;
import com.biz.warning.service.IDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictServiceImpl implements IDictService {

    @Autowired
    private DictDAO dictDAO;

    @Override
    public List<Dict> queryByGroupCode(String groupCode) {
        return dictDAO.queryByGroupCode(groupCode);
    }
}
