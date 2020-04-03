package com.biz.credit.service.impl;

import com.biz.credit.dao.ProjectApiDAO;
import com.biz.credit.service.IProjectApiService;
import com.biz.credit.utils.DateUtil;
import com.biz.credit.vo.ProjectApiVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class ProjectApiServiceImpl implements IProjectApiService {
    @Autowired
    private ProjectApiDAO projectApiDAO;
    @Override
    public boolean checkProdCodeValid(String apiCode, String prodCode,Double version) {
        if(StringUtils.isNotEmpty(apiCode)
                &&StringUtils.isNotEmpty(prodCode)
                &&null!=version){
            List<ProjectApiVO> list = projectApiDAO.findList(new ProjectApiVO(apiCode,prodCode,version));
            if(!CollectionUtils.isEmpty(list)){
                ProjectApiVO validProjectApiVO = list.iterator().next();
                DateTime now = DateTime.now();
                int nowDate = Integer.parseInt(now.toString(DateUtil.DATE_FORMAT_YYYYMMDD));
                int validEnd = Integer.parseInt(validProjectApiVO.getValidEnd().split(" ")[0].replaceAll("-",StringUtils.EMPTY));
                if(nowDate<=validEnd){
                    return true;
                }
            }
        }
        return false;
    }
}
