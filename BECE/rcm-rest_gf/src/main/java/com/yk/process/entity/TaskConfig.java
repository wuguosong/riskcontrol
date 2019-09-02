package com.yk.process.entity;

import com.alibaba.fastjson.JSON;
import org.activiti.bpmn.model.FlowElement;

/**
 * ExclusiveGateway、UserTask、StartEvent、EndEvent、InclusiveGateway在这里都被认为是一个TaskConfig，SequenceFlow不包含其中
 * Created by LiPan on 2019/2/21.
 */
public class TaskConfig {
    private String process;// 流程定义ID
    private Long deployment;// 部署ID
    private Integer index;// 节点顺序
    private String id;// 节点ID
    private String name;// 节点名称
    private String type;// 节点类型
    private String status;// 节点状态
    private FlowElement init;// 原始信息

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public Long getDeployment() {
        return deployment;
    }

    public void setDeployment(Long deployment) {
        this.deployment = deployment;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FlowElement getInit() {
        return init;
    }

    public void setInit(FlowElement init) {
        this.init = init;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
