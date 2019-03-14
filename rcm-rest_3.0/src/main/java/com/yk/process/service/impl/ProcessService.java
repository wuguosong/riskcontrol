package com.yk.process.service.impl;

import com.yk.process.dao.IProcessMapper;
import com.yk.process.entity.FlowConfig;
import com.yk.process.entity.NodeConfig;
import com.yk.process.entity.TaskConfig;
import com.yk.process.service.IProcessService;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.*;

/**
 * 流程信息业务接口实现
 * Created by LiPan on 2019/2/13.
 */
@Service
@Transactional
public class ProcessService implements IProcessService {
    // 常量来自于表中查询的字段别名
    private static final String ID_ = "ID_";
    private static final String DEPLOYMENT_ID_ = "DEPLOYMENT_ID_";
    private static final String ACT_ID_ = "ACT_ID_";

    @Resource
    private IProcessMapper processMapper;
    @Resource
    private RepositoryService repositoryService;

    @Override
    public List<HashMap<String, Object>> listProcessNode(String processKey, String businessKey) {
        return processMapper.selectProcessNodeList(processKey, businessKey);
    }

    @Override
    public List<HashMap<String, Object>> listProcessNodeDetail(String processKey, String businessKey) {
        return processMapper.selectProcessNodeListDetail(processKey, businessKey);
    }

    @Override
    public List<HashMap<String, Object>> listProcessDefinition(String processKey, String businessKey) {
        return processMapper.selectProcessDefinitionList(processKey, businessKey, false);
    }

    @Override
    public List<HashMap<String, Object>> listLastProcessDefinition(String processKey, String businessKey) {
        return processMapper.selectProcessDefinitionList(processKey, businessKey, true);
    }

    @Override
    public List<HashMap<String, Object>> listProcessConfiguration(String processKey, String businessKey) {
        return processMapper.selectProcessConfiguration(processKey, businessKey);
    }

