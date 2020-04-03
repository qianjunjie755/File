package com.biz.warning.service;

import com.biz.warning.domain.FrequencyPool;

import java.util.List;

/**
 *  频次维护接口
 */
public interface IFrequencyService {
    List<FrequencyPool>  findAllFrequency();
}
