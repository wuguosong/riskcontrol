package com.yk.notify.dao;

import com.alibaba.fastjson.JSONObject;
import com.yk.common.BaseMapper;
import com.yk.notify.entity.Notify;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by LiPan on 2019/3/27.
 */
@Repository
public interface INotifyMapper extends BaseMapper {
    /**
     * 查询知会列表
     *
     * @param business_module
     * @param business_id
     * @return
     */
    List<Notify> selectNotifies(@Param("business_module") String business_module, @Param("business_id") String business_id);

    /**
     * 根据知会id查询知会信息
     *
     * @param notify_id
     * @return
     */
    Notify selectNotifyById(@Param("notify_id") String notify_id);

    /**
     * 更新知会信息
     *
     * @param notify
     */
    void modifyNotify(@Param("notify") Notify notify);

    /**
     * 创建知会信息
     *
     * @param notify
     */
    void insertNotify(@Param("notify") Notify notify);

    /**
     * 删除知会信息
     *
     * @param notify_id
     */
    void removeNotify(@Param("notify_id") String notify_id);

    /**
     * 删除知会信息(多参数)
     *
     * @param business_module
     * @param business_id
     */
    void removeNotifyMultipleParameters(@Param("business_module") String business_module, @Param("business_id") String business_id);

    /**
     * 查询只会人的待阅/已阅信息
     *
     * @param curUserUuid
     * @return
     */
    List<JSONObject> selectNotifyInfo(@Param("curUserUuid") String curUserUuid);

    /**
     * 查询只会人的待阅/已阅信息
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> selectNotifyInfoPage(Map<String, Object> params);

    /**
     * 删除只会人
     *
     * @param business_module
     * @param business_id
     * @param notify_user
     */
    void removeNotifyByJson(@Param("business_module") String business_module, @Param("business_id") String business_id, @Param("notify_user") String notify_user, @Param("notify_created") String notify_created);
}
