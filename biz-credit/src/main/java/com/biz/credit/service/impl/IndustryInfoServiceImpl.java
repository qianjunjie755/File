package com.biz.credit.service.impl;

import com.biz.credit.dao.IndustryInfoDAO;
import com.biz.credit.service.IIndustryInfoService;
import com.biz.credit.vo.IndustryInfoVO;
import com.biz.credit.vo.IndustryInfoViewVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class IndustryInfoServiceImpl implements IIndustryInfoService {
    @Autowired
    private IndustryInfoDAO industryInfoDAO;
    @Override
    public List<IndustryInfoVO> findAllIndustryInfoVOList() {
        return industryInfoDAO.findAllIndustryInfoVOList();
    }

    @Override
    public List<IndustryInfoVO> findAllIndustryInfoVOAndVersionList() {
        return industryInfoDAO.findAllIndustryInfoVOAndVersionList();
    }

    @Override
    public List<IndustryInfoViewVO> findAllIndustryInfoVOListView() {
        List<IndustryInfoViewVO> viewList = new ArrayList<>();
        List<IndustryInfoVO> list =  industryInfoDAO.findAllIndustryInfoVOList();
        Set<String> industryCodeSet = new LinkedHashSet<>();
        list.forEach(industryInfoVO -> {
            industryCodeSet.add(industryInfoVO.getIndustryCode());
        });
        industryCodeSet.forEach(industryCode->{
            viewList.add(findByIndustryCode(industryCode, StringUtils.EMPTY));
        });
        return viewList;
    }

    @Override
   public IndustryInfoViewVO findByIndustryCode(String industryCode,String version)  {
        List<IndustryInfoVO> list = industryInfoDAO.findIndustryInfoVOByIndustryCode(industryCode);
        if(StringUtils.isNotEmpty(version)){
            int index = -1;
            for(int i=0;i<list.size();i++){
                if(StringUtils.equals(version,String.format("%.1f",list.get(i).getModelVersion()))){
                    index = i;
                    break;
                }
            }
            list.add(0,list.remove(index));
        }
        return new IndustryInfoViewVO(list);
    }
}
