package report;


class DropOutReport extends FormalAssessmentReport{

	public DropOutReport(String ObjectId) {
		super(ObjectId);
	}

	private static final String[] refillBookmark = {
			"projectName","create_name","create_date",
			"projectMessege","projectCompanyMessege","reviewSituation","exitReason",
			"originalAgreement","preliminaryAgreed",
			"operatingResults","financialBook","indexAnalysis",
			"risksProblems",
			"conclusionAndSuggestions"
	};
	
	@Override
	public String generateReport(String ObjectId) {
		wordFile.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_DropOut);
		
		wordFile.addHeader(reportData.getString(MongoKeys.ProjectName), reportData.getString("create_date"));
		
		wordFile.insertBeforeBookmark(refillBookmark, reportData);
		insertProjectConcernsIssuesAll();
		insertOptionList();
		insertRequireList();
		
		String outputPath = Path.formalAssessmentReportPath() + "DropOut"+ObjectId+System.currentTimeMillis()+".docx";
		wordFile.saveAs(outputPath);
		return outputPath;
	}

}
