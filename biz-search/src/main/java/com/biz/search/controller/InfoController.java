package com.biz.search.controller;

import com.biz.search.entity.BasicInfoHighLight;
import com.biz.search.service.IBasicInfoService;
import com.biz.search.utils.PageData;
import com.biz.search.utils.RespEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@Api(tags = "信息API")
@RestController
@RequestMapping("/info")
public class InfoController {

    @Autowired
    private IBasicInfoService service;

    @ApiOperation(value = "查询企业列表", notes = "根据企业名称或者企业统一社会信用代码查询企业工商基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "查询类型: 1-基于企业名称查询 2-基于统一社会信用代码查询", required = true),
            @ApiImplicitParam(name = "text", value = "查询文本内容", required = true),
            @ApiImplicitParam(name = "pageNo", value = "查询页号", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", required = true)})
    @GetMapping("/list")
    public RespEntity<PageData<BasicInfoHighLight>> queryCompanyList(
            @RequestParam(value = "type", defaultValue = "1") Integer type,
            @RequestParam(value = "text") String text,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<BasicInfoHighLight> page = null;
        try {
            //基于企业名称查询
            if (Objects.equals(type, 1)) {
                page = service.queryHighLightByCompanyName(text, pageNo, pageSize);
            }
            //基于统一社会信用代码查询
            else if (Objects.equals(type, 2)) {
                page = service.queryHighLightByCreditCode(text, pageNo, pageSize);
            }
            //其他情况
            else {
                return RespEntity.error().setMsg("不支持的查询类型[" + type + "]");
            }
        } catch (Exception e) {
            log.error("查询企业信息失败: " + e.getMessage(), e);
            return RespEntity.error().setMsg(e.getMessage());
        }
        if (page == null) {
            return RespEntity.noResult();
        }
        return RespEntity.success().setData(new PageData<>(page.getTotalElements(), page.getContent()));
    }
}
