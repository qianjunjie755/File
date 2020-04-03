package com.biz.credit.dao;

import com.biz.credit.domain.UserGroup;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface UserGroupDAO {
    @MapKey("id")
    Map<Integer, UserGroup> findUserGroupNameMapByApiCode(@Param("apiCode") String apiCode) throws Exception;
}
