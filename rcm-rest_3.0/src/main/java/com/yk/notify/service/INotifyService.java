package com.yk.notify.service;

import com.yk.notify.entity.Notify;

import java.util.List;

/**
 * Created by LiPan on 2019/3/27.
 */
public interface INotifyService {
    /**
     * 查询知会列表
     * @param business_module
     * @param business_id
     * @return List<Notify>
     */
    List<Notify> list(String business_module, String business_id);

    /**
     * 根据知会id查询知会信息
     * @param notify_id
     * @return
     */
    Notify get(String notify_id);

    /**
     * 更新知会信息
     * @param notify
     */
    void update(Notify notify);

    /**
     * 创建知会信息
     * @param notify
     */
    void save(Notify notify);

    /**
     * 删除知会信息
     * @param notify_id
     */
    void delete(String notify_id);

    /**
     * 保存知会信息
     * @param business_module
     * @param business_id
     * @return
     */
    List<Notify> save(String business_module, String business_id, String notifies_user);
}
