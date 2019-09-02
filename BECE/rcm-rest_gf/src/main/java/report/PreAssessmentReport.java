package report;

import java.util.List;

import org.bson.Document;

import projectPreReview.ProjectPreReview;

public class PreAssessmentReport extends BaseReport{
	
	private static final String[] refillBookmark = {
			"projectName","createName","create_date",
			"projectSummary"
	};
	private static final String[] Table_essentialInfo1 = {
			"Table_essentialInfo1",
			"basicInfoOfProject",
			"tenderTime","paymentTime","paymentMoney","bidLimit",
			"totalInvestment","oneKindCost","twoKindCost","additionalDebuggingCost","secondaryFloorCost","otherCost",
			"tenderCompany","shareholding",
			"bankLoan","annualInterestRate","privateCapital",
			"preAssessmentScale",
			"invertmentUnit","operationsUnit"
	};
	private static final String[] Table_essentialInfo2 = {
			"Table_essentialInfo2",
			"incomeComposition","measuringWaterQuantity"
	};
	private static final String[] Table_incomeEvaluation = {
			"Table_incomeEvaluation","1",
		"profitSubject","profitInvestment","profitPrivateCapital","profitCashFlowInfo",
		"元/吨","%","%",""
	};
	private static final String[] Table_influenceBidding = {
			"Table_projectConcernsIssues","2",
		"rowIdAA","pointsAndProblemsAA","opinionAndCommitmentAA",
		"rowIdAB","pointsAndProblemsAB","opinionAndCommitmentAB"
	};  
	private static final String[] Table_focusOnMatter = {
			"Table_projectConcernsIssues","4",
		"rowIdBA","pointsAndProblemsBA","opinionAndCommitmentBA",
		"rowIdBB","pointsAndProblemsBB","opinionAndCommitmentBB"
	};  
	private static final String[] Table_agreementPoints = {
			"Table_projectConcernsIssues","6",
		"rowIdCA","pointsAndProblemsCA","opinionAndCommitmentCA",
		"rowIdCB","pointsAndProblemsCB","opinionAndCommitmentCB"
	};
	private static final String[] Table_costEstimate = {
		"Table_costEstimate",
		"electricityTariffYear","electricityTariffTonsWater","electricityTariffRemarks","electricityTariffExplain",
		"basicElectricityYear","basicElectricityTonsWater","basicElectricityRemarks","basicElectricityExplain",
		"reagentCostYear","reagentCostTonsWater","reagentCostRemarks","reagentCostExplain",
		"disposalFeeYear","disposalFeeTonsWater","disposalFeeRemarks","disposalFeeExplain",
		"waterRatesYear","waterRatesTonsWater","waterRatesRemarks","waterRatesExplain",
		"laborCostYear","laborCostTonsWater","laborCostRemarks","laborCostExplain",
		"maintenanceCostYear","maintenanceCostTonsWater","maintenanceCostRemarks","maintenanceCostExplain",
		"laboratoryTestingFeeYear","laboratoryTestingFeeTonsWater","laboratoryTestingFeeRemarks","laboratoryTestingFeeExplain",
		"propertyInsurancePremiumYear","propertyInsurancePremiumTonsWater","propertyInsurancePremiumRemarks","propertyInsurancePremiumExplain",
		"taxationYear","taxationTonsWater","taxationRemarks","taxationExplain",
		"heatingFeeYear","heatingFeeTonsWater","heatingFeeRemarks","heatingFeeExplain",
		"managementExpenseYear","managementExpenseTonsWater","managementExpenseRemarks","managementExpenseExplain",
		"valueAddedTaxYear","valueAddedTaxTonsWater","valueAddedTaxRemarks","valueAddedTaxExplain",
		"incomeTaxYear","incomeTaxTonsWater","incomeTaxRemarks","incomeTaxExplain"
	};
	
	private static final String[] test1 = {
			"projectName","create_date","create_name","operationsUnitName"
	};
	
	/**
	 * 测试word导出用法
	 * */
	public String testReport(String id){
		long startTime = System.currentTimeMillis();
		wordFile.setTemplate(Path.TEST_EXPORT);
		getData(id);
		wordFile.replaceTableTexts(test1, reportData);
		String outputPath = Path.preAssessmentReportPath() + "PreAssessmentReport"+id+System.currentTimeMillis()+".docx";
		wordFile.saveAs(outputPath);
		logger.info("---end " + (System.currentTimeMillis() - startTime) + "---");
		logger.info("The report is generated at '" + outputPath + "'.");
		return outputPath;
	}
	
