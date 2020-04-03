package com.biz.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.biz.search.entity.BasicInfo;
import com.biz.search.entity.BasicInfoHighLight;
import com.biz.search.repository.BasicInfoRepository;
import com.biz.search.repository.LoadBasicDAO;
import com.biz.search.service.IBasicInfoService;
import com.biz.search.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BasicInfoServiceImpl implements IBasicInfoService {

    @Autowired
    private LoadBasicDAO basicDAO;

    @Autowired
    private BasicInfoRepository basicRepository;

    @Autowired
    private ElasticsearchTemplate elasticTemplate;

    @Override
    @Transactional
    public int loadBasicInfo(int limit) {
        List<BasicInfo> data = basicDAO.queryBasicInfo(limit);
        if (CollectionUtils.isEmpty(data)) {
            log.info("企业工商基本信息数据已全部加载完成!");
            return 0;
        }
        basicDAO.updateLoadState(data);
        basicRepository.saveAll(data);
        log.info("企业工商基本信息数据已加载[{}]条!", data.size());
        return data.size();
    }

    @Override
    public Page<BasicInfo> queryByCompanyName(String companyName, int pageNo, int pageSize) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("companyName", companyName))
                .withPageable(PageRequest.of(pageNo - 1, pageSize))
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .build();
        return basicRepository.search(searchQuery);
    }

    @Override
    public Page<BasicInfoHighLight> queryHighLightByCompanyName(String companyName, int pageNo, int pageSize) {
        //创建查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //设置索引
        queryBuilder.withIndices(Constants.BASIC_INDEX).withTypes(Constants.BASIC_TYPE);
        //查询关键字
        queryBuilder.withQuery(QueryBuilders.matchQuery("companyName", companyName));
        //设置高亮
        queryBuilder.withHighlightFields(new HighlightBuilder.Field("companyName").preTags("<font color='red'>").postTags("</font>"));
        //设置分页
        queryBuilder.withPageable(PageRequest.of(pageNo - 1, pageSize));
        //设置排序
        queryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        //分页查询
        return elasticTemplate.queryForPage(queryBuilder.build(), BasicInfoHighLight.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                //从response中获取结果
                SearchHits hits = response.getHits();
                //定义一个集合
                List<BasicInfoHighLight> data = new ArrayList<>();
                //获取命中文本结果
                for (SearchHit hit : hits) {
                    //获取高亮字段
                    String highLightText = null;
                    HighlightField highlightField = hit.getHighlightFields().get("companyName");
                    if (highlightField != null && highlightField.getFragments() != null && highlightField.getFragments().length > 0) {
                        highLightText = highlightField.getFragments()[0].toString();
                    }
                    BasicInfoHighLight info = JSON.parseObject(hit.getSourceAsString(), BasicInfoHighLight.class);
                    info.setHighLightText(highLightText);
                    //将查询结果放入集合
                    data.add(info);
                }
                return new AggregatedPageImpl(data, pageable, response.getHits().getTotalHits());
            }
        });
    }

    @Override
    public Page<BasicInfo> queryByCreditCode(String creditCode, int pageNo, int pageSize) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("creditCode", creditCode))
                .withPageable(PageRequest.of(pageNo - 1, pageSize))
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .build();
        return basicRepository.search(searchQuery);
    }

    @Override
    public Page<BasicInfoHighLight> queryHighLightByCreditCode(String creditCode, int pageNo, int pageSize) {
        //创建查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //设置索引
        queryBuilder.withIndices(Constants.BASIC_INDEX).withTypes(Constants.BASIC_TYPE);
        //查询关键字
        queryBuilder.withQuery(QueryBuilders.matchQuery("creditCode", creditCode));
        //设置高亮
        queryBuilder.withHighlightFields(new HighlightBuilder.Field("creditCode").preTags("<font color='red'>").postTags("</font>"));
        //设置分页
        queryBuilder.withPageable(PageRequest.of(pageNo - 1, pageSize));
        //设置排序
        queryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        //分页查询
        return elasticTemplate.queryForPage(queryBuilder.build(), BasicInfoHighLight.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                //从response中获取结果
                SearchHits hits = response.getHits();
                //定义一个集合
                List<BasicInfoHighLight> data = new ArrayList<>();
                //获取命中文本结果
                for (SearchHit hit : hits) {
                    //获取高亮字段
                    String highLightText = null;
                    HighlightField highlightField = hit.getHighlightFields().get("creditCode");
                    if (highlightField != null && highlightField.getFragments() != null && highlightField.getFragments().length > 0) {
                        highLightText = highlightField.getFragments()[0].toString();
                    }
                    BasicInfoHighLight info = JSON.parseObject(hit.getSourceAsString(), BasicInfoHighLight.class);
                    info.setHighLightText(highLightText);
                    //将查询结果放入集合
                    data.add(info);
                }
                return new AggregatedPageImpl(data, pageable, response.getHits().getTotalHits());
            }
        });
    }
}