    @Override
    public BpmnModel getBpmnModel(String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        InputStream inputStream = repositoryService.getProcessModel(processDefinition.getId());
        BpmnXMLConverter converter = new BpmnXMLConverter();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try {
            reader = factory.createXMLStreamReader(inputStream);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        BpmnModel bpmnModel = converter.convertToBpmnModel(reader);
        return bpmnModel;
    }

    @Override
    public NodeConfig createNodeConfig(String processKey, String businessKey) {
        List<HashMap<String, Object>> list = this.listLastProcessDefinition(processKey, businessKey);
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("没有找到[" + processKey + "," + businessKey + "]的流程模型!");
        }
        NodeConfig nodeConfig = new NodeConfig();
        HashMap<String, Object> data = list.get(0);
        BpmnModel bpmnModel = this.getBpmnModel(String.valueOf(data.get(ID_)));
        Process mainProcess = bpmnModel.getMainProcess();
        Collection<FlowElement> flowElements = this.recursionFlowElements(mainProcess.getFlowElements());
        List<TaskConfig> taskConfigs = new ArrayList<TaskConfig>();// 流程节点List
        List<FlowConfig> flowConfigs = new ArrayList<FlowConfig>();// 流程流向List
        int index = 1;
        for (FlowElement flowElement : flowElements) {// 过滤非流向对象
            if (!(flowElement instanceof SequenceFlow)) {
                TaskConfig taskConfig = new TaskConfig();
                taskConfig.setProcess(String.valueOf(data.get(ID_)));
                taskConfig.setDeployment(new Long(String.valueOf(data.get(DEPLOYMENT_ID_))));
                taskConfig.setIndex(index);
                taskConfig.setName(flowElement.getName());
                taskConfig.setId(flowElement.getId());
                taskConfig.setType(flowElement.getClass().getSimpleName());
                taskConfig.setInit(flowElement);
                taskConfigs.add(taskConfig);
                index++;
            }
        }
        Map<String, TaskConfig> taskConfigMap = this.toTaskConfigMap(taskConfigs);
        for (FlowElement flowElement : flowElements) {// 过滤流向元素对象
            if (flowElement instanceof SequenceFlow) {
                SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                FlowConfig flowConfig = new FlowConfig();
                flowConfig.setId(flowElement.getId());
                flowConfig.setName(flowElement.getName());
                flowConfig.setFrom(taskConfigMap.get(sequenceFlow.getSourceRef()));
                flowConfig.setTo(taskConfigMap.get(sequenceFlow.getTargetRef()));
                flowConfig.setInit(flowElement);
                flowConfigs.add(flowConfig);
            }
        }
        for (TaskConfig taskConfig : taskConfigs) { // 单独创建内部子流程之间的节点
            if (SubProcess.class.getSimpleName().equals(taskConfig.getType())) {
                Map<String, Collection<FlowElement>> subFlowsMap = this.getSubProcessFlowElements(mainProcess.getFlowElements(), taskConfig.getId());
                if (subFlowsMap != null) {
                    Set<Map.Entry<String, Collection<FlowElement>>> entrySet = subFlowsMap.entrySet();
                    for (Iterator<Map.Entry<String, Collection<FlowElement>>> iterator = entrySet.iterator(); iterator.hasNext(); ) {
                        Map.Entry<String, Collection<FlowElement>> entry = iterator.next();
                        String key = entry.getKey();
                        Collection<FlowElement> value = entry.getValue();
                        for (FlowElement flowElement : value) {
                            if (flowElement instanceof StartEvent) {
                                FlowConfig flowConfig = new FlowConfig();
                                flowConfig.setId(key + ":" + flowElement.getId());
                                flowConfig.setName(key + "-->" + flowElement.getId());
                                flowConfig.setFrom(taskConfigMap.get(key));
                                flowConfig.setTo(taskConfigMap.get(flowElement.getId()));
                                flowConfig.setInit(null);
                                flowConfigs.add(flowConfig);
                                System.out.println(flowConfigs.size());
                            }
                        }
                        // 构造子流程流向集合
                        Map<String, FlowConfig> flowConfigMap = this.toFlowConfigMap(flowConfigs);
                        List<FlowConfig> subFlowConfigs = new ArrayList<FlowConfig>();
                        List<TaskConfig> subTaskConfigs = new ArrayList<TaskConfig>();
                        for (FlowElement flowElement : value) {
                            if (flowElement instanceof SequenceFlow) {
                                subFlowConfigs.add(flowConfigMap.get(flowElement.getId()));
                            } else {
                                subTaskConfigs.add(taskConfigMap.get(flowElement.getId()));
                            }
                        }
                        NodeConfig subNodeConfig = new NodeConfig();
                        subNodeConfig.setProcessKey(processKey);
                        subNodeConfig.setBusinessKey(businessKey);
                        subNodeConfig.setEdges(subFlowConfigs);
                        subNodeConfig.setStates(subTaskConfigs);
                        nodeConfig.getNodes().put(key, subNodeConfig);
                    }
                }
            }
        }
        nodeConfig.setStates(taskConfigs);
        nodeConfig.setEdges(flowConfigs);
        nodeConfig.setBusinessKey(businessKey);
        nodeConfig.setProcessKey(processKey);
        // 设置主流程节点信息
        Map<String, NodeConfig> nodes = nodeConfig.getNodes();
        List<FlowConfig> mainFlowConfigs = new ArrayList<FlowConfig>();
        mainFlowConfigs.addAll(flowConfigs);
        List<TaskConfig> mainTaskConfigs = new ArrayList<TaskConfig>();
        mainTaskConfigs.addAll(taskConfigs);
        Set<Map.Entry<String, NodeConfig>> entrySet = nodes.entrySet();
        for (Iterator<Map.Entry<String, NodeConfig>> iterator = entrySet.iterator(); iterator.hasNext(); ) {
            Map.Entry<String, NodeConfig> entry = iterator.next();
            NodeConfig value = entry.getValue();
            List<FlowConfig> otherFlowConfigs = value.getEdges();
            List<TaskConfig> otherTaskConfigs = value.getStates();
            mainFlowConfigs.removeAll(otherFlowConfigs);
            mainTaskConfigs.removeAll(otherTaskConfigs);
        }
        // 主流流程节点需要移除虚拟的流向元素
        for (Iterator<FlowConfig> iterator = mainFlowConfigs.listIterator(); iterator.hasNext(); ) {
            FlowConfig mainFlowConfig = iterator.next();
            if (mainFlowConfig.getInit() == null) {
                iterator.remove();
            }
        }
        NodeConfig mainNodeConfig = new NodeConfig();
        mainNodeConfig.setStates(mainTaskConfigs);
        mainNodeConfig.setEdges(mainFlowConfigs);
        mainNodeConfig.setBusinessKey(businessKey);
        mainNodeConfig.setProcessKey(processKey);
        nodes.put(processKey, mainNodeConfig);
        System.out.println(flowConfigs.size());
        return nodeConfig;
    }

