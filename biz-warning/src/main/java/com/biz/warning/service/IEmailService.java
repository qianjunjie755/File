package com.biz.warning.service;

import com.biz.warning.vo.CompanyUser;

public interface IEmailService {
    void send(CompanyUser user, String subject, String content);
}
