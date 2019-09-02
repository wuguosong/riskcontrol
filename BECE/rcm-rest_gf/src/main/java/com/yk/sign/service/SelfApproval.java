package com.yk.sign.service;

import com.yk.common.SpringUtil;
import com.yk.flow.service.IBpmnAuditService;
import com.yk.flow.util.ProcessResult;
import com.yk.process.service.IProcessService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 自审批
 */
public class SelfApproval{
    private static final Logger logger = LoggerFactory.getLogger(SelfApproval.class);
    public ProcessResult<ProcessInstance> startFlow(String processKey, String businessId, Map<String, Object> variables) {
        IBpmnAuditService bpmnAuditService = (IBpmnAuditService)SpringUtil.getBean("bpmnAuditService");
        ProcessResult<ProcessInstance> processInstanceProcessResult = bpmnAuditService.startFlow(processKey, businessId, variables);
        /*=====成功以后才进行自审批，且自审批状态不能影响提交状态=====*/
        try{
            if (processInstanceProcessResult.isSuccess()) {
                if(variables == null){
                    variables = new HashMap<String, Object>();
                }
                ISignService signService = (ISignService)SpringUtil.getBean("signService");
                TaskService taskService = (TaskService)SpringUtil.getBean("taskService");
                IProcessService processService = (IProcessService)SpringUtil.getBean("processService");
                if (!StringUtils.isAnyBlank(processKey, businessId)) {
                    // 校验下一步是否可以自审批
                    HashMap<String, Object> validate = signService.validateSign(processKey, businessId);
                    if (validate.get("code") == null) {
                        // 获取当前任务
                        HashMap<String, Object> curTaskMap = signService.getCurrentTask(processKey, businessId);
                        if (curTaskMap != null) {
                            String curTaskId = String.valueOf(curTaskMap.get("TASKID"));
                            Task curTask = taskService.createTaskQuery().taskId(curTaskId).singleResult();
                            String curTaskKey = curTask.getTaskDefinitionKey();
                            String nexTaskKey = signService.getNextTaskInfo(processKey, businessId).getKey();
                            Map<String, Object> variable = processService.getNextTaskFlowElementVariable(curTask.getProcessDefinitionId(), curTaskKey, nexTaskKey);
                            // 结束本次任务,开始下个节点
                            variables.put("opinion", "系统自动审批");
                            variables.putAll(variable);
                            taskService.complete(curTaskId, variables);
                            logger.info("系统自动审批成功！");
                        }
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            logger.error("系统自动审批失败：" + ex.getMessage());
        }
        /*=====成功以后才进行自审批，且自审批状态不能影响提交状态=====*/
        return processInstanceProcessResult;
    }
}
