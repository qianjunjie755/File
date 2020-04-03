package com.biz.credit.dao;

import com.biz.credit.vo.QuotaModelVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class QuotaModelDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<QuotaModelVO> findModelVOList(Integer modelId,String modelName){
        List<Object> paramList = new ArrayList<>();
        StringBuffer sql = new StringBuffer("select model_id,model_name,model_version,calc_type,model_path,status,date_format(create_time,'%Y-%m-%d %T') create_time from t_quota_model where status=1 ");
        if(Objects.nonNull(modelId)){
            sql.append(" and model_id=?");
            paramList.add(modelId);
        }
        if(StringUtils.isNotEmpty(modelName)){
            sql.append(" and model_name=?");
            paramList.add(modelName);
        }
        sql.append(" order by model_id desc");
        List<QuotaModelVO> list = jdbcTemplate.query(sql.toString(),(resultSet,i )->new QuotaModelVO(resultSet.getInt("model_id"),resultSet.getString("model_name"),resultSet.getString("model_version"),resultSet.getInt("calc_type"),resultSet.getString("model_path"),resultSet.getInt("status"),resultSet.getString("create_time")), paramList.size()==0?null:paramList.toArray());
        return  list;
    }
    public int addModels(List<QuotaModelVO> list){
        String sql = "insert into report_risk.t_quota_model (model_id, model_name, model_version, model_path) values (?,?,?,?)";
        int[] ids = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                QuotaModelVO moodel = list.get(i);
                ps.setObject(1, moodel.getModelId());
                ps.setObject(2, moodel.getModelName());
                ps.setObject(3, moodel.getModelVersion());
                ps.setObject(4, moodel.getModelPath());
            }
            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
        return Objects.nonNull(ids)?ids.length:0;
    }
}
