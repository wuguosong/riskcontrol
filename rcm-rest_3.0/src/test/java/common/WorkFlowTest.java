/**
 * 
 */
package common;

import java.util.List;

import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.bson.Document;
import org.junit.Test;
import org.springframework.util.Assert;

import com.google.gson.JsonArray;

import bpm.WorkFlow;
import util.DbUtil;

/**
 * @Description: 流程处理通用类
 * @Author zhangkewei
 * @Date 2016年8月24日 上午10:40:27  
 */
public class WorkFlowTest {
	
	WorkFlow wf = new WorkFlow();
	/**
	 * {
	 * 	processKey:流程业务ID(流程图中的process ID),
	 * 	currentTaskVar:{objectId:表单数据ID,opinion:提交意见,[key1:value1]},
	 * 	nextTaskVar:{
	 * 		inputUser:下一环节处理人,assigneeList:"多任务实例处理人1,多任务实例处理人2",submitBy:上一环节处理人中文名称,
	 * 		conditionName:conditionValue(该属性用来决策排它网管流转路径，conditionName表示条件名称,conditionValue表示条件值),
	 * 		[key1:value1]
	 *  }
	 * }
	 * currentTaskVar会绑定到当前任务上
	 * nextTaskVar会绑定到下一任务上
	 * assigneeList,conditionName 不是必填字段
	 * @param json
	 * @return
	 */
	@Test
	public void startProcess(){
		String str = 
					"{\n" +
					"  processKey:\"muliInstanceTaskTest\",\n" + 
					"  runtimeVar:{\n" + 
					"    objectId:\"业务ID1\",\n" + 
					"    assigneeList:[\"psxz1\",\"psxz2\"]\n" + 
					"  },\n" + 
					"  newTaskVar:{submitBy:\"fozzie\"}\n" + 
					"}";
		str = "{processKey:'preAssessment',runtimeVar:{inputUser:'kermit',inputUser1:'gaoluo',inputUser2:'zkw'}}";
		str = "{startVar:{processKey:'myProcess',inputUser:'kermit', businessId:'objid'},runtimeVar:{inputUser:'kermit'}}";
		str = 
				"{\n" +
						"startVar:{processKey:'preAssessment',businessId:'1111',subject:'2222',inputUser:'wangxx'},\n" + 
						"runtimeVar:{assigneeList:['wang']},\n" + 
						"currentTaskVar:{opinion:'请审批'}\n" + 
						"}";

		try {
			wf.startProcess(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void approve(){
		String str = 
				"{\n" +
				"  taskId:\"697508\",\n" + 
				"  runtimeVar:{inputUser:\"kermit\"},\n" + 
				"  currentTaskVar:{opinion:\"同意\"},\n" + 
				"  newTaskVar:{\n" + 
				"    submitBy:\"kermit\"\n" + 
				"  }\n" + 
				"}";
		try {
			wf.approve(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void reject(){
		String json = 
				"{\n" +
				"        taskId:'702503',\n" + 
				"        runtimeVar:{},\n" + 
				"        currentTaskVar:{opinion:'请修改'},\n" + 
				"        newTaskVar:{submitBy:'kermit',emergencyLevel:'一般'}\n" + 
				"    }";
		try {
			//wf.reject(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void getProcessDefinitionId(){
		try {
			String json = "{processDefinitionKey:\"preAssessmentNew\"}";
			Document doc = wf.getProcessDefinitionId(json);
			System.out.println(doc.toJson());
			
			json = "{processInstanceId:\"165009\"}";
			doc = wf.getProcessDefinitionId(json);
			System.out.println(doc.toJson());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getMyTask(){
		try {
			String json = "{currentPage:'1',pageSize:'10',queryObj:{assignee:'kermit'}}";
			PageAssistant page = wf.getMyTask(json);
			System.out.println(page.getTotalItems()+"------"+page.getList().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void completeProcess(){
		String json = 
				"{\n" +
				"    taskId:'335008',processInstanceId:'335001',processDefinitionId:'preAssessment:12:317504',\n" + 
				"    deleteVar:{reason:'xxxxxxxxxxxxxxx'}\n" + 
				"}";
		try {
			wf.completeProcess(json);
			
			ProcessInstance instance = wf.getProcessInstance(json);
			Assert.isNull(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getProcessInstanceApproveHistory(){
		String json = "{processInstanceId:'442501'}";
		JsonArray array = wf.getProcessInstanceApproveHistory(json);
		System.out.println(array.toString());
	}
	
	
	@Test
	public void approveWithNoticeEvent(){
		String str = 
				"{\n" +
				"  taskId:\"547508\",\n" + 
				"  runtimeVar:{inputUser:\"kermit\"},\n" + 
				"  currentTaskVar:{opinion:\"同意\"},\n" + 
				"  newTaskVar:{\n" + 
				"    submitBy:\"kermit\"\n" + 
				"  },\n" + 
				"  noticeInfo:{\n" +
				"    infoSubject:'infoSubject1111',\n" + 
				"    businessId:'businessId111',\n" + 
				"    remark:'remark111',\n" + 
				"    formKey:'formKey111',\n" + 
				"    createBy:'createBy111',\n" + 
				"    reader:'reader111',\n" + 
				"    type:'1'\n" + 
				"  }\n"+
				"}";
		
		try {
			DbUtil.init("configuration.xml", "db.properties");
			wf.approve(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
