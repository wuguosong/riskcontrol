package process;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yk.process.entity.FlowConfig;
import com.yk.process.entity.NodeConfig;
import com.yk.process.entity.ProcConst;
import com.yk.process.entity.TaskConfig;
import com.yk.process.service.IProcessService;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.format;
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
        for(Process process : list ){
            System.out.println(JSON.toJSONString(process));
        }
    }

    @Test
    public void testSimpleProcess(){
        String proDefId = String.valueOf(processService.listLastProcessDefinition("preAssessment", "58c7909122ddf2207a7cd6c9").get(0).get("ID_"));
        BpmnModel bpmnModel = processService.getBpmnModel(proDefId);
        //System.out.println("Process->" + bpmnModel.getProcesses().size());
        Process mainProcess = bpmnModel.getMainProcess();
        //System.out.println("mainProcess->");
        // System.out.println(JSON.toJSONString(mainProcess));
        System.out.println("FlowElement->");
        Collection<FlowElement> list = mainProcess.getFlowElements();
        int index = 0;
        for (FlowElement ele : list){
            System.out.println(index + "," + ele.getId() + ", " + ele.getName() + ">" + ele.getClass().getSimpleName());
            index ++;
        }
        System.out.println(list.size());
        //System.out.println(JSON.toJSONString(bpmnModel.getProcesses().get(0)));
    }

    @Test
    public void testLast(){
        List<HashMap<String, Object>> all = processService.listProcessDefinition("bulletin", "519398fa6ec3456fbd89d68738a9dd5a");
        List<HashMap<String, Object>> last = processService.listLastProcessDefinition("bulletin", "519398fa6ec3456fbd89d68738a9dd5a");
        System.out.println(all.size());
        System.out.println(last.size());
    }




    @Test
    public void testSubProcess(){
        HashMap<String, Object> data = processService.listLastProcessDefinition("preAssessment","58c7909122ddf2207a7cd6c9").get(0);
        BpmnModel bpmnModel = processService.getBpmnModel(String.valueOf(data.get("ID_")));
        Process mainProcess = bpmnModel.getMainProcess();
        String mainProcessJsonStr = JSON.toJSONString(mainProcess);
        JSONObject mainProcessJsonObject = JSON.parseObject(mainProcessJsonStr, JSONObject.class);
        JSONArray flowElements = mainProcessJsonObject.getJSONArray("flowElements");
        System.out.println(flowElements.size());
    }

    @Test
    public void testSubProcessByApi(){
        HashMap<String, Object> data = processService.listLastProcessDefinition("preAssessment","58c7909122ddf2207a7cd6c9").get(0);
        BpmnModel bpmnModel = processService.getBpmnModel(String.valueOf(data.get("ID_")));
        Process process = bpmnModel.getProcessById("subprocess1");
        System.out.println(process);
        FlowElement flowElement = bpmnModel.getFlowElement("subprocess1");
        System.out.println(flowElement);
        SubProcess subProcess =(SubProcess)flowElement;
        System.out.println(subProcess);
        Collection<FlowElement> flowElements = subProcess.getFlowElements();
        int index = 0;
        for (FlowElement ele : flowElements){
            System.out.println(index + "," + ele.getId() + ", " + ele.getName() + ">" + ele.getClass().getSimpleName());
            index ++;
        }
        System.out.println("0000000000000000000000000000000000000000000000000000");
        System.out.println(flowElements.size() + bpmnModel.getMainProcess().getFlowElements().size());
    }

    @Test
    public void testSubProcessFlowByXML(){
    }

    @Test
    public void testFlowConfigByApi(){
        HashMap<String, Object> data = processService.listLastProcessDefinition("preAssessment","58c7909122ddf2207a7cd6c9").get(0);
        BpmnModel bpmnModel = processService.getBpmnModel(String.valueOf(data.get("ID_")));
        Process mainProcess = bpmnModel.getMainProcess();
        Collection<FlowElement> flowElements = mainProcess.getFlowElements();
        int index = 0;
        for (FlowElement flowElement : flowElements){
            // System.out.println(index + "," + flowElement.getId() + ", " + flowElement.getName() + ">" + flowElement.getClass().getSimpleName());
            index ++;
            if("SequenceFlow".equals(flowElement.getClass().getSimpleName())){
                SequenceFlow sequenceFlow = (SequenceFlow)flowElement;
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
    public void testNodeConfig(){
        NodeConfig nodeConfig = processService.createNodeConfig("preAssessment","58c7909122ddf2207a7cd6c9");
        List<TaskConfig> taskConfigs = nodeConfig.getStates();
        System.out.println("======TaskConfig=======");
        for(TaskConfig taskConfig : taskConfigs){
            System.out.println(taskConfig.getId() + "--> [" + taskConfig.getName() + "," + taskConfig.getType() + "]");
        }
        System.out.println("======FlowConfig=======");
        List<FlowConfig> flowConfigs = nodeConfig.getEdges();
        for(FlowConfig flowConfig : flowConfigs){
            System.out.println(flowConfig.getId() + "--> [" + flowConfig.getFrom().getId() + "," + flowConfig.getTo().getId() +"]");
        }
    }

    @Test
    public void testSub(){
        processService.createNodeConfig("preAssessment","58c7909122ddf2207a7cd6c9");
    }
}
