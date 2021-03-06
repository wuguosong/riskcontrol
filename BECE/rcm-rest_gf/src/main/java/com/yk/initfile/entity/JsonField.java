package com.yk.initfile.entity;

/**
 * Created by Administrator on 2019/5/30 0030.
 */
public interface JsonField {
    String name = "name";// 描述
    String table = "table";// 表名
    String jsonLevel = "jsonLevel";// 附件层级
    String filePathField = "filePathField";// 附件路径字段名
    String fileNameField = "fileNameField";// 附件名称字段名
    String cloudParams = "cloudParams";// 云库参数
    String type = "type";// 云库存储附件类型
    String code = "code";// 云库存储附件编码
    String locations = "locations";// FileDto中的文件页面位置
    String mongoId = "mongoId";// 表主键ID
    String _location = "_location";
    String version = "version";
    /*=========上会附件相关参数常量========*/
    String dataName = "dataName";
    String dataTable = "dataTable";
    String fileTable = "fileTable";
    String ITEM_NAME = "ITEM_NAME";
    String fileName = "fileName";
    String filePath = "filePath";
    String projectFormalId = "projectFormalId";
    String policyDecision = "policyDecision";
    String decisionMakingCommitteeStaffFiles = "decisionMakingCommitteeStaffFiles";
    String update = "update";// 是否同步，主表中的上会标记是否存在或者为1
    String exist = "exist";// 是否存在，主表中的文件和当前查询到的是否保持一致
    String attachmentList = "attachmentList";
    String _id = "_id";
    String $oid = "$oid";
    String isMettingAttachment = "isMettingAttachment";
}
