package process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yk.process.entity.FlowConfig;
import com.yk.process.entity.NodeConfig;
import com.yk.process.entity.TaskConfig;
import com.yk.process.service.IProcessService;
import com.yk.sign.service.ISignService;
import common.Constants;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.*;
import org.activiti.engine.task.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by LiPan on 2019/2/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class ProcessTest {
    @Resource
    private IProcessService processService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private HistoryService historyService;
    @Resource
    private TaskService taskService;

    @Test
    public void testProcessNodeList() {
        List<HashMap<String, Object>> list = processService.listProcessNode("preAssessment", "58c7909122ddf2207a7cd6c9");
        System.out.println(list.size());
        for (HashMap<String, Object> map : list) {
            System.out.println(JSON.toJSONString(map));
        }
    }

    @Test
    public void testProcessNodeDetailList() {
        List<HashMap<String, Object>> list = processService.listProcessNodeDetail("preAssessment", "58c7909122ddf2207a7cd6c9");
        for (HashMap<String, Object> map : list) {
            System.out.println(JSON.toJSONString(map));
        }
        System.out.println(list.size());
    }

    @Test
    public void xml2ModelTest() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("preAssessment").singleResult();
        //获取流程资源的名称
        String sourceName = processDefinition.getResourceName();
        //获取流程资源
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getId(), sourceName);
        //创建转换对象
        BpmnXMLConverter converter = new BpmnXMLConverter();
        //读取xml文件
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try {
            reader = factory.createXMLStreamReader(inputStream);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        //将xml文件转换成BpmnModel
        BpmnModel bpmnModel = converter.convertToBpmnModel((XMLStreamReader) reader);
        //验证bpmnModel是否为空
        assertNotNull(bpmnModel);
        org.activiti.bpmn.model.Process process = bpmnModel.getMainProcess();
        //验证转换的流程id
        assertEquals("leave", process.getId());
    }

    @Test
    public void xml2ModelTest2() {
        HashMap<String, Object> map = processService.listLastProcessDefinition("preAssessment", "58c7909122ddf2207a7cd6c9").get(0);
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(String.valueOf(map.get("ID_"))).singleResult();
        //获取流程资源 getResourceAsStream(processDefinition.getId(), sourceName);
        InputStream inputStream = repositoryService.getProcessModel(processDefinition.getId());
        //创建转换对象
        BpmnXMLConverter converter = new BpmnXMLConverter();
        //读取xml文件
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try {
            reader = factory.createXMLStreamReader(inputStream);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        //将xml文件转换成BpmnModel
        BpmnModel bpmnModel = converter.convertToBpmnModel((XMLStreamReader) reader);
        System.out.println(JSON.toJSONString(bpmnModel));
        System.out.println(bpmnModel.getMainProcess());
        Process process = bpmnModel.getMainProcess();
        System.out.println(process.getExtensionElements());
        System.out.println(process.getFlowElements());
        //验证bpmnModel是否为空
        assertNotNull(bpmnModel);
    }

    @Test
    public void getProcessDefinitionTest() {
        HashMap<String, Object> map = processService.listLastProcessDefinition("preAssessment", "58c7909122ddf2207a7cd6c9").get(0);
        System.out.println(JSON.toJSONString(map));
    }

    @Test
    public void testProcess() {
        String proDefId = String.valueOf(processService.listLastProcessDefinition("preAssessment", "58c7909122ddf2207a7cd6c9").get(0).get("ID_"));
        BpmnModel bpmnModel = processService.getBpmnModel(proDefId);
        System.out.println("BpmnModel->");
        System.out.println(bpmnModel);
        Process mainProcess = bpmnModel.getMainProcess();
        System.out.println("Process->");
        System.out.println(JSON.toJSONString(mainProcess));
        System.out.println("ExtensionElements->");
        Map<String, List<ExtensionElement>> eleMap = mainProcess.getExtensionElements();
        Set<Map.Entry<String, List<ExtensionElement>>> entrySet = eleMap.entrySet();
        for (Iterator<Map.Entry<String, List<ExtensionElement>>> iterator = entrySet.iterator(); iterator.hasNext(); ) {
            Map.Entry<String, List<ExtensionElement>> entry = iterator.next();
            String key = entry.getKey();
            List<ExtensionElement> value = entry.getValue();
            System.out.println("Key:" + key);
            System.out.println("Value:" + JSON.toJSONString(value));
        }
        System.out.println("FlowElements->");
        Collection<FlowElement> flowElements = mainProcess.getFlowElements();
        System.out.println(JSON.toJSONString(flowElements));
        System.out.println("Processes->");
        List<Process> list = bpmnModel.getProcesses();
        for (Process process : list) {
            System.out.println(JSON.toJSONString(process));
        }
    }

    @Test
    public void testSimpleProcess() {
        String proDefId = String.valueOf(processService.listLastProcessDefinition("preAssessment", "58c7909122ddf2207a7cd6c9").get(0).get("ID_"));
        BpmnModel bpmnModel = processService.getBpmnModel(proDefId);
        //System.out.println("Process->" + bpmnModel.getProcesses().size());
        Process mainProcess = bpmnModel.getMainProcess();
        //System.out.println("mainProcess->");
        // System.out.println(JSON.toJSONString(mainProcess));
        System.out.println("FlowElement->");
        Collection<FlowElement> list = mainProcess.getFlowElements();
        int index = 0;
        for (FlowElement ele : list) {
            System.out.println(index + "," + ele.getId() + ", " + ele.getName() + ">" + ele.getClass().getSimpleName());
            index++;
        }
        System.out.println(list.size());
        //System.out.println(JSON.toJSONString(bpmnModel.getProcesses().get(0)));
    }

    @Test
    public void testLast() {
        List<HashMap<String, Object>> all = processService.listProcessDefinition("bulletin", "519398fa6ec3456fbd89d68738a9dd5a");
        List<HashMap<String, Object>> last = processService.listLastProcessDefinition("bulletin", "519398fa6ec3456fbd89d68738a9dd5a");
        System.out.println(all.size());
        System.out.println(last.size());
    }


    @Test
    public void testSubProcess() {
        HashMap<String, Object> data = processService.listLastProcessDefinition("preAssessment", "58c7909122ddf2207a7cd6c9").get(0);
        BpmnModel bpmnModel = processService.getBpmnModel(String.valueOf(data.get("ID_")));
        Process mainProcess = bpmnModel.getMainProcess();
        String mainProcessJsonStr = JSON.toJSONString(mainProcess);
        JSONObject mainProcessJsonObject = JSON.parseObject(mainProcessJsonStr, JSONObject.class);
        JSONArray flowElements = mainProcessJsonObject.getJSONArray("flowElements");
        System.out.println(flowElements.size());
    }

    @Test
    public void testSubProcessByApi() {
        HashMap<String, Object> data = processService.listLastProcessDefinition("preAssessment", "58c7909122ddf2207a7cd6c9").get(0);
        BpmnModel bpmnModel = processService.getBpmnModel(String.valueOf(data.get("ID_")));
        Process process = bpmnModel.getProcessById("subprocess1");
        System.out.println(process);
        FlowElement flowElement = bpmnModel.getFlowElement("subprocess1");
        System.out.println(flowElement);
        SubProcess subProcess = (SubProcess) flowElement;
        System.out.println(subProcess);
        Collection<FlowElement> flowElements = subProcess.getFlowElements();
        int index = 0;
        for (FlowElement ele : flowElements) {
            System.out.println(index + "," + ele.getId() + ", " + ele.getName() + ">" + ele.getClass().getSimpleName());
            index++;
        }
        System.out.println("0000000000000000000000000000000000000000000000000000");
        System.out.println(flowElements.size() + bpmnModel.getMainProcess().getFlowElements().size());
    }

    @Test
    public void testSubProcessFlowByXML() {
    }

    @Test
    public void testFlowConfigByApi() {
        HashMap<String, Object> data = processService.listLastProcessDefinition("preAssessment", "58c7909122ddf2207a7cd6c9").get(0);
        BpmnModel bpmnModel = processService.getBpmnModel(String.valueOf(data.get("ID_")));
        Process mainProcess = bpmnModel.getMainProcess();
        Collection<FlowElement> flowElements = mainProcess.getFlowElements();
        int index = 0;
        for (FlowElement flowElement : flowElements) {
            // System.out.println(index + "," + flowElement.getId() + ", " + flowElement.getName() + ">" + flowElement.getClass().getSimpleName());
            index++;
            if ("SequenceFlow".equals(flowElement.getClass().getSimpleName())) {
                SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                System.out.println(sequenceFlow);
                System.out.println(sequenceFlow.getConditionExpression());
                System.out.println(sequenceFlow.getSkipExpression());
                System.out.println(sequenceFlow.getDocumentation());
                System.out.println(sequenceFlow.getId());
                System.out.println(sequenceFlow.getName());
                System.out.println("000000000000000000000000");
            }
        }
    }

    @Test
    public void testNodeConfig() {
        NodeConfig nodeConfig = processService.createNodeConfig("preAssessment", "58c7909122ddf2207a7cd6c9");
        List<TaskConfig> taskConfigs = nodeConfig.getStates();
        System.out.println("======TaskConfig=======");
        for (TaskConfig taskConfig : taskConfigs) {
            System.out.println(taskConfig.getId() + "--> [" + taskConfig.getName() + "," + taskConfig.getType() + "]");
        }
        System.out.println("======FlowConfig=======");
        List<FlowConfig> flowConfigs = nodeConfig.getEdges();
        for (FlowConfig flowConfig : flowConfigs) {
            System.out.println(flowConfig.getId() + "--> [" + flowConfig.getFrom().getId() + "," + flowConfig.getTo().getId() + "]");
        }
    }

    @Test
    public void testSub() {
        processService.createNodeConfig("preAssessment", "58c7909122ddf2207a7cd6c9");
    }


    @Resource
    private ISignService signService;

    @Test
    public void getNextTaskDef() {
        TaskDefinition taskDefinition = signService.getNextTaskInfo("bulletin", "63bd90dbfac74663ba5d138406c9600c");
        System.out.println(taskDefinition.getKey());
    }

    @Test
    public void test() {
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey("63bd90dbfac74663ba5d138406c9600c").list();
        for (ProcessInstance proc : list) {
            System.out.println(proc.getId());
        }
        List<org.activiti.engine.task.Task> tasks = taskService.createTaskQuery().processInstanceBusinessKey("63bd90dbfac74663ba5d138406c9600c").list();
        for (Task task : tasks) {
            System.out.println(task.getId());
            System.out.println(task.getName());
        }
    }

    @Test
    public void testFlow() {
        Task task = taskService.createTaskQuery().taskId("342513").singleResult();
        System.out.println(task.getDelegationState());
        System.out.println(task.getParentTaskId());
        System.out.println(task.getExecutionId());
        System.out.println(task.getTaskLocalVariables());
        System.out.println(task.getProcessVariables());
        System.out.println(task.getTenantId());
        System.out.println(task.getFormKey());
        System.out.println(task.getCategory());
    }

    @Test
    public void test2() {
        BpmnModel model = processService.getBpmnModel("bulletin:8:260004");
        Process mainProcess = model.getMainProcess();
        Collection<FlowElement> flowElements = processService.recursionFlowElements(mainProcess.getFlowElements());
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof SequenceFlow) {
                SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                String conditionExpression = sequenceFlow.getConditionExpression();
                String sourceRef = sequenceFlow.getSourceRef();
                String targetRef = sequenceFlow.getTargetRef();
                if (targetRef != null && targetRef.equals("usertask2")) {

                }
                if (StringUtils.isNotBlank(conditionExpression)) {
                    System.out.println(sourceRef + " " + targetRef + " " + conditionExpression);
                }
            }
        }
    }

    @Test
    public void test3() {
        BpmnModel model = processService.getBpmnModel("preReview:7:392504");
        Process mainProcess = model.getMainProcess();
        Collection<FlowElement> flowElements = processService.recursionFlowElements(mainProcess.getFlowElements());
        Map<Class, List<FlowElement>> map = processService.demergeFlowElements(flowElements);
        System.out.println(map.size());
        for (Iterator<Map.Entry<Class, List<FlowElement>>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Class, List<FlowElement>> entry = iterator.next();
            Class key = entry.getKey();
            List<FlowElement> value = entry.getValue();
            System.out.println(key.getSimpleName() + " " + value.size());
        }
    }

    /**
     * 给定起点与终点taskKey,获取其中的condition
     */
    @Test
    public void test4() {
        // "bulletin:8:260004"
        // "usertask1"->"usertask2"
        // "usertask3"->"usertask20"
        // 需要迭代查找,知道没有target为止
        // 需要判断无限循环
        // 起点和终点相同时,终止
        List<FlowElement> list = processService.getNextTaskFlowElement("bulletin:8:260004", "usertask2", "usertask1");
        for (FlowElement flowElement : list) {
            SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
            String key = sequenceFlow.getId();
            String sourceRef = sequenceFlow.getSourceRef();
            String targetRef = sequenceFlow.getTargetRef();
            System.out.println("res:" + key + " " + sourceRef + " " + targetRef);
        }
    }

    @Test
    public void test5() {
        processService.getNextTaskFlowElementVariable("bulletin:8:260004", "usertask1", "usertask2");
    }

    String task_id = "395008";
    String business_id = "5c91e1c45544cd3abc49dd88";

    @Test
    public void test6() {
        Task curTask = taskService.createTaskQuery().taskId(task_id).singleResult();
        String curTaskKey = curTask.getTaskDefinitionKey();
        String nexTaskKey = signService.getNextTaskInfo(Constants.PROCESS_KEY_PREREVIEW, business_id).getKey();
        System.out.println(curTaskKey);
        System.out.println(nexTaskKey);
        Map<String, Object> variable = processService.getNextTaskFlowElementVariable(curTask.getProcessDefinitionId(), curTaskKey, nexTaskKey);
        System.out.println(JSON.toJSONString(variable));
    }

    @Test
    public void test7() {
        List<FlowElement> list = processService.getNextTaskFlowElement("formalReview:14:342504", "usertask3", "usertask20");
        for (FlowElement flowElement : list) {
            SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
            String key = sequenceFlow.getId();
            String sourceRef = sequenceFlow.getSourceRef();
            String targetRef = sequenceFlow.getTargetRef();
            System.out.println("result:" + key + " " + sourceRef + " " + targetRef);
        }
    }


    @Test
    public void getNextTask() {
        String key = "formalReview";
        String business_id = "5c935f1f5544cd3968bd9fc8";
        String start = "usertask19";
        TaskDefinition taskDefinition = null;
        try {
            taskDefinition = signService.getNextTaskInfo(key, business_id);
        } catch (Exception e) {

        }
        if (taskDefinition != null) {
            String end = taskDefinition.getKey();
            System.out.println("开始节点>\t" + start);
            System.out.println("结束节点>\t" + end);
            String procDefId = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).orderByProcessDefinitionVersion().desc().list().get(0).getId();
            System.out.println("流程定义ID>\t" + procDefId);
            List<FlowElement> flowElements = processService.getNextTaskFlowElement(procDefId, start, end);
            for (FlowElement flowElement : flowElements) {
                System.out.println("流向信息>\t" + flowElement.getId());
            }
            Map<String, Object> variable = processService.getNextTaskFlowElementVariable(procDefId, start, end);
            System.out.println("变量信息>\t" + JSON.toJSONString(variable));
        }
        HashMap<String, Object> rejectType = signService.validateSign(key, business_id);
        System.out.println("拒绝类型>\t" + JSON.toJSONString(rejectType));
    }

    @Test
    public void testModel() {
        String key = "formalReview";
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).orderByProcessDefinitionVersion().desc().list();
        for(ProcessDefinition processDefinition : list){
            System.out.println(processDefinition);
        }
        if (CollectionUtils.isNotEmpty(list)){
            ProcessDefinition processDefinition = list.get(0);// 最新版本
            BpmnModel bpmnModel = getModel(processDefinition.getId());
            System.out.println(bpmnModel);
        }
    }

    private BpmnModel getModel(String processDefinitionId) {
        InputStream inputStream = repositoryService.getProcessModel(processDefinitionId);
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
}
