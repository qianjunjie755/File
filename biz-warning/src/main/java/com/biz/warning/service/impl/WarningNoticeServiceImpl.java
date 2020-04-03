package com.biz.warning.service.impl;

import com.biz.service.IAuthService;
import com.biz.warning.dao.WarningNoticeDAO;
import com.biz.warning.service.IWarningNoticeService;
import com.biz.warning.vo.WarningNoticeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class WarningNoticeServiceImpl implements IWarningNoticeService {

    @Autowired
    private WarningNoticeDAO warningNoticeDAO;
    @Autowired
    private IAuthService authService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<WarningNoticeVO> findList(WarningNoticeVO warningNoticeVO) {
        List<WarningNoticeVO> list = null;
        if(0==warningNoticeVO.getRead()||1==warningNoticeVO.getRead()){
            List<WarningNoticeVO> listTemp = 0==warningNoticeVO.getRead()?warningNoticeDAO.findWarnVariableResultListUnRead(warningNoticeVO):warningNoticeDAO.findWarnVariableResultListRead(warningNoticeVO);
            if(!CollectionUtils.isEmpty(listTemp)){
                List<Long> wIds = new ArrayList<>();
                listTemp.forEach(w->{
                    wIds.add(w.getWarnResultVariableId());
                });
                list = warningNoticeDAO.findListByWarnResultVariableIdList(wIds);
                if(!CollectionUtils.isEmpty(list)){
                    Collections.reverse(list);
                }
                list.forEach(w->{
                    w.setUserId(warningNoticeVO.getUserId());
                    w.setRead(warningNoticeVO.getRead());
                });
                listTemp.clear();
                listTemp.addAll(list);
            }else{
                list = new ArrayList<>();
            }
        }else{
            list = CollectionUtils.isEmpty(warningNoticeVO.getUserIds())?new ArrayList<>() :warningNoticeDAO.findWarnVariableResultList(warningNoticeVO);
            list.forEach(wnvo->{
                wnvo.setUserId(warningNoticeVO.getUserId());
                long count = warningNoticeDAO.findUnReadWarningNoticeCount(wnvo);
                wnvo.setRead(count>0?0:1);
            });
        }
        return list;
    }

    @Override
    public int readWarnResultVariable(WarningNoticeVO warningNoticeVO) {
        int count=0;
        if(warningNoticeDAO.findWarningNoticeCount(warningNoticeVO)==0){
            count += warningNoticeDAO.addWarningNotice(warningNoticeVO);
        }else{
            count += warningNoticeDAO.updateWarningNotice(warningNoticeVO);
        }
        count += warningNoticeDAO.deleteWaringNotceUnread(warningNoticeVO);
        return count ;
    }

    @Override
    public long findUnReadWarningNoticeCount(WarningNoticeVO warningNoticeVO) {
        long unReadCount =  warningNoticeDAO.findUnReadWarningNoticeCount(warningNoticeVO);
        //long count = warningNoticeDAO.findAllWarningNoticeCount(warningNoticeVO);
        return unReadCount;
    }

    @Override
    public WarningNoticeVO findSingleWarningNotice(WarningNoticeVO warningNoticeVO) {
        return warningNoticeDAO.findSingleWarningNotice(warningNoticeVO);
    }

    @Override
    public WarningNoticeVO findSingleWarningNoticeUnRead(WarningNoticeVO warningNoticeVO) {
        WarningNoticeVO wvo = warningNoticeDAO.findSingleWarningNoticeUnRead(warningNoticeVO);
        WarningNoticeVO ret = warningNoticeDAO.findWarnVariableResultById(wvo);
        return ret;
    }

    @Async
    @Override
    public void initWarningNotices(Long startId,Long endId) throws Exception {
        String publicKey = stringRedisTemplate.opsForValue().get("biz_credit:publicKey");
        long start = startId;
        long end = start;
        int size = 10000;
        Map<Integer,List<Integer>> userIdMap = new HashMap<>();
        while(end<endId){
            end = start+size>endId?endId:start+size;
            List<WarningNoticeVO> warningNoticeVOList = warningNoticeDAO.findListFromStartToEnd(start,end);
            log.info("wvo_init start->size:{}",CollectionUtils.isEmpty(warningNoticeVOList)?0:warningNoticeVOList.size());
            warningNoticeVOList.forEach(wvo -> {
                if(CollectionUtils.isEmpty( userIdMap.get(wvo.getUserId()))){
                    List<Integer> userIdList = authService.getRelatedUsers(publicKey,wvo.getApiCode(),wvo.getUserId());
                    userIdMap.put(wvo.getUserId(),userIdList);
                }
                List<Integer> userIdList = userIdMap.get(wvo.getUserId());
                if(!CollectionUtils.isEmpty(userIdList)){
                    userIdList.forEach(userId->{
                        wvo.setUserId(userId);
                        readWarnResultVariable(wvo);
                        log.info("wvo_init->id:{} and apiCode:{} and userId:{}",wvo.getWarnResultVariableId(),wvo.getApiCode(),wvo.getUserId());
                    });
                }
            });
            if(!CollectionUtils.isEmpty(warningNoticeVOList)){
                end = warningNoticeVOList.get(warningNoticeVOList.size()-1).getWarnResultVariableId();
            }
            start = end;
        }
    }

}
