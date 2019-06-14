package common;

import util.PropertiesUtil;

/**
 * 常量
 * 
 * @author zhouyoucheng
 *
 */
public class Constants {

	public static final String S = "S";// 成功
	public static final String E = "E";// 异常
	public static final String R = "R";// 错误
	public static final String WHERE = "where";
	public static final String SET = "set";
	public static final String DATA = "data";
	public static final String _ID = "_id";
	public static final String UTF8 = "UTF-8";
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String OPTIONS = "OPTIONS";
	public static final String CREATION_DATE = "creation_date";
	public static final String LAST_UPDATE_DATE = "last_update_date";
	
	//上传、下载文件路径地址
	public static final String UPLOAD_DIR = PropertiesUtil.getProperty("rootDir");

	// 鉴权相关
	public static final String AUTHORIZATION = "authorization";
	public static final String SYS_USER = "sys_user";
	public static final String USER_CODE = "user_code";
	public static final String PASSWORD = "password";
	public static final String BASIC = "Basic ";
	
	//流程相关
	public static final String PRE_ASSESSMENT = "preAssessment";
	public static final String FORMAL_ASSESSMENT = "formalAssessment";
	public static final String PROCESS_KEY = "processKey";
	public static final String BUSINESS_ID = "businessId";
	public static final String START_VAR = "startVar";
	public static final String CURRENTTASK_VAR = "currentTaskVar";
	public static final String RUNTIME_VAR = "runtimeVar";
	public static final String NEWTASK_VAR = "newTaskVar";
	
	public static final String FORMAL_NOTICEOFDECSTION = "noticeOfDecision";
	
	//数据字典
	public static final String DIC_RELATION_TYPE="07";
	
	//角色字典
	public static final String ROLE_SYSTEM_ADMIN="1"; //系统管理员
	public static final String ROLE_MEETING_ADMIN="3"; //会议管理员
	public static final String TASK_ASSIGNEE_MANAGER="5";//任务分配人|风控分管领导
	public static final String DECISION_MAKING_STAFF="2";//决策委员会委员
	public static final String ROLE_FKFGLEADER="9";//风控负责签发的领导
	public static final String ROLE_TBSX_ASSIGNEE_MANAGER="TBSX_FENGKONG";//通报事项_风控审核角色
	/**
	 * 法律分配角色
	 */
	public static final String ROLE_FORMAL_LEGALTASK="FORMAL_LEGALTASK";
	/**
	 * 决策项目类型
	 */
	public static final String DICT_CODE_BUSINESSTYPE = "14";
	
	
	
	
	
	//数据库对应表
	public static final String PREASSESSMENT="pre_assessment"; //预评审
	public static final String RCM_PROJECTFORMALREVIEW_INFO="rcm_projectFormalReview_info"; //正式评审
	public static final String FORMAL_MEETING_INFO="formal_meeting_info";//会议列表
	public static final String FORMAL_MEETING_NOTICE="formal_meeting_notice";//会议通知列表
	public static final String RCM_FORMALREPORT_INFO="rcm_formalReport_info";//正式评审报告列表
	
	public static final String RCM_FEEDBACK_INFO="rcm_Feedback_info";//投标结果反馈列表
	public static final String RCM_PROJECTEXPERIENCE_INFO="rcm_projectExperience_info";//项目经验总结列表
	public static final String RCM_NOTICEOFDECISION_INFO="rcm_noticeOfDecision_info";//决策通知书列表
	public static final String RCM_NOTICEDECISION_INFO="rcm_noticeDecision_info";//决策通知书列表 //新表
	/**
	 * 正式评审表名oracle,mongo
	 * 新表
	 */
	public static final String RCM_FORMALASSESSMENT_INFO="rcm_formalAssessment_info";
	/**
	 * 预评审oracle，mongo
	 */
	public static final String RCM_PRE_INFO = "rcm_pre_info";
	/**
	 * 通报事项信息
	 */
	public static final String RCM_BULLETIN_INFO = "rcm_bulletin_info";
	
	
	/**
	 * 通报事项类型code
	 */
	public static final String TBSX_TYPE = "TBSX_BUSINESS";
	
