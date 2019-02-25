package com.yk.process.entity;

import com.alibaba.fastjson.JSON;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiPan on 2019/2/22.
 */
public class NodeConfig {
    private String processKey;
    private String businessKey;
    private List<TaskConfig> states;
    private List<FlowConfig> edges;
    private Map<String, NodeConfig> nodes = new LinkedHashMap<String, NodeConfig>();

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public List<TaskConfig> getStates() {
        return states;
    }

    public List<FlowConfig> getEdges() {
        return edges;
    }

    public Map<String, NodeConfig> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, NodeConfig> nodes) {
        this.nodes = nodes;
    }

    public NodeConfig(List<TaskConfig> states, List<FlowConfig> edges) {
        this.states = states;
        this.edges = edges;
    }

    public void setStates(List<TaskConfig> states) {
        this.states = states;
    }

    public void setEdges(List<FlowConfig> edges) {
        this.edges = edges;
    }

    public NodeConfig() {
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
