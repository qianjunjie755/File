package com.biz.warning.util;

import com.biz.warning.vo.RuleSetVO;

import java.util.Comparator;

public class RuleSetVORuleSetCodeComparator implements Comparator<RuleSetVO> {
    @Override
    public int compare(RuleSetVO o1, RuleSetVO o2) {
        return o1.getRuleSetCode().compareTo(o2.getRuleSetCode());
    }
}