	/**
	 * 通报事项流程key
	 */
	public static final String PROCESS_KEY_BULLETIN = "bulletin";
	/**
	 * 决策通知书流程key
	 */
	public static final String PROCESS_KEY_NOTICEDECISION = "noticeDecision";
	/**
	 * 正式评审书流程key
	 */
	public static final String PROCESS_KEY_FormalAssessment = "formalReview";
	/**
	 * 预评审流程key
	 */
	public static final String PROCESS_KEY_PREREVIEW = "preReview";
	/**
	 * 北控水务（中国）code
	 */
	public static final String SYS_ORG_CODE_ROOT = "000001";
	/**
	 * 东部战区code
	 */
	public static final String SYS_ORG_CODE_EAST = "910001";
	/**
	 * 西部战区code
	 */
	public static final String SYS_ORG_CODE_WEST = "940001";
	/**
	 * 南部战区code
	 */
	public static final String SYS_ORG_CODE_SOUTH = "930001";
	/**
	 * 北部战区code
	 */
	public static final String SYS_ORG_CODE_NORTH = "920001";
	/**
	 * 中部战区code
	 */
	public static final String SYS_ORG_CODE_CENTER = "950001";
	
	/**
	 * 会议主席
	 */
	public static final String DICT_MEETING_LEADER = "MEETING_LEADER";
	
	/**
	 * 接口类型
	 */
	public static final String DICT_INTERFACE_TYPE = "INTERFACE_TYPE";	
	
	/**
	 * 接口类型-档案系统
	 */
	public static final String DICT_INTE_TYPE_DAXT = "INTE_TYPE_DAXT";	
	
	/**
	 * 总裁办-会议管理员
	 */
	public static final String ROLE_CODE_MEETING_ADMIN = "3";
	
	/**
	 * 会议主持人
	 */
	public static final String ROLE_CODE_MEETING_PERSON = "MEETING_PERSON";
	
	/**
	 * 正式评审决策委员会委员
	 */
	public static final String ROLE_CODE_DECISION_LEADERS = "DECISION_LEADERS";
	
	/**
	 * 角色代码：业务评审负责人角色
	 */
	public static final String ROLE_CODE_REVIEWFZR = "14";
	
	/**
	 * 角色代码：法律评审负责人角色
	 */
	public static final String ROLE_CODE_LEGALFZR = "LAW_HEAD_OF_REVIEW";
	
	/**
	 * 角色代码：投资经理
	 */
	public static final String ROLE_CODE_INVESTMENT_MANAGER = "6";
	
	/**
	 * 角色代码：风控数据权限
	 */
	public static final String ROLE_CODE_RISK_DATA = "RISK_DATA";
	
	/**
	 * TEAM代码：法律评审负责人
	 */
	public static final String TEAM_TYPE_LEGALFZR = "2";
	
	/**
	 * TEAM代码：业务评审负责人
	 */
	public static final String TEAM_CODE_REVIEWFZR = "1";
	
	
	/**
	 * 文件类型代码：其他文件
	 * */
	public static final String FILE_CODE_OTHER = "0509";
	
	/**
	 * 消息借口-短信类型
	 * 必填参数：
	 * content:短信内容
	 * target:短信接收人手机号
	 */
	public static final String MESSAGE_TYPE_SMS = "SMS";
	/**
	 * 消息借口-钉钉链接类型
	 *  必填参数：
	 * 	touser或toparty必填一个
	 * 	messageUrl:链接消息指向的URL
	 * 	picUrl:链接消息显示的图标
	 * 	text:链接消息显示的文本
	 * 	title:链接消息显示的标题
	 */
	public static final String MESSAGE_TYPE_DT_LINK = "DT_LINK";
	/**
	 * 消息借口-钉钉文本类型
	 * 必填参数：
	 * touser或toparty必填一个
	 * content:文本消息内容
	 */
	public static final String MESSAGE_TYPE_DT_TEXT = "DT_TEXT";
	/**
	 * 消息借口-微信文本类型
	 * 必填参数：
	 * touser或toparty必填一个
	 * content:文本消息内容
	 */
	public static final String MESSAGE_TYPE_WX_TEXT = "WX_TEXT";
	/**
	 * 消息借口-邮箱类型
	 * 必填参数：
	 * sender:发送方Email
	 * target:接收方Email
	 * title:邮件标题
	 * content:消息内容
	 * 
	 */
	public static final String MESSAGE_TYPE_EMAIL = "EMAIL";
	
	/**
	 * author: Sam Gao
	 * date: 2018-12-05
	 * 风控评审意见汇总
	 * 必填参数：
	 * _id:意见汇总id
	 * type:项目类型
	 * status:意见状态
	 * content:内容
	 * 
	 */
	public static final String RCM_FORMAL_SUMMARY = "rcm_formal_summary";
	
	/**
	 * author: Sunny Qi
	 * date: 2019-06-12
	 * 历史数据Mongo表
	 * 必填参数：
	 * _id:历史数据id-业务id
	 * 
	 */
	public static final String RCM_HISTORYDATA_INFO = "rcm_historyData_info";
}
