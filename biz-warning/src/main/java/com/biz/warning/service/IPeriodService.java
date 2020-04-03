package com.biz.warning.service;

import com.biz.warning.domain.Period;

import java.util.List;

/**
 *  时间范围维护接口
 */
public interface IPeriodService {
    List<Period>  findAllPeriod();
}
