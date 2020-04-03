package com.biz.search.repository;

import com.biz.search.entity.BasicInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BasicInfoRepository extends ElasticsearchRepository<BasicInfo, Long> {

}
