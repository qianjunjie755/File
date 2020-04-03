package com.biz.credit.dao;

import com.biz.credit.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDAO {
    List<User> findSuperAdmins(@Param("apiCode")String apiCode);
}
