package com.yk.notify.service.impl;

import com.yk.exception.BusinessException;
import com.yk.notify.dao.INotifyMapper;
import com.yk.notify.entity.Notify;
import com.yk.notify.service.INotifyService;
import com.yk.power.dao.IUserMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.UserUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiPan on 2019/3/27.
 */
@Service
@Transactional
public class NotifyService implements INotifyService {
    @Resource
    private INotifyMapper notifyMapper;
    @Resource
    private IUserMapper userMapper;
    @Resource
    private Notify notify;

    @Override
    public List<Notify> list(String business_module, String business_id) {
        return notifyMapper.selectNotifies(business_module, business_id);
    }

    @Override
    public Notify get(String notify_id) {
        if (StringUtils.isBlank(notify_id)) {
            throw new BusinessException("NOTIFY_ID NOT NULL!");
        }
        return notifyMapper.selectNotifyById(notify_id);
    }

    @Override
    public void update(Notify notify) {
        notifyMapper.modifyNotify(notify);
    }

    @Override
    public void save(Notify notify) {
        notifyMapper.insertNotify(notify);
    }

    @Override
    public void delete(String notify_id) {
        if (StringUtils.isBlank(notify_id)) {
            throw new BusinessException("NOTIFY_ID NOT NULL!");
        }
        notifyMapper.removeNotify(notify_id);
    }

    @Override
    public List<Notify> save(String business_module, String business_id, String notifies_user) {
        if (StringUtils.isBlank(notifies_user)){
            throw new BusinessException("NOTIFY_USER NOT NULL!");
        }
        String[] notifiesUser = notifies_user.split(",");
        if(ArrayUtils.isNotEmpty(notifiesUser)){
            Map<String, Object> params = new HashMap<String, Object>();
            String notifyCreated = UserUtil.getCurrentUserUuid();
            String notifyCreatedName = UserUtil.getCurrentUserName();
            for(String notifyUser : notifiesUser){
                params.put("UUID", notifyUser);
                Map<String, Object> user = userMapper.selectAUser(params);
                if(user != null){
                    notify.setNotifyUser(notifyUser);
                    notify.setNotifyUserName(String.valueOf(user.get("NAME")));
                    notify.setNotifyStatus("0");
                    notify.setNotifyCreated(notifyCreated);
                    notify.setNotifyCreatedName(notifyCreatedName);
                    notify.setBusinessModule(business_module);
                    notify.setBusinessId(business_id);
                    /**
                     * TODO 以下两项为备用
                     */
                    notify.setNotifyComments(null);
                    notify.setAssociateId(null);
                    this.save(notify);
                }
            }
            return notifyMapper.selectNotifies(business_module, business_id);
        }
        return new ArrayList<Notify>();
    }
}
