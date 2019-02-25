package com.yk.process.service;

import com.yk.process.entity.FlowConfig;
import com.yk.process.entity.NodeConfig;
import com.yk.process.entity.TaskConfig;
import org.activiti.bpmn.model.BpmnModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程信息业务接口
 * Created by LiPan on 2019/2/13.
 */
public interface IProcessService {
    /**
     * 获取流程节点信息
     *
     * @param processKey  流程定义Key
     * @param businessKey 业务Key
     * @return List<HashMap<String, Object>>
     */
    List<HashMap<String, Object>> listProcessNode(String processKey, String businessKey);

    /**
     * 获取流程节点信息
     *
     * @param processKey  流程定义Key
     * @param businessKey 业务Key
     * @return List<HashMap<String, Object>>
     */
    List<HashMap<String, Object>> listProcessNodeDetail(String processKey, String businessKey);

    /**
     * 获取流程配置信息（包含网关等节点）
     *
     * @param processKey  流程Key
     * @param businessKey 业务Key
     * @return List<HashMap<String, Object>>
     */
    List<HashMap<String, Object>> listProcessConfiguration(String processKey, String businessKey);

    /**
     * 通过流程Key和业务Key获取流程定义部署相关信息
     *
     * @param processKey  流程Key
     * @param businessKey 业务Key
     * @return List<HashMap<String, Object>>
     */
    List<HashMap<String, Object>> listProcessDefinition(String processKey, String businessKey);

    /**
     * 通过流程Key和业务Key获取最新版本的流程定义部署相关信息
     *
     * @param processKey  流程Key
     * @param businessKey 业务Key
     * @return List<HashMap<String, Object>>
     */
    List<HashMap<String, Object>> listLastProcessDefinition(String processKey, String businessKey);

    /**
     * 获取流程XML图并将其转换为对象
     *
     * @return BpmnModel
     */
    BpmnModel getBpmnModel(String processDefinitionId);


    /**
     * 生成节点配置信息
     * @param processKey  流程Key
     * @param businessKey 业务Key
     * @return NodeConfig
     */
    NodeConfig createNodeConfig(String processKey, String businessKey);

    /**
     * 渲染配置信息
     * @param nodeConfig 渲染前的配置
     * @return NodeConfig
     */
    NodeConfig renderNodeConfig(NodeConfig nodeConfig);
}
