package com.biz.credit.plugins;


import com.biz.credit.dao.CompanyCreditDAO;
import com.biz.credit.domain.CompanyCredit;
import com.biz.credit.service.ICompanyCreditService;
import com.biz.credit.vo.CompanyCreditListVO;
import com.biz.credit.vo.CompanyCreditModelVO;
import com.biz.decision.common.Result;
import com.biz.decision.entity.Flow;
import com.biz.decision.entity.Input;
import com.biz.decision.entity.Node;
import com.biz.decision.entity.Task;
import com.biz.decision.enums.EModel;
import com.biz.decision.model.Model;
import com.biz.decision.plugins.ICustomPlugin;
import com.biz.decision.report.engine.ReportEngine;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompanyCreditServiceImpl implements ICustomPlugin, ICompanyCreditService {

    @Autowired
    private CompanyCreditDAO companyCreditDAO;

    @Autowired
    private ReportEngine reportEngine;

    @Override
    public void execute(Task task, Input input, Flow flow) throws Exception {
        Result result = input.getResult();
        if (result == null || !result.ok()){
            return;
        }

        List<CompanyCredit> list = new ArrayList<>();
        TreeSet<Node> nodes = flow.getNodes();
        for (Node node : nodes){
            if (node.getResult() == null) {
                continue;
            }
            List<Model> models = node.getModels();
            if (models != null) {
                for (Model model : models) {
                    Integer modelType = null;
                    //信用评分模型/评分卡/反欺诈模型
                    if (model.getType() == EModel.D_SCORE_MODEL ||
                            model.getType() == EModel.D_SCORE_CARD ||
                            model.getType() == EModel.D_ANTI_FRAUD_SCORE) {
                        modelType = model.getType().value();
                    }
                    if (modelType == null){
                        continue;
                    }
                    CompanyCredit companyCredit = new CompanyCredit();
                    companyCredit.setInputId(String.valueOf(input.getInputId()));
                    companyCredit.setModelType(modelType);
                    companyCredit.setModelCode(model.getCode());
                    companyCredit.setCreditValue(model.getValue());
                    companyCredit.setCreditLevel(reportEngine.getScoreLevel(flow.getApiCode(), model));
                    list.add(companyCredit);
                }
            }
        }
        companyCreditDAO.insertBatch(list);




    }

    @Override
    public List<CompanyCreditListVO> findByCompanyName(String companyName) {
        List<CompanyCreditListVO> list = companyCreditDAO.findByCompanyName(companyName);
        if (!CollectionUtils.isEmpty(list)){

            //处理里面的字段
            Map<String, CompanyCreditModelVO> map = new HashMap<>();
            list.forEach(e -> {
                //根据modelType+modelCode查找modelName
                String key = new StringBuilder()
                        .append(e.getModelCode())
                        .append(".")
                        .append(e.getModelType()).toString();
                if (map.containsKey(key)){
                    //在map中有缓存，直接设置modelName
                    e.setModelName(map.get(key).getModelName());
                }else {
                    //在map中没有缓存，到数据库查询
                    CompanyCreditModelVO model =
                            findModelByTypeAndCode(e.getModelCode(), Integer.valueOf(e.getModelType()));
                    if (model != null){
                        //查到数据后，先设置modelName，在缓存到map中
                        e.setModelName(model.getModelName());
                        map.put(key, model);
                    }
                }
                //modelType替换为中文
                e.setModelType(EModel.valueOf(e.getModelType()).stringValue());
            });

        }
        return list;
    }

    @Override
    public CompanyCreditModelVO findModelByTypeAndCode(Integer modelCode, Integer modelType) {
        List<CompanyCreditModelVO> models =
                companyCreditDAO.findModelNameByModelCode(modelCode, modelType);
        if (CollectionUtils.isEmpty(models)){
            return null;
        }
        return models.get(0);
    }
}
