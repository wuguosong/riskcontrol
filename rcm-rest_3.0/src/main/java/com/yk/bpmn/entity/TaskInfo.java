package com.yk.bpmn.entity;

import java.util.List;

import org.activiti.bpmn.model.SequenceFlow;

public class TaskInfo {
	
	private String taskId;
	private String description;
	private String taskKey;
	private String assignee;
	private String formKey;
	private String executionId;
	
	public String getExecutionId() {
		return executionId;
	}
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	private List<SequenceFlow> outSequences;
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTaskKey() {
		return taskKey;
	}
	public void setTaskKey(String taskKey) {
		this.taskKey = taskKey;
	}
	public List<SequenceFlow> getOutSequences() {
		return outSequences;
	}
	public void setOutSequences(List<SequenceFlow> outSequences) {
		this.outSequences = outSequences;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getFormKey() {
		return formKey;
	}
	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}
	
}
