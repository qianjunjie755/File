package com.biz.credit.dao;

import com.biz.credit.vo.BiRuleDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InputTestDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addVarResults(){

    }
}
