package com.biz.credit.dao;

import com.biz.credit.domain.ApiInputFileDetailContact;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiInputFileDetailContactDAO {
    int addContact(@Param("apiInputFileDetailContact") ApiInputFileDetailContact apiInputFileDetailContact);
    int addContacts(List<ApiInputFileDetailContact> apiInputFileDetailContacts);
}
