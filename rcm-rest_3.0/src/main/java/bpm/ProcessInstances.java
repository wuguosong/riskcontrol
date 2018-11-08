package bpm;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.junit.*;


public class ProcessInstances {

	@Test
	public void createTable() {
	//1.创建Activiti配置对象的实例
	ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
	//2.设置数据库连接信息
	// 设置数据库地址
	configuration.setJdbcUrl("jdbc:mysql://172.25.105.74:3306/testactiviti?createDatabaseIfNotExist&amp;useUnicode=true&amp;characterEncoding=utf8");
	// 数据库驱动
	configuration.setJdbcDriver("com.mysql.jdbc.Driver");
	// 用户名
	configuration.setJdbcUsername("root");
	// 密码
	configuration.setJdbcPassword("pccw");
	// 设置数据库建表策略
	/**
	 * DB_SCHEMA_UPDATE_TRUE：如果不存在表就创建表，存在就直接使用
	 * DB_SCHEMA_UPDATE_FALSE：如果不存在表就抛出异常
	 * DB_SCHEMA_UPDATE_CREATE_DROP：每次都先删除表，再创建新的表
	 */
	configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
	//3.使用配置对象创建流程引擎实例（检查数据库连接等环境信息是否正确）
	ProcessEngine processEngine = configuration.buildProcessEngine();
	System.out.println(processEngine);
	}

	@Test
	public void testProcessEngine(){
	    // 获取流程引擎
	    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    System.out.println(processEngine);
	}

	
	@Test
	public void deploy() throws Exception {
	// 获取流程引擎
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	// 获取仓库服务的实例
	Deployment deployment = processEngine.getRepositoryService()//
	.createDeployment()//
	.addClasspathResource("diagrams/Contract.bpmn")//
	.addClasspathResource("diagrams/Contract.png")//
	.name("contractApproved")
	.deploy();
	System.out.println(deployment.getId()+"        "+deployment.getName());
	}

	//2. 启动流程
	//@Test
	public void startProcess1() throws Exception {
	// 获取流程引擎对象
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	// 启动流程
	//使用流程定义的key启动流程实例，默认会按照最新版本启动流程实例
	ProcessInstance pi = processEngine.getRuntimeService()
		.startProcessInstanceByKey("myProcess");
	System.out.println("pid:" + pi.getId() +",activitiId:" + pi.getActivityId());
	}
	@Test
	public void startProcess2() throws Exception {
		// 获取流程引擎对象
		
		ProcessEngineConfiguration configuration=ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
		configuration.setJdbcDriver("com.mysql.jdbc.Driver");
		configuration.setJdbcUrl("jdbc:mysql://114.251.247.74:3306/testactiviti?createDatabaseIfNotExist=true&amp;useUnicode=true&amp;characterEncoding=utf8");
		configuration.setJdbcUsername("root");
		configuration.setJdbcPassword("pccw");
		//建表策略
		configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		//创建核心心对象
		ProcessEngine processEngine =configuration.buildProcessEngine();
	
		// 启动流程
		//使用流程定义的key启动流程实例，默认会按照最新版本启动流程实例
		ProcessInstance pi = processEngine.getRuntimeService()
			.startProcessInstanceByKey("myProcess");
		System.out.println("pid:" + pi.getId() +",activitiId:" + pi.getActivityId());
		}

