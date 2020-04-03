package com.biz.credit.dao;

import com.biz.credit.vo.IndustryInfoVO;
import com.biz.credit.vo.ReportVariableVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndustryInfoDAO {
    List<IndustryInfoVO> findListByIndustryInfoVO(@Param("industryInfoVO") IndustryInfoVO industryInfoVO);
    IndustryInfoVO findIndustryInfoVOByIndustryId(@Param("industryId") Integer industryId);
    List<IndustryInfoVO> findIndustryInfoVOByIndustryCode(@Param("industryCode") String industryCode);
    IndustryInfoVO findIndustryInfoVOByIndustryInfoVO(@Param("industryInfoVO") IndustryInfoVO industryInfoVO) throws Exception;
    IndustryInfoVO findIndustryInfoByIndustryType(@Param("industryInfoVO") IndustryInfoVO industryInfoVO) throws Exception;
    List<ReportVariableVO> findScoreVariableList(@Param("industryInfoVO") IndustryInfoVO industryInfoVO) throws Exception;
    List<String> findApiProdCodeListByIndustryId(@Param("industryId") Integer industryId);
    List<IndustryInfoVO> findAllIndustryInfoVOList();
    List<IndustryInfoVO> findAllIndustryInfoVOAndVersionList();
}
