package com.biz.warning.service.impl;

import com.biz.warning.dao.FrequencyDAO;
import com.biz.warning.domain.FrequencyPool;
import com.biz.warning.service.IFrequencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrequencyServiceImpl implements IFrequencyService {
    @Autowired
    private FrequencyDAO dao;

    @Override
    public List<FrequencyPool>  findAllFrequency() {
        return dao.findAllFrequency();
    }
}