	/**
	 * @param json
	 * {
	 *   ProcessKey:"",//流程键
	 *   UserID："",    //用户ID
	 *   form
	 * }
	 * @return
	 * @throws Exception
	 */
	@Test
	public String startProcess(String json) throws Exception {
		//1.获取流程引擎对象
		ProcessEngine processEngine = BpmFactory.getInstance();
		//2.分配流程变量，执行人和表单参数
		Document doc=Document.parse(json);
		String ProcessKey=doc.getString("ProcessKey");
		String UserID=doc.getString("UserID");
		String ObjectID=doc.getString("ObjectID");
		
		Map<String, Object> vars=new HashMap<String,Object>();
		vars.put("inputUser", UserID);
		vars.put("ObjectID", ObjectID);
		
		//3.启动流程实例
		ProcessInstance pi = processEngine.getRuntimeService()
			.startProcessInstanceByKey(ProcessKey,vars);
		System.out.println("pid:" + pi.getId() +",activitiId:" + pi.getActivityId());
	
		
		//4.1根据流程实例ID,查找该任务ID
		// 查询任务的列表
		 Task atask = processEngine.getTaskService()
		.createTaskQuery()//创建任务查询对象
		//.taskAssignee()//指定个人任务办理人
		.processInstanceId(pi.getId())
		.singleResult();
		 System.out.println("taskID:" + atask.getId());
		//4.2完成该任务
		 processEngine.getTaskService()
			.complete(atask.getId());
		
		//System.out.println("pid:" + pi.getId() +",activitiId:" + pi.getActivityId());
		return "pid:" + pi.getId() +",activitiId:" + pi.getActivityId();
		
		}
	//3. 查看任务
	@Test
	public List<Document> GetMyTask(String json) throws Exception {
	
	//2.分配流程变量，执行人和表单参数
	Document doc=Document.parse(json);
	String assignee=doc.getString("assignee");
	
			
	// 获取流程引擎对象
	ProcessEngine processEngine = BpmFactory.getInstance();
	// 查询任务的列表
	List<Task> tasks = processEngine.getTaskService()
	.createTaskQuery()//创建任务查询对象
	.taskAssignee(assignee)//指定个人任务办理人

	.list();
	
	
	// 遍历结合查看内容
	List<Document> docList=new ArrayList<Document>();
	for (Task task : tasks) {
	
		Document document=new Document();
		document.put("taskId", task.getId());
		document.put("taskName", task.getName());
		document.put("CreateTime", task.getCreateTime().toString());
		document.put("Description", task.getDescription());
		document.put("ProcessDefinitionId", task.getProcessDefinitionId());
		document.put("ExecutionId", task.getExecutionId());
		document.put("FormKey", task.getFormKey());
		document.put("ObjectID", processEngine.getTaskService().getVariable(task.getId(),"ObjectID"));
		
//		document.put("Description", task.getProcessVariables()getDescription());
		docList.add(document);

		System.out.println("taskId:" + task.getId()+",taskName:" + task.getName());
		System.out.println("*************************");
	
	}
	
	return docList;
	
	//
	
	}
	//3. 查看历史任务
		@Test
		public List<Document> GetMyHistoricTask(String json) throws Exception {
		
		//2.分配流程变量，执行人和表单参数
		Document doc=Document.parse(json);
		String assignee=doc.getString("assignee");
		
				
		// 获取流程引擎对象
		ProcessEngine processEngine = BpmFactory.getInstance();
		// 查询任务的列表
		// 使用办理人查询流程实例
			List<HistoricTaskInstance> tasks = processEngine.getHistoryService()//
			.createHistoricTaskInstanceQuery()//创建历史任务查询
			.taskAssignee(assignee)//指定办理人查询历史任务
			.list();
		
		
		// 遍历结合查看内容
		List<Document> docList=new ArrayList<Document>();
		for (HistoricTaskInstance task : tasks) {
		
			Document document=new Document();
			document.put("taskId", task.getId());
			document.put("taskName", task.getName());
			document.put("CreateTime", task.getCreateTime().toString());
			document.put("Description", task.getDescription());
			document.put("FormKey", task.getFormKey());
//			document.put("ObjectID", processEngine.getTaskService().getVariable(task.getId(),"ObjectID"));
			
//			document.put("Description", task.getProcessVariables()getDescription());
			docList.add(document);

			System.out.println("taskId:" + task.getId()+",taskName:" + task.getName());
			System.out.println("*************************");
		
		}
		
		return docList;
		
		//
		
		}

