package com.yk.notify.service;

import com.alibaba.fastjson.JSONObject;
import com.yk.notify.entity.Notify;
import common.PageAssistant;

import java.util.List;

/**
 * Created by LiPan on 2019/3/27.
 */
public interface INotifyService {
    /**
     * 查询知会列表
     *
     * @param business_module
     * @param business_id
     * @return List<Notify>
     */
    List<Notify> list(String business_module, String business_id);

    /**
     * 根据知会id查询知会信息
     *
     * @param notify_id
     * @return
     */
    Notify get(String notify_id);

    /**
     * 更新知会信息
     *
     * @param notify
     */
    void update(Notify notify);

    /**
     * 创建知会信息
     *
     * @param notify
     */
    void save(Notify notify);

    /**
     * 删除知会信息
     *
     * @param notify_id
     */
    void delete(String notify_id);

    /**
     * 保存知会信息
     *
     * @param business_module
     * @param business_id
     * @return
     */
    List<Notify> save(String business_module, String business_id, String notifies_user);

    /**
     * 同步到统一流程平台
     *
     * @param business_module
     * @param business_id
     * @param isTodo
     * @param isRead
     * @return
     */
    List<Notify> sendToPortal(String business_module, String business_id, boolean isTodo, boolean isRead);

    /**
     * 同步到统一消息平台
     *
     * @param business_module
     * @param business_id
     * @return
     */
    List<Notify> sendToMessage(String business_module, String business_id);

    /**
     * 查询只会人的待阅和已阅
     *
     * @return
     */
    JSONObject queryNotifyInfo();

    /**
     * 查询只会人的待阅和已阅
     *
     * @param page
     */
    void queryNotifyInfoPage(PageAssistant page);
}