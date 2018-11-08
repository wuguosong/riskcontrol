package com.yk.rcm.pre.listener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.bson.Document;
import org.springframework.stereotype.Component;

import com.yk.common.IBaseMongo;
import common.Constants;


@Component
public class QueryTaskMajorMembersListener implements ExecutionListener{

	private static final long serialVersionUID = 1L;

	@Resource
	private IBaseMongo baseMongo;
	
	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		
		String businessKey = execution.getProcessBusinessKey();
		
		Map<String, Object> preMongo = baseMongo.queryById(businessKey, Constants.RCM_PRE_INFO);
		// TODO 需要根据正式评审的相关字段处理专业评审人员及数据结构、模板；
		Map<String, Object> taskallocation = (Map<String, Object>) preMongo.get("taskallocation");
		
		List<Document> majorMember = (List<Document>) taskallocation.get("majorMember");
		
		List<String> majorMembersList = new ArrayList<String>();
		
		for (Document ll : majorMember) {
			String id = ll.getString("VALUE");
			majorMembersList.add(id);
		}
		execution.setVariable("majorMembers", majorMembersList);
	}

}
