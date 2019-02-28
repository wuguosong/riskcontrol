define(['app'], function (app) {
    var srvUrl = "/rcm-rest";
    app
        .constant('BEWG_URL', {
            // 登录
            LoginUrl: srvUrl + "login/login",

            // 数据字典
            SaveOrUpdateDict : srvUrl + "dict/saveOrUpdateDictType.do",

            // 用户管理
            SelectAllUser: srvUrl + "user/getAll.do",
            SelectUserById: srvUrl + "user/getSysUserByID.do",
            SelectAllRoleUser : srvUrl + "user/getAllRole.do",
            SelectUserRole : srvUrl + "user/getRoleByUserId.do",
            SaveOrUpdateUserRole : srvUrl + "user/saveUserRole.do",
            SaveUser: "fnd/SysUser/createSysUser",
            UpdateUser: "fnd/SysUser/updateSysUser",

            // 菜单管理
            SelectAllSysFun: "fnd/SysFunction/getAll",
            SelectSysFunById: "fnd/SysFunction/getSysFunctionByID",
            SelectAllOrgSysFun: "fnd/SysFunction/getOrg",
            UpdateSysFunStateById: "fnd/SysFunction/updateSysFunctionState",
            SelectSysFunCode: "fnd/SysFunction/SelectCode",
            DeleteSysFunById: "fnd/SysFunction/delectSysFunctionByID",
            SaveSysFun: "fnd/SysFunction/createSysFunction",
            UpdateSysFun: "fnd/SysFunction/updateSysFunction",
            ValidateSysFunForm : "fnd/SysFunction/Validate",

            // 终止流程
            SelectProjectListEndFlow: srvUrl + "bpmn/queryProjectListByPage.do",
            doEndFlow: srvUrl + "bpmn/endFlow.do",

            // 流程人员变更
            SelectTaskPerson: srvUrl + "bpmn/getTaskPerson.do",
            SelectPfrByIdChangeBpmnUser: srvUrl + "formalAssessmentInfo/getFormalAssessmentByID.do",
            SelectPreByIdChangeBpmnUser: srvUrl + "preInfo/getPreByID.do",
            SelectPfrAuditLogsByIdChangeBpmnUser: srvUrl + "formalAssessmentAudit/queryAuditedLogsById.do",
            SelectPreAuditLogsByIdChangeBpmnUser: srvUrl + "preAudit/queryAuditedLogsById.do",
            SelectBulletinAuditLogsByIdChangeBpmnUser: srvUrl + "bulletinInfo/queryViewDefaultInfo.do",
            UpdateAuditUser: srvUrl + "bpmn/changeAuditUser.do",

            // 流程控制
            DeployBpmn: srvUrl + "bpmn/deploy.do",
            StopProcessBpmn: srvUrl + "bpmn/stopProcess.do",
            ClearBpmn: srvUrl + "bpmn/clear.do",
            RepeatCallByNoticeIdBpmn: srvUrl + "wscall/repeatCallByNoticeId.do",
            InitReportStatusBpmn: srvUrl + "wscall/initReportStatus.do",
            SendTaskBpmn: srvUrl + "wscall/sendTask.do",
            InitWithJsonBpmn: srvUrl + "wscall/initWithJson.do",

            // 错误日志
            SelectAllJournal: srvUrl + "journal/queryByPage.do",
            SelectJournalById: srvUrl + "journal/queryById.do",
            SelectJournalDetailById: "exception/Journal/getByID",

            // 接口重调
            SelectAllWscall: srvUrl + "wscall/queryByPage.do",
            SelectRepeatCallOneWscall: srvUrl + "wscall/repeatCallOne.do",
            SelectWscallById: srvUrl + "wscall/queryById.do",

            // 修改项目名称
            SelectAllProject: srvUrl + "updateProjectName/queryAllProject.do",
            UpdateProjectName: srvUrl + "updateProjectName/updateProject.do",

            // 平台公告
            SelectAllNotification: srvUrl + "notificationFlatform/queryNotifications.do",
            SelectDictItemByCodeNotification: srvUrl + "dict/queryDictItemByDictTypeCode.do",
            DeleteNotification: srvUrl + "notificationFlatform/deleteNotifications.do",
            SelectNotificationById: srvUrl + "notificationFlatform/queryNotificationInfo.do",
            SaveNotification: srvUrl + "notificationFlatform/addNotification.do",
            UpdateNotification: srvUrl + "notificationFlatform/modifyNotification.do",
            SubmitNotification: srvUrl + "notificationFlatform/submitNotification.do",
            UploadFileNotification: srvUrl + "file/uploadFile.do",
            SelectNotificationForViewById: srvUrl + "notificationFlatform/queryNotificationInfoForView.do",

            // 商业模式添加附件
            SelectAllBusinessModel: "rcm/BusinessModel/getAll",
            SelectAllBusiness: "rcm/ManageAttachment/listBusiness",
            SelectAllBusinessType: "rcm/ManageAttachment/SelectType",
            SelectAllBusinessAndAttachment: "rcm/ManageAttachment/ListBusinessAndAttachment",
            DeleteBusiness: "rcm/ManageAttachment/Delete",
            AddBusinessAttachment:"rcm/ManageAttachment/addAttachment",
            SelectAllManageAttachment: "rcm/ManageAttachment/getAll",
        });
});