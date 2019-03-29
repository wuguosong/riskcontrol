package report;

import java.util.List;

import org.bson.Document;

class HazardousWasteReport extends FormalAssessmentReport{

	public HazardousWasteReport(String ObjectId) {
		super(ObjectId);
	}

	private static final String[] refillBookmark = {
			"projectName","create_name","create_date",
			"projectOverview",
			"marketCapacityAnalysis",
			"materialsPriceAnalysis",
			"conclusion",
			"result",
			"technologicalIntroduction","technologicalEvaluation",
			"investmentBenefitEvaluation",
			"potentialSpace",
			"conclusionAndSuggestions",
			"lawContent",
			"technicalReviewOpinions",
			"financialProfessionalReviewOpinion",
			"costCostReview",
			"explain"
	};
	private static final String[] Table_1 = {
		"Table_1",
		"projectSource", "regionalBackground", "regionalBackground2", 
		"invertmentUnit", "operationsUnit"
	};
	private static final String[] Table_2 = {
			"Table_2",
		"processingAbility","technologyCoreTeam","mianProcessing","mianProduct"
	};
	private static final String[] Table_3 = {
			"Table_3",
		"companyName","bewg",
		"bankLoan","annualInterestRate","ownFunds",
		"totalInvestment","oneKindCost","twoKindCost","additionalDebuggingCost","secondaryFloorCost","otherCost"
	};
	private static final String[] Table_rawMaterial = {
			"Table_rawMaterial","1",
		"rawMaterialName","title1","marketContent", "marketDataResource",
		"blank1","title2","areaContent", "areaDataResource",
		"blank1","title3","bidCompanyContent", "bidCompanyDataResource"
	};
	private static final String[] Table_5 = {
			"Table_5",
		"salesApproachContent", "salesApproachResource",
		"salePriceContent", "salePriceResource",
		"bidCompanySalesChannelsContent","bidCompanySalesChannelsResource"
	};
	private static final String[] Table_6 = {
			"Table_6",
			"productionScales",
			"mainIncomes",
			"mainCosts",
			"materialCosts",
			"manufacturingCostss",
			"AmortizationOfDepreciations",
			"subsidiesIncomes",
			"annualNetProfits",
			"workingCapitalNeedss",
			"loanDemands",
			"totalInvestments",
			"ROIs",
			"recentProfitMarginss"
	};
	private static final String[] Table_6PrepareData = {
			"Table_6",
		"productionScales","%",
		"mainIncomes","",
		"mainCosts","",
		"materialCosts","",
		"manufacturingCostss","",
		"AmortizationOfDepreciations","",
		"subsidiesIncomes","",
		"annualNetProfits","",
		"workingCapitalNeedss","",
		"loanDemands","",
		"totalInvestments","",
		"ROIs","",
		"recentProfitMarginss",""
	};
	
	@SuppressWarnings("unchecked")
	@Override
	public String generateReport(String ObjectId) {
		wordFile.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_HazardousWaste);

		wordFile.addHeader(reportData.getString(MongoKeys.ProjectName), reportData.getString("create_date"));
		
		wordFile.insertBeforeBookmark(refillBookmark, reportData);
		wordFile.replaceTableTexts(Table_1, reportData);
		wordFile.replaceTableTexts(Table_2, reportData);
		wordFile.replaceTableTexts(Table_3, reportData);
		wordFile.replaceTableTexts(Table_5, reportData);
		
		List<Document> rawMaterial = (List<Document>)reportData.get("rawMaterial");
		wordFile.fillTableRows(Table_rawMaterial, prepareRawMaterialData(rawMaterial), 3);
		
		
		wordFile.fillTableByColumn(Table_6, prepareTableColumnData(Table_6PrepareData, true), 1, 0);
		
		insertProjectConcernsIssuesAll();
		insertPotentialSpace();
		insertOptionList();
		insertRequireList();
		insertProfessionalOpinions();
		insertCostEstimate();
		
		String outputPath = Path.formalAssessmentReportPath() + "HazardousWaste"+ObjectId+System.currentTimeMillis()+".docx";
		wordFile.saveAs(outputPath);
		return outputPath;
	}
	
	private List<Document> prepareRawMaterialData(List<Document> rawMaterial){
		for(Document rowData : rawMaterial){
			rowData.put("title1", "全国市场容量");
			rowData.put("title2", "经营区域内容量");
			rowData.put("title3", "标的公司原材料来源");
		}
		return rawMaterial;
	}
	
	
}
