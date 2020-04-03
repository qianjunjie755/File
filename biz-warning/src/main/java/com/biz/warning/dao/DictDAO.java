package com.biz.warning.dao;

import com.biz.warning.domain.Dict;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictDAO {

    List<Dict> queryByGroupCode(@Param("groupCode") String groupCode);
}
