package com.biz.credit.utils;

import com.biz.credit.dao.ReportVariableDAO;
import com.biz.credit.vo.ReportVariableVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DataTransferUtil {
    @Autowired
    private ReportVariableDAO reportVariableDAO;
    public void variablePeriodTransfer() throws Exception{
        List<ReportVariableVO> variableList = reportVariableDAO.findStrongRuleVariableList();
        for(ReportVariableVO reportVariableVO : variableList){

        }
    }
}
