package com.biz.search.repository;

import com.biz.search.entity.BasicInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LoadBasicDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询基础信息
     *
     * @return
     */
    public List<BasicInfo> queryBasicInfo(int limit) {
        String sql = "SELECT id, company_name, credit_code, legal_person_name, register_no, register_capital, register_address," +
                     " run_status, province, set_up_time, biz_scope FROM t_basic_info WHERE is_load = 0 LIMIT ?";
        return jdbcTemplate.query(sql, (resultSet, i) -> {
            BasicInfo basicInfo = new BasicInfo();
            basicInfo.setId(resultSet.getLong("id"));
            basicInfo.setCompanyName(resultSet.getString("company_name"));
            basicInfo.setCreditCode(resultSet.getString("credit_code"));
            basicInfo.setLegalPersonName(resultSet.getString("legal_person_name"));
            basicInfo.setRegisterNo(resultSet.getString("register_no"));
            basicInfo.setRegisterCapital(resultSet.getString("register_capital"));
            basicInfo.setRegisterAddress(resultSet.getString("register_address"));
            basicInfo.setRunStatus(resultSet.getString("run_status"));
            basicInfo.setProvince(resultSet.getString("province"));
            basicInfo.setSetUpTime(resultSet.getString("set_up_time"));
            basicInfo.setBizScope(resultSet.getString("biz_scope"));
            return basicInfo;
        }, limit);
    }

    /**
     * 批量更新加载状态
     *
     * @param data
     */
    public void updateLoadState(List<BasicInfo> data) {
        String sql = "update t_basic_info set is_load = 1, update_time= now() where id = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                BasicInfo basicInfo = data.get(i);
                preparedStatement.setLong(1, basicInfo.getId());
            }

            @Override
            public int getBatchSize() {
                return data.size();
            }
        });
    }
}
