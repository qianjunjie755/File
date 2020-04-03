package com.biz.credit.plugins;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.biz.decision.common.Constants;
import com.biz.decision.enums.ETemplateType;
import com.biz.decision.report.converts.ITemplateConvert;
import com.biz.decision.report.engine.MyTemplates;
import com.biz.decision.report.entity.ApiAttr;
import com.biz.decision.report.entity.Attribute;
import com.biz.decision.report.entity.Template;
import com.biz.decision.report.entity.TemplateParam;
import com.biz.decision.utils.DecideUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateServiceImpl implements ITemplateConvert {

    @Override
    public void afterPropertiesSet() {
        MyTemplates.register(ETemplateType.SPECIAL_LIST, this);
    }

    @Override
    public JSONObject convertData(TemplateParam param) {
        if (param == null) {
            throw new RuntimeException("模板参数不能为空!!");
        }

        Template template = param.getTemplate();
        if (template == null) {
            throw new RuntimeException("模板不能为空!!");
        }

        ETemplateType type = template.getTemplateType();
        if (type == null) {
            throw new RuntimeException("模板类型不能为空!!");
        }

        //与易派客平台交易表现
        if (template.getTemplateId() == 61001) {
            return convert(param.getAttrs(), param.getJsonObject());
        }
        return null;
    }

    private JSONObject convert(List<Attribute> attrs, JSONObject jsonObject) {
        if (attrs == null) {
            return null;
        }
        JSONArray head = new JSONArray();
        head.add("统计分类");
        head.add("统计总量");
        JSONArray body = new JSONArray();
        for (Attribute attr : attrs) {
            ApiAttr apiAttr = attr.getApiAttr();
            Object value = JSONPath.eval(jsonObject, apiAttr.getJsonPath());
            JSONArray row = new JSONArray();
            row.add(attr.getName());
            row.add(DecideUtils.dataConvert(value, apiAttr.getConvert()));
            body.add(row);
        }
        JSONObject value = new JSONObject();
        value.put(Constants.HEAD, head);
        value.put(Constants.BODY, body);
        //
        JSONObject result = new JSONObject();
        result.put(Constants.TYPE, ETemplateType.LIST);
        result.put(Constants.VALUE, value);
        return result;
    }
}
