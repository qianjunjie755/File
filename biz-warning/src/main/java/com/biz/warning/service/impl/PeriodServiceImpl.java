package com.biz.warning.service.impl;

import com.biz.warning.dao.PeriodDAO;
import com.biz.warning.domain.Period;
import com.biz.warning.service.IPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeriodServiceImpl implements IPeriodService{
    @Autowired
    private PeriodDAO dao;

    @Override
    public List<Period>  findAllPeriod() {
        return dao.findAllPeriod();
    }
}