	private void getData(String id){
		connectMongoDB();
		
		ProjectPreReview dataSource = new ProjectPreReview();
		Document projectData = (Document) dataSource.getProjectPreReviewByID(id);
		reportData = (Document) projectData.get(MongoKeys.PreAssessmentReport);
		
		Document essentialInformation = (Document) reportData.get("essentialInformation");
		Document operationsUnit = (Document) essentialInformation.get("operationsUnit");
		reportData.put("operationsUnitName", operationsUnit.get("name"));
		
		Document applyInfo = (Document)projectData.get(MongoKeys.ApplyInfo);
		reportData.put("projectName", applyInfo.get(MongoKeys.ProjectName));
		
		closeMongoDB();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * @param id _id of Constants.PREASSESSMENT
	 * @return outputPath Path.preAssessmentReportPath()+xxx.docx
	 */
	public String generateReport(String id){
		logger.info("---start generate PreAssessmentReport---");
		long startTime = System.currentTimeMillis();
		wordFile.setTemplate(Path.PRE_ASSESSMENT_REPORT);
		
		initData(id);
		
		wordFile.addHeader(reportData.getString(MongoKeys.ProjectName), reportData.getString("create_date"));
		
		wordFile.insertBeforeBookmark(refillBookmark, reportData);
		wordFile.replaceTableTexts(Table_essentialInfo1, (Document)reportData.get(MongoKeys.PreAssessmentReport_EssentialInfo));
		wordFile.replaceTableTexts(Table_essentialInfo2, (Document)reportData.get(MongoKeys.PreAssessmentReport_EssentialInfo));
		wordFile.fillTableByRow(Table_incomeEvaluation, (List<Document>)reportData.get(MongoKeys.PreAssessmentReport_IncomeEvaluation), true);
		insertProjectConcernsIssues();
		wordFile.replaceTableTexts(Table_costEstimate, (Document)reportData.get(MongoKeys.CostEstimate));
		
		String outputPath = Path.preAssessmentReportPath() + "PreAssessmentReport"+id+System.currentTimeMillis()+".docx";
		wordFile.saveAs(outputPath);
		logger.info("---end " + (System.currentTimeMillis() - startTime) + "---");
		logger.info("The report is generated at '" + outputPath + "'.");
		return outputPath;
	}
	
	private void initData(String id){
		connectMongoDB();
		
		ProjectPreReview dataSource = new ProjectPreReview();
		Document projectData = (Document) dataSource.getProjectPreReviewByID(id);
		reportData = (Document) projectData.get(MongoKeys.PreAssessmentReport);
		System.out.print(reportData.get(MongoKeys.ProjectCreateName));
		System.out.print(reportData.get(MongoKeys.ProjectCreateDate));
		
		Document applyInfo = (Document)projectData.get(MongoKeys.ApplyInfo);
		reportData.put("projectName", applyInfo.get(MongoKeys.ProjectName));
		
		reportData.put("createName", reportData.getString("create_name"));
		dateFormate(reportData, "create_date");
		if(reportData.containsKey("update_date")){
			reportData.put("create_date", getStandardDateFormate(reportData.getString("update_date")));
		}
		
		Document essentialInfo = (Document)reportData.get(MongoKeys.PreAssessmentReport_EssentialInfo);
		dateFormate(essentialInfo, "tenderTime");
		dateFormate(essentialInfo, "paymentTime");
		storeObjectName(essentialInfo, "tenderCompany", MongoKeys.DocumentName);
		storeObjectName(essentialInfo, "invertmentUnit", MongoKeys.DocumentName);
		storeObjectName(essentialInfo, "operationsUnit", MongoKeys.DocumentName);
		if(essentialInfo.containsKey("inputWay")){
			int type = Integer.valueOf(essentialInfo.getString("inputWay"));
			if(type == 1){
				essentialInfo.put("preAssessmentScale", essentialInfo.getString("preAssessmentScale")+"万吨/日");
			}
		}
		if(essentialInfo.containsKey("bidLimit")){
			essentialInfo.put("bidLimit", "投标上限："+essentialInfo.getString("bidLimit"));
		}
		
		Document projectConcernsIssues = (Document)reportData.get(MongoKeys.ProjectConcernsIssues);
		if(projectConcernsIssues != null ){
			if(projectConcernsIssues.containsKey("projectSummary")){
				reportData.put("projectSummary", projectConcernsIssues.get("projectSummary"));
			}
		}
		
		closeMongoDB();
	}
	

	@SuppressWarnings("unchecked")
	private void insertProjectConcernsIssues(){
		Document projectConcernsIssues = (Document)reportData.get(MongoKeys.ProjectConcernsIssues);
		List<Document> influenceBidding = (List<Document>)projectConcernsIssues.get("influenceBidding");
		List<Document> focusOnMatter = (List<Document>)projectConcernsIssues.get("focusOnMatter");
		List<Document> agreementPoints = (List<Document>)projectConcernsIssues.get("agreementPoints");
		replaceTableTexts_haveStableColumn(Table_influenceBidding, influenceBidding);
		replaceTableTexts_haveStableColumn(Table_focusOnMatter, focusOnMatter);
		replaceTableTexts_haveStableColumn(Table_agreementPoints, agreementPoints);
	}
	
}
