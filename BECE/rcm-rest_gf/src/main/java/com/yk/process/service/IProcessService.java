package com.yk.process.service;

import com.yk.process.entity.NodeConfig;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;

import java.util.Collection;
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
     *
     * @param processKey  流程Key
     * @param businessKey 业务Key
     * @return NodeConfig
     */
    NodeConfig createNodeConfig(String processKey, String businessKey);

    /**
     * 渲染配置信息
     *
     * @param nodeConfig 渲染前的配置
     * @return NodeConfig
     */
    NodeConfig renderNodeConfig(NodeConfig nodeConfig);

    /**
     * 递归流程图的所有节点元素
     * @param flowElements
     * @return
     */
    Collection<FlowElement> recursionFlowElements(Collection<FlowElement> flowElements);

    /**
     * List转Map
     * @param flowElements
     * @return
     */
    Map<String, FlowElement> listToMap(Collection<FlowElement> flowElements);

    /**
     * 拆分模型元素为List
     * @param flowElements
     * @return
     */
    Map<Class, List<FlowElement>> demergeFlowElements(Collection<FlowElement> flowElements);

    /**
     * 拆分模型元素为Map
     * @param flowElements
     * @return
     */
    Map<Class, Map<String, FlowElement>> demergeFlowElementsMap(Collection<FlowElement> flowElements);

    /**
     * 获取到达下一个节点需要经过的线条元素
     * @param procDefId
     * @param start
     * @param end
     * @return
     */
    List<FlowElement> getNextTaskFlowElement(String procDefId, String start, String end);

    /**
     * 获取到达下一个节点需要经过的线条元素中的变量
     * @param procDefId
     * @param start
     * @param end
     * @return
     */
    Map<String,Object> getNextTaskFlowElementVariable(String procDefId, String start, String end);
}
