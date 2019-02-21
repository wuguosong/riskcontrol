package com.yk.process.dao;

import com.yk.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * 流程信息持久层
 * Created by LiPan on 2019/2/13.
 */
@Repository
public interface IProcessMapper extends BaseMapper {
    /**
     * 通过流程Key和业务Key获取流程进度信息列表
     *
     * @param processKey  流程Key
     * @param businessKey 业务Key
     * @return List<HashMap<String, Object>>
     */
    List<HashMap<String, Object>> selectProcessNodeList(@Param("processKey") String processKey, @Param("businessKey") String businessKey);

    /**
     * 通过流程Key和业务Key获取流程进度信息列表
     *
     * @param processKey  流程Key
     * @param businessKey 业务Key
     * @return List<HashMap<String, Object>>
     */
    List<HashMap<String, Object>> selectProcessNodeListDetail(@Param("processKey") String processKey, @Param("businessKey") String businessKey);

    /**
     * 通过流程Key和业务Key获取流程定义部署相关信息
     *
     * @param processKey  流程Key
     * @param businessKey 业务Key
     * @param last        是否最新版本信息
     * @return List<HashMap<String, Object>>
     */
    List<HashMap<String, Object>> selectProcessDefinitionList(@Param("processKey") String processKey, @Param("businessKey") String businessKey, @Param("last") boolean last);

    /**
     * 获取流程配置信息（包含网关等节点）
     *
     * @param processKey  流程Key
     * @param businessKey 业务Key
     * @return List<HashMap<String, Object>>
     */
    List<HashMap<String, Object>> selectProcessConfiguration(@Param("processKey") String processKey, @Param("businessKey") String businessKey);
}