    @Override
    public NodeConfig renderNodeConfig(NodeConfig nodeConfig) {
        List<HashMap<String, Object>> processConfigurations = this.listProcessConfiguration(nodeConfig.getProcessKey(), nodeConfig.getBusinessKey());
        // 设置节点状态
        Map<String, TaskConfig> taskConfigMap = this.toTaskConfigMap(nodeConfig.getStates());
        for (HashMap<String, Object> processConfiguration : processConfigurations) {
            String id = String.valueOf(processConfiguration.get(ACT_ID_));
            if (taskConfigMap.get(id) != null) {
                taskConfigMap.get(id).setStatus(NodeConfig.ALREADY);
            }
        }
        List<TaskConfig> taskConfigs = this.toTaskConfigList(taskConfigMap);
        for (TaskConfig taskConfig : taskConfigs) {
            if (StringUtils.isBlank(taskConfig.getStatus())) {
                taskConfig.setStatus(NodeConfig.AGENCY);
            }
        }
        // 设置流向状态
        List<FlowConfig> flowConfigs = nodeConfig.getEdges();
        for (int i = 1; i < processConfigurations.size(); i++) {
            String alreadyFrom = String.valueOf(processConfigurations.get(i - 1).get(ACT_ID_));
            String alreadyTo = String.valueOf(processConfigurations.get(i).get(ACT_ID_));
            for (FlowConfig flowConfig : flowConfigs) {
                String to = flowConfig.getTo().getId();
                String from = flowConfig.getFrom().getId();
                flowConfig.setFrom(taskConfigMap.get(from));// 更新来和去节点状态信息
                flowConfig.setTo(taskConfigMap.get(to));
                if (alreadyFrom.equals(from) && alreadyTo.equals(to)) {
                    flowConfig.setStatus(NodeConfig.ALREADY);
                }
                if (flowConfig.getInit() == null && NodeConfig.ALREADY.equals(flowConfig.getFrom().getStatus()) && NodeConfig.ALREADY.equals(flowConfig.getTo().getStatus())) {
                    flowConfig.setStatus(NodeConfig.ALREADY);// 子流程与开始的连线状态设置
                }
            }
        }
        for (FlowConfig flowConfig : flowConfigs) {
            if (StringUtils.isBlank(flowConfig.getStatus())) {
                flowConfig.setStatus(NodeConfig.AGENCY);
            }
        }
        return nodeConfig;
    }

    /**
     * 流向List转流向Map
     *
     * @param flowConfigs
     * @return
     */
    private Map<String, FlowConfig> toFlowConfigMap(List<FlowConfig> flowConfigs) {
        Map<String, FlowConfig> flowConfigMap = new HashMap<String, FlowConfig>();
        for (FlowConfig flowConfig : flowConfigs) {
            flowConfigMap.put(flowConfig.getId(), flowConfig);
        }
        return flowConfigMap;
    }

