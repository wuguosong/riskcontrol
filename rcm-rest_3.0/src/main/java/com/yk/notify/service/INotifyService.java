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
     * @param business_module 业务模块
     * @param business_id 业务id
     * @return List<Notify> 知会列表
     */
    List<Notify> list(String business_module, String business_id);

    /**
     * 根据知会id查询知会信息
     *
     * @param notify_id 知会id
     * @return 知会信息
     */
    Notify get(String notify_id);

    /**
     * 更新知会信息
     *
     * @param notify 知会信息
     */
    void update(Notify notify);

    /**
     * 创建知会信息
     *
     * @param notify 知会信息
     */
    void save(Notify notify);

    /**
     * 删除知会信息
     *
     * @param notify_id 知会
     */
    void delete(String notify_id);

    /**
     * 保存知会信息
     *
     * @param business_module 业务模块
     * @param business_id 业务id
     * @return List<Notify> 知会列表
     */
    List<Notify> save(String business_module, String business_id, String notifies_user);

    /**
     * 同步到统一流程平台
     *
     * @param business_module 业务模块
     * @param business_id 业务id
     * @param isTodo 代办同步
     * @param isRead 待阅同步
     * @return List<Notify> 知会列表
     */
    List<Notify> sendToPortal(String business_module, String business_id, boolean isTodo, boolean isRead);

    /**
     * 同步到统一消息平台
     *
     * @param business_module 业务模块
     * @param business_id 业务id
     * @return List<Notify>
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
     * @param page 分页
     */
    void queryNotifyInfoPage(PageAssistant page);

    /**
     * 删除只会人
     * @param business_module 业务模块
     * @param business_id 业务id
     * @param notify_user 知会人
     * @return List<Notify> 知会列表
     */
    List<Notify> delete(String business_module, String business_id, String notify_user);
}