	// 查询所有
	@Test
	public void queryProcessDefinition() throws Exception {
	// 获取仓库服务对象,使用版本的升序排列，查询列表
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	List<ProcessDefinition> pdList = processEngine.getRepositoryService()
	.createProcessDefinitionQuery()
	//添加查询条件
	//.processDefinitionName(processDefinitionName)
	//.processDefinitionId(processDefinitionId)
	//.processDefinitionKey(processDefinitionKey)
	//排序
	.orderByProcessDefinitionVersion().asc()
	//查询的结果集
	//.count()//返回结果集的数量
	//.listPage(firstResult, maxResults)//分页查询
	//.singleResult()//惟一结果集
	.list();//总的结果集数量
	// 遍历集合，查看内容
	for (ProcessDefinition pd : pdList) {
	System.out.println("id:" + pd.getId());
	System.out.println("name:" + pd.getName());
	System.out.println("key:" + pd.getKey());
	System.out.println("version:" + pd.getVersion());
	System.out.println("resourceName:"+pd.getDiagramResourceName());
	System.out.println("***************************************");
	}
	}

	
	//4. 办理任务
	@Test
	public String completeTask(String json) throws Exception {

		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		Document doc=Document.parse(json);
		String taskId=doc.getString("taskId");
		String approvalOpinion=doc.getString("approvalOpinion");
		
		//可以从页面上获取重要/不重要的选项，设置流程变量
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("approvalOpinion", approvalOpinion);
		processEngine.getTaskService()//
				.complete(taskId,variables);
		System.out.println("完成任务");
		return "成功完成任务！";

		
		//		String taskId = "15002";
//		// 获取流程引擎对象
//		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//		// 完成任务
//		processEngine.getTaskService()
//			.complete(taskId);
//		System.out.println("完成任务！");
	}
	// 查看流程图（xxx.png）
	@Test
	public InputStream showProcessView() throws Exception {
		// 从仓库中找需要展示的文件
		ProcessEngine processEngine = BpmFactory.getInstance();
		String deploymentId = "160001";
		List<String> names = processEngine.getRepositoryService()
				.getDeploymentResourceNames(deploymentId);
		String imageName = "";
		for (String name : names) {
			System.out.println("name:"+name);
			if(name.indexOf(".png")>=0){
				imageName = name;
			}
		}
		InputStream in = processEngine.getRepositoryService()
			      .getResourceAsStream(deploymentId, imageName);
		return in;
		
	}
	
