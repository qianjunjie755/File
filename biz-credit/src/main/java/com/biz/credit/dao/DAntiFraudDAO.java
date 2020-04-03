package com.biz.credit.dao;

import com.biz.credit.domain.DNodeParam;
import com.biz.credit.vo.DAntiFraudVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DAntiFraudDAO {
    List<DAntiFraudVO> queryList(@Param("antiFraud") DAntiFraudVO antiFraudVO);
    List<DNodeParam> queryParamsAntiFraud(@Param("id") Long id);
}
