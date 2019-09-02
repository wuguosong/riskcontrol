package report;

import java.util.List;

import org.bson.Document;

public class SupplementReport extends FormalAssessmentReport{
	
	public SupplementReport(String ObjectId) {
		super(ObjectId);
	}

	private static final String[] refillBookmark = {
			"projectName","create_name","create_date",
			"mainReason",
			"borderCondition",
			"risksProblems",
			"potentialSpace",
			"conclusionAndSuggestions"
	};
	private static final String[] Table_1 = {
		"Table_1",
		"originalReviewTime","originalAppraisalConclusion","originalReviewProjectInformation"
	};
	private static final String[] Table_adjustmentAnalysis = {
			"Table_adjustmentAnalysis","1",
			"adjustTargets","title1","oldReview","jumpingReason",
			"blank1","title2","afterReview","blank2"
	};
	private static final String[] Table_3 = {
			"Table_3",
			"inerIncomeInvestmentIRR1", "inerIncomeInvestmentIRR2", "inerIncomeInvestmentIRR3", 
		    "ownIncomeInvestmentIRR1", "ownIncomeInvestmentIRR2", "ownIncomeInvestmentIRR3", 
		    "staticIncome1", "staticIncome2", "staticIncomeInvestment3", 
		    "averageROE1", "averageROE2", "averageROE3", 
		    "averageROI1","averageROI2", "averageROI3", 
		    "ownCashFlow1","ownCashFlow2","ownCashFlow3"
	};
	
	
	@SuppressWarnings("unchecked")
	@Override
	public String generateReport(String ObjectId) {
		wordFile.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_Supplement);
		
		wordFile.addHeader(reportData.getString(MongoKeys.ProjectName), reportData.getString("create_date"));
		
		wordFile.insertBeforeBookmark(refillBookmark, reportData);
		wordFile.replaceTableTexts(Table_1, reportData);
		wordFile.replaceTableTexts(Table_3, reportData);
		
		List<Document> adjustmentAnalysis = (List<Document>)reportData.get("adjustmentAnalysis");
		for(Document rowData : adjustmentAnalysis){
			rowData.put("title1", "原评审");
			rowData.put("title2", "调整后");
		}
		wordFile.fillTableRows(Table_adjustmentAnalysis, adjustmentAnalysis, 2);
		
		insertProjectConcernsIssues();
		insertPotentialSpace();
		insertOptionList();
		insertRequireList();
		
		String outputPath = Path.formalAssessmentReportPath() + "Supplement"+ObjectId+System.currentTimeMillis()+".docx";
		wordFile.saveAs(outputPath);
		return outputPath;
	}

	@Override
	protected void initData(String ObjectId){
		super.initData(ObjectId);
		reportData.put("staticIncome1", reportData.getString("staticIncomeInvestment1"));
		reportData.put("staticIncome2", reportData.getString("staticIncomeInvestment2"));
	}
	
}
