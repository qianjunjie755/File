package com.biz.credit.dao;

import com.biz.credit.domain.InputFileDetail;
import com.biz.credit.vo.InputFileDetailParamVO;
import com.biz.credit.vo.InputFileDetailVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InputFileDetailDAO {
    int addInputFileDetail(@Param("inputFileDetail") InputFileDetail inputFileDetail);
    int addInputFileDetails(List<InputFileDetail> inputFileDetails);
    int addInputFileParams(@Param("id") Integer inputFileDetailId, @Param("list") List<com.biz.credit.domain.Param> params);
    int updateInputFileDetail(@Param("inputFileDetail") InputFileDetail inputFileDetail);
    int updateApiInputFileDetail(@Param("inputFileDetail") InputFileDetail inputFileDetail);
    List<InputFileDetailVO> findInputFileDetailList(@Param("inputFileDetail") InputFileDetail inputFileDetail);
    InputFileDetailVO findInputFileDetail(@Param("inputFileDetail") InputFileDetailVO inputFileDetail);
    List<InputFileDetailParamVO> findInputFileDetailParams(@Param("inputFileDetailId") Integer inputFileDetailId);
    InputFileDetailVO findApiInputFileDetail(@Param("inputFileDetail") InputFileDetailVO inputFileDetail);
    List<InputFileDetail> queryTaskByInputDetailName(@Param("inputFileDetail") InputFileDetailVO inputFileDetail);
    List<InputFileDetailVO> findInputFileDetailByTaskId(@Param("inputFileDetail") InputFileDetailVO inputFileDetail);
    int updateFailedInputFileDetail(@Param("inputFileDetail") InputFileDetailVO inputFileDetail);
}
