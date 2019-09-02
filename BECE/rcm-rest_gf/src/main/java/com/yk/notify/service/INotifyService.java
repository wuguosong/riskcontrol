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
     * @param notifyType 知会类型
     * @return List<Notify> 知会列表
     */
    List<Notify> list(String business_module, String business_id,  String notifyType);

    /**
     * 根据知会id查询知会信息
     *
     * @param notify_id 知会id
     * @return 知会信息
     */
    Notify get(String notify_id);

    /**
     * 根据知会id查询知会信息
     *
     * @param notify_id 知会id
     * @return 知会信息
     */
    JSONObject getNotifyJSONObject(String notify_id);

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
     * @param isCheck 是否进行重复性过滤
     * @return List<Notify> 知会列表
     */
    List<Notify> save(String business_module, String business_id, String notifies_user, boolean isCheck);

    /**
     * 同步到统一流程平台
     *
     * @param business_module 业务模块
     * @param business_id 业务id
     * @param isTodo 代办同步
     * @param isRead 待阅同步
     * @param notifyType 知会类型
     * @return List<Notify> 知会列表
     */
    List<Notify> sendToPortal(String business_module, String business_id, boolean isTodo, boolean isRead,  String notifyType);

    /**
     * 同步到统一消息平台
     *
     * @param business_module 业务模块
     * @param business_id 业务id
     * @param notifyType 知会类型
     * @return List<Notify>
     */
    List<Notify> sendToMessage(String business_module, String business_id, String notifyType);

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
     * @param notifyType 知会人类型
     * @return List<Notify> 知会列表
     */
    List<Notify> delete(String business_module, String business_id, String notify_user, String notifyType);

    /**
     * 保存
     * @param business_module 模块
     * @param business_id 业务id
     * @param notifies_user 知会人
     * @param notifyType 知会类型
     * @param associateId 关联主键id
     * @param notifyComments 知会意见
     * @return List<Notify>
     */
    List<Notify> save(String business_module, String business_id, String notifies_user, String notifyType, String associateId, String notifyComments, boolean isCheck);
}