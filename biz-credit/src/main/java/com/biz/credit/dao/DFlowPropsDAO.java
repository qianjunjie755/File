package com.biz.credit.dao;

import com.biz.credit.vo.DFlowBizVO;
import com.biz.credit.vo.DFlowLinkVO;
import com.biz.credit.vo.DFlowPlatformVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class DFlowPropsDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public List<DFlowPlatformVO> findPlatformList(Integer platformId){
        StringBuffer sql = new StringBuffer();
        if(Objects.isNull(platformId)){
            sql.append("select id,platform_name,status,date_format(create_time,'%Y-%m-%d %T') create_time,date_format(update_time,'%Y-%m-%d %T') update_time from report.t_d_flow_platform where status=1 order by id asc");
            List<DFlowPlatformVO> list = jdbcTemplate.query(sql.toString(),(resultSet, i)->new DFlowPlatformVO(resultSet.getInt("id"),resultSet.getString("platform_name"),resultSet.getInt("status"),resultSet.getString("create_time"),resultSet.getString("update_time")));
            return list;
        }
        sql.append("select id,platform_name,status,date_format(create_time,'%Y-%m-%d %T') create_time,date_format(update_time,'%Y-%m-%d %T') update_time from report.t_d_flow_platform where status=1 and id=? order by id asc");
        List<DFlowPlatformVO> list = jdbcTemplate.query(sql.toString(),(resultSet, i)->new DFlowPlatformVO(resultSet.getInt("id"),resultSet.getString("platform_name"),resultSet.getInt("status"),resultSet.getString("create_time"),resultSet.getString("update_time")),platformId);
        return list;
    }


    public List<DFlowBizVO> findBizList(Integer platformId,Integer bizId){
        StringBuffer sql = new StringBuffer();
        if(Objects.isNull(platformId)){
            if(Objects.isNull(bizId)){
                sql.append("select id,biz_name,platform_id,status,date_format(create_time,'%Y-%m-%d %T') create_time,date_format(update_time,'%Y-%m-%d %T') update_time from report.t_d_flow_biz where status=1 order by id asc");
                List<DFlowBizVO> list = jdbcTemplate.query(sql.toString(),(resultSet, i)->new DFlowBizVO(resultSet.getInt("id"),resultSet.getString("biz_name"),resultSet.getInt("platform_id"),resultSet.getInt("status"),resultSet.getString("create_time"),resultSet.getString("update_time")));
                return list;
            }
            sql.append("select id,biz_name,platform_id,status,date_format(create_time,'%Y-%m-%d %T') create_time,date_format(update_time,'%Y-%m-%d %T') update_time from report.t_d_flow_biz where status=1 and biz_id=? order by id asc");
            List<DFlowBizVO> list = jdbcTemplate.query(sql.toString(),(resultSet, i)->new DFlowBizVO(resultSet.getInt("id"),resultSet.getString("biz_name"),resultSet.getInt("platform_id"),resultSet.getInt("status"),resultSet.getString("create_time"),resultSet.getString("update_time")),bizId);
            return list;
        }
        sql.append("select id,biz_name,platform_id,status,date_format(create_time,'%Y-%m-%d %T') create_time,date_format(update_time,'%Y-%m-%d %T') update_time from report.t_d_flow_biz where status=1 and platform_id=? order by id asc");
        List<DFlowBizVO> list = jdbcTemplate.query(sql.toString(),(resultSet, i)->new DFlowBizVO(resultSet.getInt("id"),resultSet.getString("biz_name"),resultSet.getInt("platform_id"),resultSet.getInt("status"),resultSet.getString("create_time"),resultSet.getString("update_time")),platformId);
        return list;
    }


    public List<DFlowLinkVO> findLinkList(Integer bizId, Integer linkId){
        StringBuffer sql = new StringBuffer();
        if(Objects.isNull(bizId)){
            if(Objects.isNull(linkId)){
                sql.append("select id,link_name,biz_id,status,date_format(create_time,'%Y-%m-%d %T') create_time,date_format(update_time,'%Y-%m-%d %T') update_time from report.t_d_flow_link where status=1 order by id asc");
                List<DFlowLinkVO> list = jdbcTemplate.query(sql.toString(),(resultSet, i)->new DFlowLinkVO(resultSet.getInt("id"),resultSet.getString("link_name"),resultSet.getInt("biz_id"),resultSet.getInt("status"),resultSet.getString("create_time"),resultSet.getString("update_time")));
                return list;
            }
            sql.append("select id,link_name,biz_id,status,date_format(create_time,'%Y-%m-%d %T') create_time,date_format(update_time,'%Y-%m-%d %T') update_time from report.t_d_flow_link where status=1 and link_id=? order by id asc");
            List<DFlowLinkVO> list = jdbcTemplate.query(sql.toString(),(resultSet, i)->new DFlowLinkVO(resultSet.getInt("id"),resultSet.getString("link_name"),resultSet.getInt("biz_id"),resultSet.getInt("status"),resultSet.getString("create_time"),resultSet.getString("update_time")),linkId);
            return list;
        }
        sql.append("select id,link_name,biz_id,status,date_format(create_time,'%Y-%m-%d %T') create_time,date_format(update_time,'%Y-%m-%d %T') update_time from report.t_d_flow_link where status=1 and biz_id=? order by id asc");
        List<DFlowLinkVO> list = jdbcTemplate.query(sql.toString(),(resultSet, i)->new DFlowLinkVO(resultSet.getInt("id"),resultSet.getString("link_name"),resultSet.getInt("biz_id"),resultSet.getInt("status"),resultSet.getString("create_time"),resultSet.getString("update_time")),bizId);
        return list;
    }

}
