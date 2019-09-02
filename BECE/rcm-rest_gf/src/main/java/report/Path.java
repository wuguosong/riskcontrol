package report;

import java.util.Calendar;

import util.PropertiesUtil;
import util.Util;

public final class Path {
	public static String TEMPLATE_FILE="";
	/**
	 * 正式运行中模板附件取编译后的地址
	 * UnitTest 请取src/main/resource/Template/ 中地址
	 * */
	//XXX private static String TEMPLATE_FILE="src/main/resource/Template/";
	static {
		TEMPLATE_FILE=Path.class.getResource("/Template").getPath();
	}

	public static String TEST_EXPORT = TEMPLATE_FILE + "testExport.docx";
	
	public static String PRE_ASSESSMENT_REPORT = TEMPLATE_FILE + "PreAssessmentReport.docx";
	public static String PRE_APPLICATION = TEMPLATE_FILE + "PreApplication.docx";
	
	public static String FORMAL_ASSESSMENT_REPORT_TecTransform = TEMPLATE_FILE + "TecTransformReport.docx";
	public static String FORMAL_ASSESSMENT_REPORT_DropOut = TEMPLATE_FILE + "DropOutReport.docx";
	public static String FORMAL_ASSESSMENT_REPORT_HazardousWaste = TEMPLATE_FILE + "HazardousWasteReport.docx";
	public static String FORMAL_ASSESSMENT_REPORT_EquityAcquisition = TEMPLATE_FILE + "EquityAcquisitionReport.docx";
	public static String FORMAL_ASSESSMENT_REPORT_Supplement = TEMPLATE_FILE + "SupplementReport.docx";
	public static String FORMAL_ASSESSMENT_REPORT_FormalReview = TEMPLATE_FILE + "FormalReviewReport.docx";
	public static String FORMAL_ASSESSMENT_REPORT_BTPPP = TEMPLATE_FILE + "BTppp.docx";
	public static String FORMAL_ASSESSMENT_REPORT_BOTSupply = TEMPLATE_FILE + "BOTsupply.docx";
	public static String FORMAL_ASSESSMENT_REPORT_BOTSewage = TEMPLATE_FILE + "BOTsewage.docx";
	public static String FORMAL_ASSESSMENT_REPORT_TOTSupply = TEMPLATE_FILE + "TOTsupply.docx";
	public static String FORMAL_ASSESSMENT_REPORT_TOTSewage = TEMPLATE_FILE + "TOTsewage.docx";
	public static String FORMAL_ASSESSMENT_REPORT_sanitation = TEMPLATE_FILE + "sanitation.docx";
	public static String FORMAL_ASSESSMENT_REPORT_EntrustedManagement = TEMPLATE_FILE + "EntrustedManagement.docx";
	public static String FORMAL_APPLICATION = TEMPLATE_FILE + "FormalApplication.docx";
	public static String SUPPLEMENT_FORMAL_APPLICATION = TEMPLATE_FILE + "FormalApplication_supplement.docx";
	/**
	 * 决策通知书报告模板路径
	 * see NoticeOfDecision_XML,NoticeOfDecision_DOCX
	 */
	@Deprecated
	public static String NoticeOfDecision = TEMPLATE_FILE + "NoticeOfDecision.docx";
	
	public static String NoticeOfDecision_XML = "NoticeOfDecision.xml";
	public static String NoticeOfDecision_DOCX = "NoticeOfDecision.docx";
	
	/**
	 * 预评审报告存储路径
	 */
	public static String preAssessmentReportPath(){
		return getPath(PropertiesUtil.getProperty("preAssessmentReportPath"));
	}
	/**
	 * 正式评审报告存储路径
	 */
	public static String formalAssessmentReportPath(){
		return getPath(PropertiesUtil.getProperty("formalAssessmentReportPath"));
	}
	/**
	 * 决策通知书申请单生成word文档存储路径
	 */
	public static String noticeOfDecisionReportPath(){
		return getPath(PropertiesUtil.getProperty("noticeOfDecisionReportPath"));
	}
	/**
	 * 决策通知书最终领导签字后的文件
	 */
	public static String noticeDecisionFinalPath(){
		return getPath(PropertiesUtil.getProperty("noticeDecisionFinalPath"));
	}
	/**
	 * 预评审附件存储路径
	 */
	public static String preAttachmentPath(){
		return getPath(PropertiesUtil.getProperty("preAssessmentPath"));
	}
	
	/**
	 * 正式评审附件存储路径
	 */
	public static String formalAttachmentPath(){
		return getPath(PropertiesUtil.getProperty("formalAssessmentPath"));
	}
	
	/**
	 * 会议纪要存储路径
	 */
	public static String mettingSummaryPath(){
		return getPath(PropertiesUtil.getProperty("mettingSummaryPath"));
	}
	/**
	 * 其他需决策事项法律附件存储路径
	 */
	public static String bulletinLegalPath(){
		return getPath(PropertiesUtil.getProperty("bulletinLegalPath"));
	}
	/**
	 * 其他需决策事项业务附件存储路径
	 */
	public static String bulletinReviewPath(){
		return getPath(PropertiesUtil.getProperty("bulletinReviewPath"));
	}
	/**
	 * 上会信息附件
	 */
	public static String preMeetingPath(){
		return getPath(PropertiesUtil.getProperty("preMeetingPath"));
	}
	/**
	 * 档案系统文件路径
	 */
	public static String daxtPath(){
		return getPath(daxtPathRoot());
	}
	/**
	 * 档案系统文件路径  根路径
	 */
	public static String daxtPathRoot(){
		return PropertiesUtil.getProperty("daxtPath");
	}
	public static String changeFrontFilePathRoot(){
		return PropertiesUtil.getProperty("changeFrontFilePath");
	}
	/**
	 * 取公共路径
	 */
	public static String getPropertyPath(String key){
		return getPath(PropertiesUtil.getProperty(key));
	}
	
	private static String getPath(String oldPath){
		String yearmonth = Util.format(Calendar.getInstance().getTime(), "yyyyMM");
		String newPath = oldPath+yearmonth+"/";
		return newPath;
	}
	
	/**
	 * 项目投资总结文件存放路径
	 */
	public static String formalSummaryAttachmentPath(){
		return getPath(PropertiesUtil.getProperty("formalSummaryPath"));
	}
	
}
