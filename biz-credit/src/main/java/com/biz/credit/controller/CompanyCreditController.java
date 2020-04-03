package com.biz.credit.controller;

import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.ICompanyCreditService;
import com.biz.credit.utils.PageData;
import com.biz.credit.vo.CompanyCreditListVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "企业评分结果API")
@RestController
@RequestMapping("/companyCredit")
public class CompanyCreditController {

    @Autowired
    private ICompanyCreditService companyCreditService;

    @ApiOperation(value = "查询企业评分结果列表", notes = "根据companyName查询企业评分结果列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyName", value = "企业名称"),
            @ApiImplicitParam(name = "pageNo", value = "页-Integer", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量-Integer", required = true)
    })
    @GetMapping("/list")
    public RespEntity<PageData<CompanyCreditListVO>> getScoreLevel(
            @RequestParam(name = "companyName", required = false) String companyName,
            @RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        Page<CompanyCreditListVO> page = PageHelper.startPage(pageNo, pageSize);
        companyCreditService.findByCompanyName(companyName);
        return RespEntity.success().setData(new PageData<>(page.getTotal(), page.getResult()));
    }


}
