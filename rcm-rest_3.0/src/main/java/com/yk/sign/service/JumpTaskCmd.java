package com.yk.sign.service;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.task.Comment;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/5/27 0027.
 * 驳回到任务的某个节点，不适用并行网关
 */
public class JumpTaskCmd implements Command<Comment> {
    protected String executionId;// 当前任务运行实例Id
    protected String activityId;// 要跳转的任务Key
    private List<String> tasks = new ArrayList<String>();// 跳转后的任务实例Id

    public JumpTaskCmd(String executionId, String activityId) {
        this.executionId = executionId;
        this.activityId = activityId;
    }

    public List<String> getTasks() {
        return tasks;
    }

    @Override
    public Comment execute(CommandContext commandContext) {
        for (TaskEntity taskEntity : Context.getCommandContext().getTaskEntityManager().findTasksByExecutionId(executionId)) {
            Context.getCommandContext().getTaskEntityManager().deleteTask(taskEntity, "jump", false);
        }
        ExecutionEntity executionEntity = Context.getCommandContext().getExecutionEntityManager().findExecutionById(executionId);
        ProcessDefinitionImpl processDefinition = executionEntity.getProcessDefinition();
        ActivityImpl activity = processDefinition.findActivity(activityId);
        executionEntity.executeActivity(activity);
        List<TaskEntity> list = executionEntity.getTasks();
        if(CollectionUtils.isNotEmpty(list)){
            for(TaskEntity task : list){
                this.tasks.add(task.getId());
            }
        }
        return null;
    }
}
