package com.biz.credit.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.domain.ScoreCard;
import com.biz.credit.service.IScoreCardService;
import com.biz.credit.vo.DNodeConfigVO;
import com.biz.credit.vo.ScoreCardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 评分卡管理
 */
@RestController
@RequestMapping("/scoreCard")
public class ScoreCardController {

    @Autowired
    private IScoreCardService scoreCardService;

    @PostMapping
    public RespEntity saveScoreCard(@RequestBody ScoreCardVO scoreCardVO,HttpSession session){
        String userId = (String)session.getAttribute( "userId");
        String apiCode = (String)session.getAttribute("apiCode");
        scoreCardVO.setApiCode(apiCode);
        scoreCardVO.setUserId(userId);
        return scoreCardService.saveScoreCard(scoreCardVO);
    }

    @GetMapping("/{scoreCardId}")
    public RespEntity getScoreCard(@PathVariable("scoreCardId") Long scoreCardId) {
        ScoreCardVO scoreCard = scoreCardService.getScoreCardById(scoreCardId);
        return RespEntity.success().setData(scoreCard);
    }

    @PostMapping("/publish")
    public RespEntity publishScoreCard(@RequestParam(name = "scoreCardId") Long scoreCardId,
                                       @RequestParam(name = "publish") Integer publish){
        if (publish == null || (publish != 0 && publish != 1)){
            return RespEntity.error().setMsg("发布状态不合法");
        }
        return scoreCardService.publishScoreCard(scoreCardId, publish);
    }

    @GetMapping("/maxVersion")
    public RespEntity getMaxVersion(ScoreCardVO scoreCardVO){
        String maxVersion = scoreCardService.getMaxVersionByCardName(scoreCardVO);
        return RespEntity.success().setData(maxVersion);
    }

    @GetMapping("/existCardName")
    public RespEntity existCardName(ScoreCardVO scoreCardVO){
        boolean exist = scoreCardService.existCardName(scoreCardVO);
        return RespEntity.success().setData(exist);
    }

    @GetMapping("/versionList")
    public RespEntity getVersionListByCardName(ScoreCardVO scoreCardVO){
        List<ScoreCardVO> versionList = scoreCardService.getVersionListByCardName(scoreCardVO);
        return RespEntity.success().setData(versionList);
    }


    @GetMapping("/allMaxVersionList/{projectId}")
    public RespEntity getAllMaxVersionList(@PathVariable("projectId") Long projectId, HttpSession session){
        String apiCode = (String)session.getAttribute("apiCode");
        List<ScoreCard> allMaxVersionList = scoreCardService.getAllMaxVersionList(projectId, apiCode);
        return RespEntity.success().setData(allMaxVersionList);
    }

    @GetMapping("/queryScoreCardConfig")
    public RespEntity queryScoreCardConfig(@RequestParam(value = "nodeId",required = false )Long nodeId,HttpSession session){
        String apiCode=session.getAttribute("apiCode").toString();
        DNodeConfigVO nodeConfigVO=new DNodeConfigVO();
        nodeConfigVO.setApiCode(apiCode);
        nodeConfigVO.setNodeId(nodeId);
        List<JSONObject> list = scoreCardService.queryScoreCardConfig(nodeConfigVO);
        return RespEntity.success().setData(list);
    }

    @DeleteMapping("/{scoreCardId}")
    public RespEntity deleteScoreCard(@PathVariable("scoreCardId")Long scoreCardId){
        return scoreCardService.deleteScoreCardById(scoreCardId);
    }
}
