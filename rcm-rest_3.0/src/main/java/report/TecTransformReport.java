package report;

import java.util.List;

import org.bson.Document;

class TecTransformReport extends FormalAssessmentReport{

	public TecTransformReport(String ObjectId) {
		super(ObjectId);
	}
	private static final String[] refillBookmark = {
			"projectName","create_name","create_date",
			"contentDescription",
			"technicalInnovationPlan",
			"effect",
			"partProceeds",
			"overallProjectRevenue",
			"risksProblems",
			"conclusionAndSuggestions",
			"lawContent",
			"technicalReviewOpinions",
			"financialProfessionalReviewOpinion",
			"explain"
	};
	private static final String[] Table_1 = {
			"Table_1",
		"projectName", "projectNameSource",
		"regionalBackground","regionalBackgroundSource",
		"basicSituation", "basicSituationSource",
		"technicalInnovationAgreement","technicalInnovationAgreementSource",
		"totalInvestment", "totalInvestmentSource",
		"constructionContent","constructionContentSource",
		"increaseCost","increaseCostSource"
	};
	private static final String[] Table_2 = {
			"Table_2",
		"agreementCOD","agreementBOD","agreementSS","agreementNN","agreementZN","agreementZL","agreementPH",
		"frontCOD","frontBOD","frontSS","frontNN","frontZN","frontZL","frontPH",
		"backCOD","backBOD","backSS","backNN","backZN","backZL","backPH"
	};
	private static final String[] Table_incomeList = {
			"Table_incomeList","1",
		"income_price","income_IRR",
		"元/吨","%"
	};
	
	@SuppressWarnings("unchecked")
	@Override
	public String generateReport(String ObjectId) {
		wordFile.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_TecTransform);
		
		wordFile.addHeader(reportData.getString(MongoKeys.ProjectName), reportData.getString("create_date"));
		
		wordFile.insertBeforeBookmark(refillBookmark, reportData);
		wordFile.replaceTableTexts(Table_1, reportData);
		wordFile.replaceTableTexts(Table_2, reportData);
		wordFile.fillTableByRow(Table_incomeList, (List<Document>)reportData.get("incomeList"), true);
		
		insertProjectConcernsIssuesAll();
		insertRequireList();
		insertProfessionalOpinions();
		insertCostEstimate();

		String outputPath = Path.formalAssessmentReportPath() + "TecTransform"+ObjectId+System.currentTimeMillis()+".docx";
		wordFile.saveAs(outputPath);
		return outputPath;
	}

}
