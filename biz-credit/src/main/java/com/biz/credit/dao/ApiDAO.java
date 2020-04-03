package com.biz.credit.dao;

import com.biz.credit.vo.ApiRequestVO;
import com.biz.credit.vo.ApiResponseVO;
import com.biz.credit.vo.ApiVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ApiDAO {
    List<ApiVO> findApiList(@Param("apiVO") ApiVO apiVO);
    List<ApiVO> findBizValidApiList();
    int updateApi(@Param("apiVO") ApiVO apiVO);
    int addApi(@Param("apiVO") ApiVO apiVO);
    List<ApiRequestVO> findApiRequestVOList(@Param("apiRequestVO") ApiRequestVO apiRequestVO);
    int updateApiRequest(@Param("apiRequestVO") ApiRequestVO apiRequestVO);
    int addApiRequest(@Param("apiRequestVO") ApiRequestVO apiRequestVO);
    List<ApiResponseVO> findApiResponseVOList(@Param("apiResponseVO") ApiResponseVO apiResponseVO);
    int updateApiResponseVO(@Param("apiResponseVO") ApiResponseVO apiResponseVO);
    int addApiResponseVO(@Param("apiResponseVO") ApiResponseVO apiResponseVO);
    int updateApiResponseVOStatusByApiId(@Param("apiId") Long apiId, @Param("status") Integer status);


    List<ApiVO> queryList(@Param("api") ApiVO apiVO);

    List<Map<String,Object>> querySourceCount(@Param("apiCode") String apiCode);

    List<ApiRequestVO> queryApiRequestList(@Param("apiRequestVO") ApiRequestVO apiRequestVO);

    List<ApiResponseVO> queryApiResponseList(@Param("apiResponseVO") ApiResponseVO apiResponseVO);

    ApiVO queryApiByCodeAndVersion(@Param("apiVO") ApiVO apiVO);
}
