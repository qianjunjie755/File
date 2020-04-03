package com.biz.credit.service;

public interface IProjectApiService {
    boolean checkProdCodeValid(String apiCode, String prodCode, Double version);
}
