package process;

import com.alibaba.fastjson.JSON;
import com.yk.process.entity.TaskConfig;
import com.yk.process.service.IProcessService;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
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
        for(Process process : list ){
            System.out.println(JSON.toJSONString(process));
        }
    }

    @Test
    public void testSimpleProcess(){
        String proDefId = String.valueOf(processService.listLastProcessDefinition("preAssessment", "58c7909122ddf2207a7cd6c9").get(0).get("ID_"));
        BpmnModel bpmnModel = processService.getBpmnModel(proDefId);
        Process mainProcess = bpmnModel.getMainProcess();
        System.out.println("Process->");
        // System.out.println(JSON.toJSONString(mainProcess));
        System.out.println("FlowElement->");
        Collection<FlowElement> list = mainProcess.getFlowElements();
        for (FlowElement ele : list){
            System.out.println(ele.getId() + ", " + ele.getName() + ">" + ele.getClass().getSimpleName());
        }
        System.out.println(list.size());
    }

    @Test
    public void testLast(){
        List<HashMap<String, Object>> all = processService.listProcessDefinition("bulletin", "519398fa6ec3456fbd89d68738a9dd5a");
        List<HashMap<String, Object>> last = processService.listLastProcessDefinition("bulletin", "519398fa6ec3456fbd89d68738a9dd5a");
        System.out.println(all.size());
        System.out.println(last.size());
    }

    @Test
    public void testTaskConfig(){
        List<TaskConfig> taskConfigs = processService.createTaskConfig("bulletin", "519398fa6ec3456fbd89d68738a9dd5a");
        for(TaskConfig taskConfig : taskConfigs){
            System.out.println(taskConfig);
        }
        System.out.println(taskConfigs.size());
    }

    @Test
    public void testFlowConfig(){
        processService.createFlowConfig("bulletin", "519398fa6ec3456fbd89d68738a9dd5a");
    }

    @Test
    public void testRenderProcess(){
        processService.renderProcessProgress("formalReview", "5a96192022ddf260e1f73ecb");
    }
}
