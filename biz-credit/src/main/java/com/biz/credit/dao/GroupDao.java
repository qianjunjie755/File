package com.biz.credit.dao;

import com.biz.credit.vo.SystemGroupVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupDao {


    List<SystemGroupVO> queryGroupList();
}