	 public  String getProcessMap(String json) throws Exception {  
		 
		    Document doc=Document.parse(json);
			String procDefId=doc.getString("procDefId");
			String executionId=doc.getString("executionId");
		 
		 
		    ProcessEngine processEngine = BpmFactory.getInstance();
		    RepositoryService repositoryService = processEngine  
		            .getRepositoryService();  
		    ProcessDefinition processDefinition = repositoryService  
		            .createProcessDefinitionQuery().processDefinitionId(procDefId)  
		            .singleResult();  
		  
		    ProcessDefinitionImpl pdImpl = (ProcessDefinitionImpl) processDefinition;  
		    String processDefinitionId = pdImpl.getId();  
		 
		    ProcessDefinitionEntity def = (ProcessDefinitionEntity) 
		((RepositoryServiceImpl) repositoryService)  
		            .getDeployedProcessDefinition(processDefinitionId);  
		    ActivityImpl actImpl = null;  
		  
		    RuntimeService runtimeService = processEngine.getRuntimeService();  
		    ExecutionEntity execution = (ExecutionEntity) runtimeService  
		            .createExecutionQuery().executionId(executionId).singleResult();  
		    // 执行实例  
		  
		    String activitiId = execution.getActivityId();// 当前实例的执行到哪个节点  
		  
		    List<ActivityImpl> activitiList = def.getActivities();// 获得当前任务的所有节点  
		  
		    for (ActivityImpl activityImpl : activitiList) {  
		        String id = activityImpl.getId();  
		        if (id.equals(activitiId)) {// 获得执行到那个节点  
		            actImpl = activityImpl;  
		            break;  
		        }  
		    }  
		    Document retDoc=new Document();
		    retDoc.put("X",actImpl.getX());
		    retDoc.put("Y",actImpl.getY());
		    retDoc.put("Width",actImpl.getWidth());
		    retDoc.put("Height",actImpl.getHeight());
			 return retDoc.toJson();
		 
		}  
	
//	 public String getProcessMap(String procDefId,String executionId) throws Exception {  
//         
//		 
//		 
//		 ProcessEngine processEngine = BpmFactory.getInstance();
//		 RepositoryService repositoryService=processEngine.getRepositoryService();
//		 ProcessDefinition processDefinition = repositoryService  
//                 .createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();  
//
//         ProcessDefinitionImpl pdImpl = (ProcessDefinitionImpl) processDefinition;  
//         String processDefinitionId = pdImpl.getId();// 流程标识  
//
//         ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)  
//                 .getDeployedProcessDefinition(processDefinitionId);  
//         ActivityImpl actImpl = null;  
//
//         ExecutionEntity execution = (ExecutionEntity) runtimeService  
//                 .createExecutionQuery().executionId(executionId).singleResult();// 执行实例  
//
//         String activitiId = execution.getActivityId();// 当前实例的执行到哪个节点  
////       List<String>activitiIds = runtimeService.getActiveActivityIds(executionId);  
//           
//
//         List<ActivityImpl> activitiList = def.getActivities();// 获得当前任务的所有节点  
////       for(String activitiId : activitiIds){  
//         for (ActivityImpl activityImpl : activitiList) {  
//             String id = activityImpl.getId();  
//             if (id.equals(activitiId)) {// 获得执行到那个节点  
//                 actImpl = activityImpl;  
//                 break;  
//             }  
//         }  
////       }  
//         Document doc=new Document();
//		 doc.put("X",actImpl.getX());
//		 doc.put("Y",actImpl.getY());
//		 doc.put("Width",actImpl.getWidth());
//		 doc.put("Height",actImpl.getHeight());
//		 return doc.toJson();
//
//     }  

	@Test
	public void queryAllLatestVersions() throws Exception {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		
		// 查询，把最大的版本都排到后面
	List<ProcessDefinition> list = processEngine.getRepositoryService()//
	.createProcessDefinitionQuery()//
	.orderByProcessDefinitionVersion().asc()//
	.list();
	// 过滤出最新的版本
	Map<String, ProcessDefinition> map = new LinkedHashMap<String, ProcessDefinition>();
	for (ProcessDefinition pd : list) {
		map.put(pd.getKey(), pd);
	}

	// 显示
	for (ProcessDefinition pd : map.values()) {
	     System.out.println("id:" + pd.getId()// 格式：{key}-{version}
	     + ", name:" + pd.getName()// .jpdl.xml根元素的name属性的值
	     + ", key:" + pd.getKey()// .jpdl.xml根元素的key属性的值，如果不写，默认为name属性的值
	     + ", version:" + pd.getVersion()// 默认自动维护，第1个是1，以后相同key的都会自动加1
	     + ", deploymentId:" + pd.getDeploymentId()); // 所属的某个Deployment的对象
	}
	}

