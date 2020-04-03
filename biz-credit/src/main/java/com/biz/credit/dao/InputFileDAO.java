package com.biz.credit.dao;

import com.biz.credit.domain.InputFile;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InputFileDAO {
    int addInputFile(@Param("inputFile") InputFile inputFile)throws Exception;
    int updateInputFile(@Param("inputFile") InputFile inputFile)throws Exception;
    List<InputFile> findInputFileList(@Param("inputFile") InputFile inputFile)throws Exception;
}
