package com.biz.credit.dao;

import com.biz.credit.vo.InputFileDetailVO;
import com.biz.credit.vo.reportApiVO.ApiInputFileDetailVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiInputFileDetailDAO {
    int addApiInputFileDetail(@Param("apiInputFileDetail") ApiInputFileDetailVO apiInputFileDetail);
    int addInputFileParams(@Param("id") Long inputFileDetailId, @Param("list") List<com.biz.credit.domain.Param> params);
    ApiInputFileDetailVO queryApiInputFileDetailById(@Param("apiInputFileDetail") ApiInputFileDetailVO apiInputFileDetail);
    InputFileDetailVO findInputFileDetail(@Param("inputFileDetail") InputFileDetailVO inputFileDetailVO);

}
