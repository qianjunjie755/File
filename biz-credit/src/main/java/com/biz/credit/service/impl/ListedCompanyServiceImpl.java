package com.biz.credit.service.impl;

import com.biz.credit.dao.ListedCompanyDAO;
import com.biz.credit.service.IListedCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListedCompanyServiceImpl implements IListedCompanyService {

    @Autowired
    private ListedCompanyDAO dao;

    @Override
    public String getStockCode(String companyName) {
        return dao.queryStockCode(companyName);
    }
}