    /**
     * 迭代子流程下面的节点和流向
     *
     * @param flowElements
     * @param subProcessId
     * @return
     */
    private Map<String, Collection<FlowElement>> getSubProcessFlowElements(Collection<FlowElement> flowElements, String subProcessId) {
        Map<String, Collection<FlowElement>> allSubFlowElementsMap = new LinkedHashMap<String, Collection<FlowElement>>();
        if (CollectionUtils.isNotEmpty(flowElements)) {
            for (FlowElement flowElement : flowElements) {
                if (flowElement instanceof SubProcess) {
                    SubProcess subProcess = (SubProcess) flowElement;
                    Collection<FlowElement> subFlowElements = subProcess.getFlowElements();
                    allSubFlowElementsMap.put(subProcessId, subFlowElements);
                    this.getSubProcessFlowElements(subFlowElements, flowElement.getId());
                }
            }
        }
        return allSubFlowElementsMap;
    }

    /**
     * 迭代流程元素(如果一个流程有内部流程时候)
     *
     * @param flowElements
     * @return Collection<FlowElement> 迭代结果
     */
    @Override
    public Collection<FlowElement> recursionFlowElements(Collection<FlowElement> flowElements) {
        Collection<FlowElement> allFlowElements = new ArrayList<FlowElement>();
        if (CollectionUtils.isNotEmpty(flowElements)) {
            allFlowElements.addAll(flowElements);
            for (FlowElement flowElement : flowElements) {
                if (flowElement instanceof SubProcess) {
                    SubProcess subProcess = (SubProcess) flowElement;
                    Collection<FlowElement> subFlowElements = subProcess.getFlowElements();
                    allFlowElements.addAll(subFlowElements);
                    this.recursionFlowElements(subFlowElements);
                }
            }
        }
        return allFlowElements;
    }

    /**
     * 将任务List变成任务Map
     *
     * @param taskConfigs 任务List
     * @return Map<String, TaskConfig>
     */
    private Map<String, TaskConfig> toTaskConfigMap(List<TaskConfig> taskConfigs) {
        Map<String, TaskConfig> taskConfigMap = new LinkedHashMap<String, TaskConfig>();
        for (TaskConfig taskConfig : taskConfigs) {
            taskConfigMap.put(taskConfig.getId(), taskConfig);
        }
        return taskConfigMap;
    }

    /**
     * 将任务Map变成任务List
     *
     * @param taskConfigMap 任务Map
     * @return List<TaskConfig>
     */
    private List<TaskConfig> toTaskConfigList(Map<String, TaskConfig> taskConfigMap) {
        List<TaskConfig> taskConfigs = new ArrayList<TaskConfig>();
        Set<Map.Entry<String, TaskConfig>> entrySet = taskConfigMap.entrySet();
        for (Iterator<Map.Entry<String, TaskConfig>> iterator = entrySet.iterator(); iterator.hasNext(); ) {
            Map.Entry<String, TaskConfig> entry = iterator.next();
            taskConfigs.add(entry.getValue());
        }
        return taskConfigs;
    }

    @Override
    public Map<String, FlowElement> listToMap(Collection<FlowElement> flowElements) {
        Map<String, FlowElement> map = new LinkedHashMap<String, FlowElement>();
        for (FlowElement flowElement : flowElements) {
            map.put(flowElement.getId(), flowElement);
        }
        return map;
    }

