package com.yk.process.entity;

/**
 * Created by LiPan on 2019/2/21.
 */
public interface ProcConst {
    /**
     * 常量来自于流程XML文件
     **/
    public static final String FLOW_ELEMENTS = "flowElements";
    public static final String OUTGOING_FLOWS = "outgoingFlows";
    public static final String INCOMING_FLOWS = "incomingFlows";
    public static final String SOURCE_REF = "sourceRef";
    public static final String TARGET_REF = "targetRef";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String EXCLUSIVE_GATEWAY = "ExclusiveGateway";
    public static final String INCLUSIVE_GATEWAY = "InclusiveGateway";
    public static final String USER_TASK = "UserTask";
    public static final String START_EVENT = "StartEvent";
    public static final String END_EVENT = "EndEvent";
    public static final String SEQUENCE_FLOW = "SequenceFlow";
    /**
     * 常量自定义
     **/
    public static final String AGENCY = "Agency";// 代阅
    public static final String ALREADY = "Already";// 已阅
    public static final String STATES = "states";// 节点集合名
    public static final String EDGES = "edges";// 流向集合名
}
