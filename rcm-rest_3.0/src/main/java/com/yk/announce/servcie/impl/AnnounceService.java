package com.yk.announce.servcie.impl;

import com.yk.announce.dao.IAnnounceMapper;
import com.yk.announce.entity.Announce;
import com.yk.announce.servcie.IAnnounceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
    public List<Announce> findList() {
        List<Announce> list = announceMapper.findList();
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
}
