package com.biz.warning.service;

import com.biz.warning.domain.Dict;

import java.util.List;

public interface IDictService {

    List<Dict> queryByGroupCode(String groupCode);

}
