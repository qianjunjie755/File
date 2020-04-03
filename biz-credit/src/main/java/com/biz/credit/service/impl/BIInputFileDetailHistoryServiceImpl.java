package com.biz.credit.service.impl;

import com.biz.credit.dao.BIInputFileDetailHistoryDAO;
import com.biz.credit.dao.UserDAO;
import com.biz.credit.domain.User;
import com.biz.credit.service.IBIInputFileDetailHistoryService;
import com.biz.credit.vo.BIInputFileDetailHistoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BIInputFileDetailHistoryServiceImpl implements IBIInputFileDetailHistoryService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private BIInputFileDetailHistoryDAO biInputFileDetailHistoryDAO;

    @Override
    public List<BIInputFileDetailHistoryVO> findListByPage(BIInputFileDetailHistoryVO biInputFileDetailHistoryVO) {
        List<BIInputFileDetailHistoryVO> list = Objects.equals(1, biInputFileDetailHistoryVO.getInputType()) ? biInputFileDetailHistoryDAO.findListByPage(biInputFileDetailHistoryVO) : biInputFileDetailHistoryDAO.findListByPageForApiInput(biInputFileDetailHistoryVO);
        if (Objects.equals(2, biInputFileDetailHistoryVO.getInputType())) {
            List<User> admins = userDAO.findSuperAdmins(biInputFileDetailHistoryVO.getApiCode());
            User superAdmin = admins.iterator().next();
            list.forEach(data -> {
                data.setInputType(biInputFileDetailHistoryVO.getInputType());
                data.setRealname(superAdmin.getRealname());
                data.setGroupName("超级管理员");
                data.setUsername(superAdmin.getUsername());
            });
        }
        return list;
    }

    @Override
    public String findColumnHead(BIInputFileDetailHistoryVO biInputFileDetailHistoryVO) {
        List<String> list = biInputFileDetailHistoryDAO.findColumnHeadList(biInputFileDetailHistoryVO);
        return list.get(0);
    }
}