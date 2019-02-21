package com.yk.process.entity;

import com.alibaba.fastjson.JSON;

/**
 * Created by LiPan on 2019/2/21.
 */
public class FlowConfig {
    private String id;// id
    private String name;// 名称
    private TaskConfig from;// 来节点
    private TaskConfig to;// 去节点
    private String status;// 节点状态

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

    public TaskConfig getFrom() {
        return from;
    }

    public void setFrom(TaskConfig from) {
        this.from = from;
    }

    public TaskConfig getTo() {
        return to;
    }

    public void setTo(TaskConfig to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
