package report;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import formalAssessment.NoticeOfDecision;
import formalAssessment.ProjectFormalReview;

public class FormalApplication extends BaseApplication{
	private static Map<String, String> InvestmentTypeMap;
	static {
		InvestmentTypeMap = new HashMap<String, String>();
		InvestmentTypeMap.put("1","同意投资");
		InvestmentTypeMap.put("2","不同意投资");
		InvestmentTypeMap.put("3","同意有条件投资");
	}
	
	private static final String[] Table_supplement = {
			"Table",
		"decisionTime","decisionConclusion",
		"supplementaryItem"
	};
	
	@Override
	/**
	 * @param id _id of Constants.RCM_PROJECTFORMALREVIEW_INFO
	 * @return outputPath Path.formalAssessmentReportPath()+xxx.docx
	 */
	public String generateReport(String ObjectId) {
		logger.info("---start generate FormalApplication---");
		long startTime = System.currentTimeMillis();
		wordFile.setTemplate(Path.FORMAL_APPLICATION);
		
		super.generateReport(ObjectId);
		wordFile.replaceTableTexts(Table_supplement, reportData);
		
		String outputPath = Path.formalAssessmentReportPath() + "FormalApplication"+ObjectId+System.currentTimeMillis()+".docx";
		wordFile.saveAs(outputPath);
		logger.info("---end " + (System.currentTimeMillis() - startTime) + "---");
		logger.info("The application is generated at '" + outputPath + "'.");
		return outputPath;
	}

	@Override
	protected void initData(String id) {
		ProjectFormalReview dataSource = new ProjectFormalReview();
		Document projectData = (Document) dataSource.getProjectFormalReviewByID(id);
		reportData = (Document) projectData.get(MongoKeys.ApplyInfo);

		super.initData(id);
		
		if(reportData.containsKey("supplementReview")
				&& reportData.getBoolean("supplementReview")){
			wordFile.setTemplate(Path.SUPPLEMENT_FORMAL_APPLICATION);
			Document lastDecisionInfo = new NoticeOfDecision().getNoticeOfDecstionByProjectFormalID(reportData.getString("projectNo"));
			reportData.put("decisionTime", getStandardDateFormate(lastDecisionInfo.getString("dateOfMeeting")));
			reportData.put("decisionConclusion", InvestmentTypeMap.get(lastDecisionInfo.get("consentToInvestment")));
		}
		
	}
	
}
