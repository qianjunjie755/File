package com.biz.warning.controller;

import com.biz.warning.domain.Dict;
import com.biz.warning.service.IDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private IDictService dictService;

    @RequestMapping("/groupCode/{groupCode}")
    public List<Dict> getListByGroupCode(@PathVariable("groupCode")String groupCode){
        return dictService.queryByGroupCode(groupCode);
    }
}