    @Override
    public Map<Class, List<FlowElement>> demergeFlowElements(Collection<FlowElement> flowElements) {
        Map<Class, List<FlowElement>> map = new LinkedHashMap<Class, List<FlowElement>>();
        List<FlowElement> subProcess = new ArrayList<FlowElement>();
        List<FlowElement> exclusiveGateway = new ArrayList<FlowElement>();
        List<FlowElement> parallelGateway = new ArrayList<FlowElement>();
        List<FlowElement> sequenceFlow = new ArrayList<FlowElement>();
        List<FlowElement> userTask = new ArrayList<FlowElement>();
        List<FlowElement> startEvent = new ArrayList<FlowElement>();
        List<FlowElement> endEvent = new ArrayList<FlowElement>();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof SubProcess) {
                subProcess.add(flowElement);
            }
            if (flowElement instanceof ExclusiveGateway) {
                exclusiveGateway.add(flowElement);
            }
            if (flowElement instanceof ParallelGateway) {
                parallelGateway.add(flowElement);
            }
            if (flowElement instanceof SequenceFlow) {
                sequenceFlow.add(flowElement);
            }
            if (flowElement instanceof UserTask) {
                userTask.add(flowElement);
            }
            if (flowElement instanceof StartEvent) {
                startEvent.add(flowElement);
            }
            if (flowElement instanceof EndEvent) {
                endEvent.add(flowElement);
            }
        }
        map.put(SubProcess.class, subProcess);
        map.put(ExclusiveGateway.class, exclusiveGateway);
        map.put(ParallelGateway.class, parallelGateway);
        map.put(SequenceFlow.class, sequenceFlow);
        map.put(UserTask.class, userTask);
        map.put(StartEvent.class, startEvent);
        map.put(EndEvent.class, endEvent);
        return map;
    }

    @Override
    public Map<Class, Map<String, FlowElement>> demergeFlowElementsMap(Collection<FlowElement> flowElements) {
        Map<Class, List<FlowElement>> map = this.demergeFlowElements(flowElements);
        Map<Class, Map<String, FlowElement>> all = new LinkedHashMap<Class, Map<String, FlowElement>>();
        for (Iterator<Map.Entry<Class, List<FlowElement>>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Class, List<FlowElement>> entry = iterator.next();
            Class key = entry.getKey();
            List<FlowElement> value = entry.getValue();
            Map<String, FlowElement> sub = new LinkedHashMap<String, FlowElement>();
            for (FlowElement flowElement : value) {
                sub.put(flowElement.getId(), flowElement);
            }
            all.put(key, sub);
        }
        return all;
    }

    @Override
    public List<FlowElement> getNextTaskFlowElement(String procDefId, String start, String end) {
        Collection<FlowElement> flowElements = this.recursionFlowElements(this.getBpmnModel(procDefId).getMainProcess().getFlowElements());
        return this.getNextTaskFlowElement(flowElements, null, start, end);
    }

    private List<FlowElement> getNextTaskFlowElement(Collection<FlowElement> flowElements, List<FlowElement> filter, String start, String end) {
        List<FlowElement> finalFilter = new ArrayList<FlowElement>();
        if (CollectionUtils.isNotEmpty(filter)) {
            finalFilter.addAll(filter);
        }
        Map<String, FlowElement> flowElementMap = this.listToMap(flowElements);
        Map<Class, List<FlowElement>> demergeFlowElements = this.demergeFlowElements(flowElements);
        List<FlowElement> list = demergeFlowElements.get(SequenceFlow.class);
        for (FlowElement flowElement : list) {
            SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
            String key = sequenceFlow.getId();
            String sourceRef = sequenceFlow.getSourceRef();
            String targetRef = sequenceFlow.getTargetRef();
            FlowElement target = flowElementMap.get(targetRef);
            if (start.equals(sourceRef)) {
                if (!(target instanceof EndEvent)) {
                    if (end.equals(targetRef)) {
                        finalFilter.add(flowElement);
                        System.out.println(key + " " + sourceRef + " " + targetRef);
                        if(target instanceof UserTask){
                            break;
                        }
                    } else {
                        if (!(target instanceof UserTask)) {
                            finalFilter.add(flowElement);
                            System.out.println(key + " " + sourceRef + " " + targetRef);
                            finalFilter = this.getNextTaskFlowElement(flowElements, finalFilter, targetRef, end);
                        }
                    }
                }
            }
        }
        return finalFilter;
    }

    @Override
    public Map<String, Object> getNextTaskFlowElementVariable(String procDefId, String start, String end) {
        List<FlowElement> flowElements = this.getNextTaskFlowElement(procDefId, start, end);
        Map<String, Object> variable = new HashMap<String, Object>();
        for(FlowElement flowElement : flowElements){
            SequenceFlow sequenceFlow = (SequenceFlow)flowElement;
            String conditionExpression =  sequenceFlow.getConditionExpression();
            if(StringUtils.isNotBlank(conditionExpression)){
                conditionExpression = conditionExpression.replaceAll("[$|'{'|'|'}']+", "");
                String[] array = conditionExpression.split("==");
                if(ArrayUtils.isNotEmpty(array) && array.length > 1){
                    variable.put(array[0], array[1]);
                }
            }
        }
        return variable;
    }
}
