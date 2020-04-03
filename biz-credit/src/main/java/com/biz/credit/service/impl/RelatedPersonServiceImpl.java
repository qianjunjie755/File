package com.biz.credit.service.impl;

import com.biz.credit.dao.BIInputFileDetailHistoryDAO;
import com.biz.credit.service.IRelatedPersonService;
import com.biz.credit.vo.BIInputFileDetailHistoryVO;
import com.biz.credit.vo.RelatedPersonVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class RelatedPersonServiceImpl implements IRelatedPersonService {
    @Autowired
    private BIInputFileDetailHistoryDAO biInputFileDetailHistoryDAO;
    @Override
    public void findRelatedPersons(BIInputFileDetailHistoryVO biInputFileDetailHistoryVO) {
        if(StringUtils.isNotEmpty(biInputFileDetailHistoryVO.getRelatedPersonHead())){
            List<RelatedPersonVO> relatedPersonVOList = Objects.equals(2,biInputFileDetailHistoryVO.getInputType()) ?biInputFileDetailHistoryDAO.findRaltedPersonListByInputFileDetailIdForApiInput(biInputFileDetailHistoryVO.getInputFileDetailId()):biInputFileDetailHistoryDAO.findRaltedPersonListByInputFileDetailId(biInputFileDetailHistoryVO.getInputFileDetailId());
            if(CollectionUtils.isEmpty(relatedPersonVOList)){
                relatedPersonVOList = new ArrayList<>();
                relatedPersonVOList.add(new RelatedPersonVO());relatedPersonVOList.add(new RelatedPersonVO());
            }else if(1==relatedPersonVOList.size()){
                relatedPersonVOList.add(new RelatedPersonVO());
            }
            biInputFileDetailHistoryVO.setPersonVOList(relatedPersonVOList);
        }
    }

    @Override
    public String findRelatedPersonHead(BIInputFileDetailHistoryVO biInputFileDetailHistoryVO) {
        String ret =  biInputFileDetailHistoryDAO.findRelatedModuleTypeRelatedPeronHeadByApiCode(biInputFileDetailHistoryVO);
        if(StringUtils.isNotEmpty(ret)){
            ret = ret.concat("_").concat(ret);
        }
        return ret;
    }
}
