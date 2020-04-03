package com.biz.credit.utils.comparators;

import com.biz.credit.vo.VarThresholdVO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VarThresholdVOComparator implements Comparator<VarThresholdVO> {
    @Override
    public int compare(VarThresholdVO o1, VarThresholdVO o2) {
        if(StringUtils.isNotEmpty(o1.getThreshold())&&StringUtils.isNotEmpty(o2.getThreshold())){
            if(NumberUtils.isNumber(o1.getThreshold())&&NumberUtils.isNumber(o2.getThreshold())){
                return NumberUtils.compare(NumberUtils.toDouble(o1.getThreshold()),NumberUtils.toDouble(o2.getThreshold()));
            }
            return o1.getThreshold().compareTo(o2.getThreshold());
        }
        return 0;
    }

}
