package com.yk.rcm.formalAssessment.listener;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import com.yk.flow.util.JsonUtil;

/**
 * 
 * @author yaphet
 * 
 * 2017年6月5日
 *
 */
@Component
public class MultiTaskWatchListener implements ExecutionListener, TaskListener {

	private static final long serialVersionUID = 1L;


	@Override
	public void notify(DelegateExecution execution) throws Exception {
		//当前的流程选项
//		String sequenceFlow = (String) execution.getVariable("sequenceFlow");
		//默认的路线编号
//		String defaultSequenceFlow = (String) execution.getVariable("defaultSequenceFlow");
//		//
//		if(!defaultSequenceFlow.equals(sequenceFlow)){
//			//否决线路
//			execution.setVariable("defaultDisFlow", sequenceFlow);
//		}
//		//剩余人数
//		int nrOfActiveInstances = (Integer) execution.getVariable("nrOfActiveInstances");
//		if(nrOfActiveInstances == 0){
//			String defaultDisFlow = (String) execution.getVariable("defaultDisFlow");
//			execution.setVariable("sequenceFlow", defaultDisFlow==null?defaultSequenceFlow:defaultDisFlow);
//		}
//		int parseInt = Integer.parseInt("aa");
	}
	@Override
	public void notify(DelegateTask delegateTask) {
		
		String json = delegateTask.getDescription();
		//默认路线
		String flowId = (String) JsonUtil.fromJson(json, Map.class).get("defaultFlow");
		
		delegateTask.setVariable("defaultSequenceFlow", flowId);
		
		//int parseInt = Integer.parseInt("aa");
	}
}
