package com.yk.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yk.process.dao.IProcessMapper;
import com.yk.process.entity.FlowConfig;
import com.yk.process.entity.ProcConst;
import com.yk.process.entity.TaskConfig;
import com.yk.process.service.IProcessService;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.collections4.CollectionUtils;
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
        BpmnModel bpmnModel = converter.convertToBpmnModel((XMLStreamReader) reader);
        return bpmnModel;
    }

    @Override
    public List<TaskConfig> createTaskConfig(String processKey, String businessKey) {
        List<HashMap<String, Object>> list = this.listLastProcessDefinition(processKey, businessKey);
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("没有找到[" + processKey + "," + businessKey + "]的流程模型!");
        }
        HashMap<String, Object> data = list.get(0);
        BpmnModel bpmnModel = this.getBpmnModel(String.valueOf(data.get(ID_)));
        Process mainProcess = bpmnModel.getMainProcess();
        Collection<FlowElement> flowElements = mainProcess.getFlowElements();
        List<TaskConfig> taskConfigs = new ArrayList<TaskConfig>();
        int index = 1;
        for (FlowElement flowElement : flowElements) {
            String type = flowElement.getClass().getSimpleName();
            String id = flowElement.getId();
            String name = flowElement.getName();
            if (!ProcConst.SEQUENCE_FLOW.equals(type)) {// 过滤掉流向，只记录：任务/网关/开始/结束
                TaskConfig taskConfig = new TaskConfig();
                taskConfig.setProcess(String.valueOf(data.get(ID_)));
                taskConfig.setDeployment(new Long(String.valueOf(data.get(DEPLOYMENT_ID_))));
                taskConfig.setIndex(index);
                taskConfig.setName(name);
                taskConfig.setId(id);
                taskConfig.setType(type);
                taskConfigs.add(taskConfig);
                index++;
            }
        }
        return taskConfigs;
    }


    @Override
    public List<FlowConfig> createFlowConfig(String processKey, String businessKey) {
        List<FlowConfig> flowConfigs = new ArrayList<FlowConfig>();
        List<HashMap<String, Object>> list = this.listLastProcessDefinition(processKey, businessKey);
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("没有找到[" + processKey + "," + businessKey + "]的流程模型!");
        }
        Map<String, TaskConfig> taskConfigMap = this.toTaskConfigMap(this.createTaskConfig(processKey, businessKey));
        HashMap<String, Object> data = list.get(0);
        BpmnModel bpmnModel = this.getBpmnModel(String.valueOf(data.get(ID_)));
        Process mainProcess = bpmnModel.getMainProcess();
        String mainProcessJsonStr = JSON.toJSONString(mainProcess);
        JSONObject mainProcessJsonObject = JSON.parseObject(mainProcessJsonStr, JSONObject.class);
        JSONArray flowElements = mainProcessJsonObject.getJSONArray(ProcConst.FLOW_ELEMENTS);
        for (int i = 0; i < flowElements.size(); i++) {
            JSONObject flowElement = flowElements.getJSONObject(i);
            JSONArray outgoingFlows = flowElement.getJSONArray(ProcConst.OUTGOING_FLOWS);
            TaskConfig taskConfig = taskConfigMap.get(flowElement.getString(ProcConst.ID));
            if (taskConfig != null) {
                if (ProcConst.SEQUENCE_FLOW.equals(taskConfig.getType()) || ProcConst.END_EVENT.equals(taskConfig.getType())) {
                    continue;// 流向和结束不需要进行计算，其他类型只计算流出方向
                } else {
                    for (int j = 0; j < outgoingFlows.size(); j++) {
                        JSONObject outgoingFlow = outgoingFlows.getJSONObject(j);
                        if (outgoingFlow != null) {
                            FlowConfig flowConfig = new FlowConfig();
                            flowConfig.setId(outgoingFlow.getString(ProcConst.ID));
                            flowConfig.setName(outgoingFlow.getString(ProcConst.NAME));
                            flowConfig.setFrom(taskConfigMap.get(flowElement.getString(ProcConst.ID)));
                            flowConfig.setTo(taskConfigMap.get(outgoingFlow.getString(ProcConst.TARGET_REF)));
                            flowConfigs.add(flowConfig);
                        }
                    }
                }
            }
        }
        return flowConfigs;
    }

    @Override
    public Map<String, Object> renderProcessProgress(String processKey, String businessKey) {
        List<HashMap<String, Object>> processConfigurations = this.listProcessConfiguration(processKey, businessKey);
        // 设置节点状态
        Map<String, TaskConfig> taskConfigMap = this.toTaskConfigMap(this.createTaskConfig(processKey, businessKey));
        for (HashMap<String, Object> processConfiguration : processConfigurations) {
            String id = String.valueOf(processConfiguration.get(ACT_ID_));
            if (taskConfigMap.get(id) != null) {
                taskConfigMap.get(id).setStatus(ProcConst.ALREADY);
            }
        }
        List<TaskConfig> taskConfigs = this.toTaskConfigList(taskConfigMap);
        for (TaskConfig taskConfig : taskConfigs) {
            if (StringUtils.isBlank(taskConfig.getStatus())) {
                taskConfig.setStatus(ProcConst.AGENCY);
            }
        }
        // 设置流向状态
        List<FlowConfig> flowConfigs = this.createFlowConfig(processKey, businessKey);
        for(int i = 1; i < processConfigurations.size(); i++){
            String alreadyFrom = String.valueOf(processConfigurations.get(i - 1).get(ACT_ID_));
            String alreadyTo = String.valueOf(processConfigurations.get(i).get(ACT_ID_));
            for(FlowConfig flowConfig : flowConfigs){
                String from = flowConfig.getFrom().getId();
                String to = flowConfig.getTo().getId();
                if(alreadyFrom.equals(from) && alreadyTo.equals(to)){
                    flowConfig.setStatus(ProcConst.ALREADY);
                }
            }
        }
        for(FlowConfig flowConfig : flowConfigs){
            if(StringUtils.isBlank(flowConfig.getStatus())){
                flowConfig.setStatus(ProcConst.AGENCY);
            }
        }
        // 封装数据
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(ProcConst.STATES, taskConfigs);
        data.put(ProcConst.EDGES, flowConfigs);
        return data;
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

    /**
     * 将流向List变成流向Map
     *
     * @param flowConfigs 流向List
     * @return Map<String, FlowConfig>
     */
    private Map<String, FlowConfig> toFlowConfigMap(List<FlowConfig> flowConfigs) {
        Map<String, FlowConfig> flowConfigMap = new LinkedHashMap<String, FlowConfig>();
        for (FlowConfig flowConfig : flowConfigs) {
            flowConfigMap.put(flowConfig.getId(), flowConfig);
        }
        return flowConfigMap;
    }

    /**
     * 将流向Map变成流向List
     *
     * @param flowConfigMap 流向List
     * @return List<FlowConfig>
     */
    private List<FlowConfig> toFlowConfigList(Map<String, FlowConfig> flowConfigMap) {
        List<FlowConfig> flowConfigs = new ArrayList<FlowConfig>();
        Set<Map.Entry<String, FlowConfig>> entrySet = flowConfigMap.entrySet();
        for (Iterator<Map.Entry<String, FlowConfig>> iterator = entrySet.iterator(); iterator.hasNext(); ) {
            Map.Entry<String, FlowConfig> entry = iterator.next();
            flowConfigs.add(entry.getValue());
        }
        return flowConfigs;
    }
}
