package com.biz.warning.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailDAO {
    Integer insertEmailContent(@Param("apiCode") String apiCode,
                               @Param("userId") Integer userId,
                               @Param("email") String email,
                               @Param("subject") String subject,
                               @Param("content") String content,
                               @Param("status") Integer status);
}
