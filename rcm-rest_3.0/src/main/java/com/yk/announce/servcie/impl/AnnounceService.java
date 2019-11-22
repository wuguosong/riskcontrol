package com.yk.announce.servcie.impl;

import com.alibaba.fastjson.JSONObject;
import com.yk.announce.dao.IAnnounceMapper;
import com.yk.announce.entity.Announce;
import com.yk.announce.servcie.IAnnounceService;
import common.PageAssistant;
import fnd.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.DateUtil;
import util.UserUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lipan92
 * @version 1.0
 * @calssName AnnounceService
 * @description 公告
 * @date 2019/11/21 0021 14:31
 **/
@Service
@Transactional
public class AnnounceService implements IAnnounceService {
    @Resource
    private IAnnounceMapper announceMapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Announce> findList(Map<String, Object> params) {
        List<Announce> list = announceMapper.findList(params);
        return list;
    }

    @Override
    public Announce findOne(Long id) {
        Announce announce = announceMapper.findOne(id);
        return announce;
    }

    @Override
    public void delete(Long id) {
        int result = announceMapper.delete(id);
        announceMapper.updateFile(String.valueOf(id));
        System.out.println(result);
        logger.info("###-删除，返回结果：" + result);
    }

    @Override
    public Announce save(Announce announce) {
        int result = announceMapper.save(announce);
        logger.info("###-新增，返回结果：" + result);
        return announce;
    }

    @Override
    public Announce update(Announce announce) {
        int result = announceMapper.update(announce);
        logger.info("###-更新，返回结果：" + result);
        return announce;
    }

    @Override
    public void pageList(PageAssistant pageAssistant) {
        Map<String, Object> queryParams = new HashMap();
        queryParams.put("page", pageAssistant);
        if (pageAssistant.getParamMap() != null) {
            queryParams.putAll(pageAssistant.getParamMap());
        }
        List<JSONObject> jsonObjects = announceMapper.pageList(queryParams);
        pageAssistant.setList(jsonObjects);
    }

    @Override
    public Announce initAnnounce(Announce announce) {
        UserDto user = UserUtil.getCurrentUser();
        if (announce == null) {
            announce = new Announce();
            announce.setCreateTime(DateUtil.getCurrentDate());
            announce.setUpdateTime(DateUtil.getCurrentDate());
            announce.setCreateBy(user.getUuid());
            announce.setUpdateBy(user.getUuid());
            announce.setCreateName(user.getName());
            announce.setUpdateName(user.getName());
            announce.setStatus("0");
        } else {
            if (announce.getAnnounceId() == null) {// 保存
                announce.setCreateBy(user.getUuid());
                announce.setCreateTime(DateUtil.getCurrentDate());
                announce.setUpdateBy(user.getUuid());
                announce.setUpdateTime(DateUtil.getCurrentDate());
                announce.setStatus("0");
            } else {// 更新
                announce.setUpdateBy(user.getUuid());
                announce.setUpdateTime(DateUtil.getCurrentDate());
            }
        }
        return announce;
    }
}
