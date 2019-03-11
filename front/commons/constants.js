define(['app'], function (app) {
    var srvUrl = "/rcm-rest";
    app
        .constant('BEWG_URL', {
                // 常量数据
            //决策评审总投资
            DICT_JC_PSZTZ:'JC_PSZTZ',
            //决策会委员
            DICT_DECISION_LEADERS:'DECISION_LEADERS',


           // 公用地址
            SelectGroupOrg: "fnd/Group/getOrg",
            SelectBusinessType: "businessDict/queryBusinessType.do",
            SelectProjectType: srvUrl+"common/commonMethod/queryAllProjectTypes",
            SelectSyncBusinessModel: "common/commonMethod/selectsyncbusinessmodel",
            SelecttDataDictionByCode: "common/commonMethod/selectDataDictionByCode",
            SelecttUserRoleByCode: "common/commonMethod/getRoleuserByCode",
            UploadFile: srvUrl + "uploadFile",
            UploadRcmFile: srvUrl + 'common/RcmFile/upload',

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
            SelectNotificationForViewById: srvUrl + "notificationFlatform/queryNotificationInfoForView.do",

            // 商业模式添加附件
            SelectAllBusinessModel: "rcm/BusinessModel/getAll",
            SelectAllBusiness: "rcm/ManageAttachment/listBusiness",
            SelectAllBusinessType: "rcm/ManageAttachment/SelectType",
            SelectAllBusinessAndAttachment: "rcm/ManageAttachment/ListBusinessAndAttachment",
            DeleteBusiness: "rcm/ManageAttachment/Delete",
            AddBusinessAttachment:"rcm/ManageAttachment/addAttachment",
            SelectAllManageAttachment: "rcm/ManageAttachment/getAll",

            // 预评审时间预警设置
            SelectAllPreWaringConfig: "projectPreReview/ListNotice/listWaringConfig",
            SelectAllFormalWaringConfig: "projectPreReview/ListNotice/listFormalWaringConfig",
            UpdateWaringConfig: "projectPreReview/ListNotice/updateWaringConfig",
            SaveWaringConfigInfo: "rcm/Warning/createWarning",
            UpdateWaringConfigInfo: "rcm/Warning/updateWarning",
            SelectPreWaringConfigById: "rcm/Warning/getWarningByID",

            // 风险案例
            SelectAllRiskGuidelines: srvUrl + "riskGuidelinesform/queryRiskGuidelines.do",
            DeleteRiskGuidelines: srvUrl+"riskGuidelinesform/deleteRiskGuideline.do",
            SelectAllSubmitRiskGuidelines: srvUrl + "riskGuidelinesform/queryRiskGuidelinesForSubmit.do",
            SelectRiskGuidelineById: srvUrl + "riskGuidelinesform/queryRideGuidelineInfo.do",
            SaveRiskGuideline: srvUrl +  "riskGuidelinesform/addRiskGuideline.do",
            UpdateRiskGuideline: srvUrl +  "riskGuidelinesform/modifyRiskGuideline.do",
            SumbitRiskGuideline: srvUrl + "riskGuidelinesform/submitRideGuideline.do",
            SelectRiskGuidelineByIdForView: srvUrl + "riskGuidelinesform/queryRideGuidelineInfoForView.do",

            // 评审小组管理
            SelectAllReviewTeams: "rcm/Pteam/getAll",
            SelectAllReviewTeamsForView: "rcm/Pteam/viewAll",
            DelectReviewTeam: "rcm/Pteam/deleteTeamByID",
            UpdateReviewTeam: "rcm/Pteam/updateTeam",
            SaveReviewTeam: "rcm/Pteam/createTeam",
            SelectReviewTeamById: "rcm/Pteam/getTeamByID",

            // 模板文件管理
            SelectAllTemplateFile: srvUrl + "templateFileFrom/queryTemplateFiles.do",
            DeleteAllTemplateFile: srvUrl+"templateFileFrom/deleteTemplateFile.do",
            SelectTemplateFileById: srvUrl + "templateFileFrom/queryTemalateFileInfo.do",
            SaveTemplateFile: srvUrl + "templateFileFrom/addTemplateFile.do",
            UpdateTemplateFile: srvUrl + "templateFileFrom/modifyTemplateFile.do",
            SubmitTemplateFile: srvUrl + "templateFileFrom/submitTemalateFile.do",
            SelectAllSubmitTemplateFile: srvUrl + "templateFileFrom/queryRiskGuidelinesForSubmit.do",

            // 	规章制度管理
            SelectAllRegulation: srvUrl + "regulationsFrom/queryRegulationsList.do",
            DeleteRegulation: srvUrl+"regulationsFrom/deleteRegulations.do",
            SelectAllSubmitRegulation: srvUrl + "regulationsFrom/queryRegulationsForSubmit.do",
            SaveRegulation: srvUrl + "regulationsFrom/addRegulations.do",
            UpdateRegulation: srvUrl + "regulationsFrom/modifyRegulations.do",
            SubmitRegulation: srvUrl + "regulationsFrom/submitRegulations.do",
            SelectRegulationById: srvUrl + "regulationsFrom/queryRegulationsInfo.do",

            // 投标评审申请
            SelectAllPre: srvUrl + "preInfo/queryPreList.do",
            SelectAllSubmitPre: srvUrl + "preInfo/queryPreSubmitedList.do",
            DeletePre: srvUrl + "preInfoCreate/deleteProject.do",
            SelectPreById: srvUrl + "preInfo/getPreByID.do",
            SelectPreAuditedLogsById: srvUrl + "preAudit/queryAuditedLogsById.do",
            SelectPre1ById: srvUrl + "preInfoCreate/getProjectByID.do",
            SavePre: srvUrl + "preInfoCreate/createProject.do",
            UpdatePre: srvUrl + "preInfoCreate/updateProject.do",

            // 正式评审申请
            SelectAllPfr: srvUrl + "formalAssessmentInfo/queryFormalAssessmentList.do",
            SelectAllSubmitPfr: srvUrl + "formalAssessmentInfo/queryFormalAssessmentSubmitedList.do",
            DeletePfr: "formalAssessmentInfoCreate/deleteProject.do",
            SelectPfrById: srvUrl + "formalAssessmentInfo/getFormalAssessmentByID.do",
            SelectPfrAuditedLogsById: srvUrl + "formalAssessmentAudit/queryAuditedLogsById.do",
            SelectPfr1ById: srvUrl + "formalAssessmentInfoCreate/getProjectByID.do",
            SavePfr: srvUrl + "formalAssessmentInfoCreate/createProject.do",
            UpdatePfr: srvUrl + "formalAssessmentInfoCreate/updateProject.do",
            SelectPfrTaskOpinion: "rcm/ProjectInfo/getTaskOpinion",
            SelectPfrTaskOpinionTwo: "rcm/ProjectInfo/getTaskOpinionTwo",

            // 其他评审申请
            SelectBulletinInfo: srvUrl + "bulletinInfo/queryListDefaultInfo.do",
            SelectAllBulletin: srvUrl + "bulletinInfo/queryApplyList.do",
            SelectAllSubmitBulletin: srvUrl + "bulletinInfo/queryApplyedList.do",
            DeleteBulletin: srvUrl + "bulletinInfo/deleteByIds.do",
            StartBatchFlow: srvUrl+"bulletinAudit/startBatchFlow.do",
            SelectBulletinById: srvUrl + "bulletinInfo/queryViewDefaultInfo.do",
            SelectById: srvUrl + "bulletinInfo/queryCreateDefaultInfo.do",
            SelectBulletin1ById: srvUrl + "bulletinInfo/queryUpdateDefaultInfo.do",
            SaveOrUpdateBulletin: srvUrl + "bulletinInfo/saveOrUpdate.do",

            // 投标评审报告
            SelectAllReportPre: srvUrl + "preAuditReport/queryUncommittedReportByPage.do",
            SelectAllSubmitReportPre: srvUrl + "preAuditReport/querySubmittedReportByPage.do",
            DeleteReportPre: srvUrl + "preAuditReport/batchDeletePreReport.do",
            SelectNotNewlyReportPre: srvUrl + "preAuditReport/queryNotNewlyPreAuditProject.do",
            SelectPreProjectById: srvUrl + "preAuditReport/getPreProjectFormalReviewByID.do",
            SaveReportPre: srvUrl + "preAuditReport/saveReviewReportById.do",
            UpdateReportPre: srvUrl + "preAuditReport/updateReviewReport.do",
            SubmitReportPre: srvUrl +  "preAuditReport/submitAndupdate.do",
            SelectReportPreById: srvUrl + "preAuditReport/getById.do",
            CreateReportPreWord: srvUrl + "preAuditReport/getPreWordReport.do",
            IsSubmitReportPre: srvUrl + "preAuditReport/isPossible2Submit.do",
            SelectReportPreView: srvUrl + "formalReport/selectPrjReviewView.do",

            // 投标评审提交决策会材料
            SelectAllBiddlingPre: srvUrl + "preBidding/queryUncommittedByPage.do",
            SelectAllSubmitBiddlingPre: srvUrl + "preBidding/querySubmittedByPage.do",
            SelectBiddlingPreById: srvUrl + "preBidding/getByBusinessId.do",
            SaveBiddingPre: srvUrl+"preBidding/addPolicyDecision.do",

            // 正式评审报告
            SelectAllReportPfr: srvUrl + "formalReport/queryUncommittedReportByPage.do",
            SelectAllSubmitReportPfr: srvUrl + "formalReport/querySubmittedReportByPage.do",
            DeleteReportPfr: srvUrl + "formalReport/batchDeleteUncommittedReport.do",
            ExportReportPfrWord: srvUrl + "formalReport/exportReportInfo.do",
            SelectNotNewlyReportPfr: srvUrl + "formalReport/queryNotNewlyBuiltProject.do",
            SelectPfrProjectById: srvUrl + "formalReport/selectPrjReviewView.do",
            SaveReportPfr: srvUrl + "'formalReport/createNewReport.do",
            UpdateReportPfr: srvUrl + "formalReport/updateReport.do",
            IsExistReportPfr: srvUrl + "formalReport/isReportExist.do",
            SubmitReportPfr: srvUrl +  "formalReport/submitAndupdate.do",
            SelectReportPrfrById: srvUrl + "formalReport/getByID.do",
            CreateReportPfrWord: srvUrl + "formalReport/getPfrAssessmentWord.do",
            IsSubmitReportPfr: srvUrl + "formalReport/isPossible2Submit.do",
            SelectReportPfrView: srvUrl + "formalReport/getProjectFormalReviewByID.do",

            // 正式评审提交决策会材料
            SelectAllBiddlingPfr: srvUrl + "formalReport/queryUncommittedDecisionMaterialByPage.do",
            SelectAllSubmitBiddlingPfr: srvUrl + "formalReport/querySubmittedDecisionMaterialByPage.do",
            SelectFormalAndReport: srvUrl + "formalReport/findFormalAndReport.do",
            SaveBiddingPfr: srvUrl + "formalReport/addPolicyDecision.do",
            SelectNoticeDecstionById: srvUrl + "noticeDecisionInfo/getNoticeDecstionByBusinessId.do",
            SelectMarkById: srvUrl + "formalMark/queryMarks.do",
            SaveFormalMarkPfr: srvUrl + "formalMark/saveOrUpdate.do",
            SaveConferenceInformation: srvUrl + "information/addConferenceInformation.do",
        });
});