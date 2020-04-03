package com.biz.credit.utils.comparators;

import com.biz.credit.vo.DTaskParamVO;

import java.util.Comparator;

public class DTaskParamVOComparator implements Comparator<DTaskParamVO> {
    @Override
    public int compare(DTaskParamVO o1, DTaskParamVO o2) {
        return o1.getOrder().compareTo(o2.getOrder());
    }
}
