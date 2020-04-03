package com.biz.credit.dao;

import com.biz.credit.vo.QuotaModelVarVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuotaModelVarDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<QuotaModelVarVO> findModelVarList(Integer modelId){
        String sql = "select model_id, api_prod_code, api_version, var_code, var_name, var_version, var_interval, var_json_path, param_name, status,date_format(create_time,'%Y-%m-%d %T') create_time from t_quota_model_var where status=1 and model_id=? order by create_time desc";
        List<QuotaModelVarVO> list = jdbcTemplate.query(sql,(rs,i)->new QuotaModelVarVO(
                rs.getInt("model_id")
                ,rs.getString("api_prod_code")
        ,rs.getString("api_version")
                ,rs.getString("var_code")
        ,rs.getString("var_name")
        ,rs.getString("var_version")
        ,rs.getString("var_interval")
        ,rs.getString("var_json_path")
        ,rs.getString("param_name")
        ,rs.getInt("status")
        ,rs.getString("create_time")),modelId);
        return list;
    }
}
