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
     * @param business_module 业务模块
     * @param business_id 业务id
     * @param notify_type 知会类型
     * @return List<Notify> 知会列表
     */
    List<Notify> selectNotifies(@Param("business_module") String business_module, @Param("business_id") String business_id, @Param("notify_type")String notify_type);

    /**
     * 根据知会id查询知会信息
     *
     * @param notify_id 知会id
     * @return Notify 知会信息
     */
    Notify selectNotifyById(@Param("notify_id") String notify_id);

    /**
     * 更新知会信息
     *
     * @param notify 知会信息
     */
    void modifyNotify(@Param("notify") Notify notify);

    /**
     * 创建知会信息
     *
     * @param notify 知会信息
     */
    void insertNotify(@Param("notify") Notify notify);

    /**
     * 删除知会信息
     *
     * @param notify_id 知会id
     */
    void removeNotify(@Param("notify_id") String notify_id);

    /**
     * 删除知会信息(多参数)
     *
     * @param business_module 业务模块
     * @param business_id 业务id
     * @param notify_type 知会类型
     */
    void removeNotifyMultipleParameters(@Param("business_module") String business_module, @Param("business_id") String business_id,@Param("notify_type")String notify_type);

    /**
     * 查询知会人的待阅/已阅信息
     *
     * @param curUserUuid
     * @return
     */
    List<JSONObject> selectNotifyInfo(@Param("curUserUuid") String curUserUuid);

    /**
     * 查询知会人的待阅/已阅信息
     *
     * @param params 查询参数
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> selectNotifyInfoPage(Map<String, Object> params);

    /**
     * 删除知会人
     *
     * @param business_module 业务模块
     * @param business_id 业务id
     * @param notify_user 知会人
     * @param notify_type 知会类型
     */
    void removeNotifyByJson(@Param("business_module") String business_module, @Param("business_id") String business_id, @Param("notify_user") String notify_user, @Param("notify_created") String notify_created,@Param("notify_type")String notify_type);
}
