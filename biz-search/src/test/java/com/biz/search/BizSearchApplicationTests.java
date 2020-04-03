package com.biz.search;

import com.biz.search.entity.BasicInfo;
import com.biz.search.entity.BasicInfoHighLight;
import com.biz.search.service.IBasicInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class BizSearchApplicationTests {

	@Autowired
	private IBasicInfoService service;

	@Autowired
	private ElasticsearchTemplate elasticTemplate;

	@Test
	public void delIndex() {
		elasticTemplate.deleteIndex(BasicInfo.class);
	}

	@Test
	public void contextLoads() {
		service.loadBasicInfo(400);
		log.info("企业工商基本信息数据已全部加载完成!");
	}

	@Test
	public void queryByCompanyName() {
		Page<BasicInfo> page = service.queryByCompanyName("广州", 1, 10);
		System.out.println(page.getTotalElements());
		List<BasicInfo> data = page.getContent();
		if (!CollectionUtils.isEmpty(data)) {
			for (BasicInfo info : data) {
				System.out.println(info.getCompanyName());
			}
		}
		System.out.println("---------------------");
	}

	@Test
	public void queryHighLightByCompanyName() {
		Page<BasicInfoHighLight> page = service.queryHighLightByCompanyName("耐信模具科技", 1, 10);
		System.out.println(page.getTotalElements());
		List<BasicInfoHighLight> data = page.getContent();
		if (!CollectionUtils.isEmpty(data)) {
			for (BasicInfoHighLight info : data) {
				System.out.println(info.getHighLightText() + " " + info.getCompanyName());
			}
		}
		System.out.println("---------------------");
	}

	@Test
	public void queryByCreditCode() {
		Page<BasicInfo> page = service.queryByCreditCode("91310112736222474M", 1, 10);
		System.out.println(page.getTotalElements());
		List<BasicInfo> data = page.getContent();
		if (!CollectionUtils.isEmpty(data)) {
			for (BasicInfo info : data) {
				System.out.println(info.getCompanyName() + " " + info.getCreditCode());
			}
		}
		System.out.println("---------------------");
	}

	@Test
	public void queryHighLightByCreditCode() {
		Page<BasicInfoHighLight> page = service.queryHighLightByCreditCode("91310112736222474M", 1, 10);
		System.out.println(page.getTotalElements());
		List<BasicInfoHighLight> data = page.getContent();
		if (!CollectionUtils.isEmpty(data)) {
			for (BasicInfoHighLight info : data) {
				System.out.println(info.getHighLightText() + " " + info.getCompanyName() + " " + info.getCreditCode());
			}
		}
		System.out.println("---------------------");
	}
}
