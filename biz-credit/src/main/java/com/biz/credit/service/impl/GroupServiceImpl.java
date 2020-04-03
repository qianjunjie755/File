package com.biz.credit.service.impl;


import com.biz.credit.dao.GroupDao;
import com.biz.credit.service.GroupService;
import com.biz.credit.vo.SystemGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupDao groupDao;

    @Override
    public List<SystemGroupVO> queryGroupList() {
        return groupDao.queryGroupList();
    }
}