	@Test
	public void queryProcessState() throws Exception {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	String processInstanceId="2501";
	// 通过流程实例ID查询流程实例
	ProcessInstance pi = processEngine.getRuntimeService()
		.createProcessInstanceQuery()//创建流程实例查询，查询正在执行的流程实例
		.processInstanceId(processInstanceId)//按照流程实例ID查询
		.singleResult();//返回惟一的结果集
	if(pi!=null){
		System.out.println("当前流程在：" + pi.getActivityId());
	}else{
		System.out.println("流程已结束!!");
	}
	}
	@Test
	public void queryHistoryTask() throws Exception {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	//历史任务办理人
	String taskAssignee="张三";
	// 使用办理人查询流程实例
	List<HistoricTaskInstance> list = processEngine.getHistoryService()//
	.createHistoricTaskInstanceQuery()//创建历史任务查询
	.taskAssignee(taskAssignee)//指定办理人查询历史任务
	.list();
	if(list!=null && list.size()>0){
	 for(HistoricTaskInstance task:list){
	 System.out.println("任务ID："+task.getId());
	 System.out.println("流程实例ID："+task.getProcessInstanceId());
	 System.out.println("任务的办理人："+task.getAssignee());
	 System.out.println("执行对象ID："+task.getExecutionId());
	 System.out.println(task.getStartTime()+"　"+task.getEndTime()+"　"+task.getDurationInMillis());
	}
	}
	}

	@Test
	public void queryHistoryProcessInstance() throws Exception {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	String processInstanceId = "10001";
	HistoricProcessInstance hpi = processEngine.getHistoryService()//
	    .createHistoricProcessInstanceQuery()//创建历史流程实例查询
	    .processInstanceId(processInstanceId)//使用流程实例ID查询
	    .singleResult();
	System.out.println("流程定义ID："+hpi.getProcessDefinitionId());
	System.out.println("流程实例ID："+hpi.getId());
	System.out.println(hpi.getStartTime()+"　　　　"+hpi.getEndTime()+"        "+hpi.getDurationInMillis());
	}

	
	/**设置流程变量*/
	@Test
	public void setVariables(){
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//获取执行的Service
	TaskService taskService = processEngine.getTaskService();
	//指定办理人
	String assigneeUser = "张三";
	//流程实例ID
	String processInstanceId = "20001";
	Task task = taskService.createTaskQuery()//创建任务查询
	.taskAssignee(assigneeUser)//指定办理人
	.processInstanceId(processInstanceId)//指定流程实例ID
	.singleResult();
	/**一：变量中存放基本数据类型*/
	taskService.setVariable(task.getId(), "请假人", "张无忌");//使用流程变量的名称和流程变量的值设置流程变量，一次只能设置一个值
	taskService.setVariable(task.getId(), "请假天数", 3);
	taskService.setVariable(task.getId(), "请假日期", new Date());
	/**二：变量中存放javabean对象，前提：让javabean对象实现implements java.io.Serializable*/
//	Person p = new Person();
//	p.setId(1);
//	p.setName("翠花");
//	taskService.setVariable(task.getId(), "人员信息", p);
	}

	
	/**获取流程变量*/
	@Test
	public void getVariables(){
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	//获取执行的Service
	TaskService taskService = processEngine.getTaskService();
	//指定办理人
	String assigneeUser = "张三";
	//流程实例ID
	String processInstanceId = "20001";
	Task task = taskService.createTaskQuery()//创建任务查询
	.taskAssignee(assigneeUser)//指定办理人
	.processInstanceId(processInstanceId)//指定流程实例ID
	.singleResult();
	/**一：变量中存放基本数据类型*/
	String stringValue = (String) taskService.getVariable(task.getId(), "请假人");
	Integer integerValue = (Integer) taskService.getVariable(task.getId(), "请假天数");
	Date dateValue = (Date) taskService.getVariable(task.getId(), "请假日期");
	System.out.println(stringValue+"     "+integerValue+"     "+dateValue);
	/**二：变量中存放javabean对象，前提：让javabean对象实现implements java.io.Serializable*/
	/**
	 * 获取流程变量时注意：流程变量如果是javabean对象，除了要求实现Serializable之外，
	 * 同时要求流程变量对象的属性不能发生发生变化，否则抛出异常
	 * 解决方案：在设置流程变量的时候，在javabean的对象中使用：
	 * private static final long serialVersionUID = -8065294171680448312L;
	 */
//	Person p = (Person)taskService.getVariable(task.getId(), "人员信息");
//	System.out.println(p.getId());
//	System.out.println(p.getName());
	}

	
	
}
