package report;

import formalAssessment.FormalReport;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

class FormalAssessmentReport extends BaseReport{

	public FormalAssessmentReport(String ObjectId){
		initData(ObjectId);
	}
	
	//==ProjectConcernsIssues==
	// 风险及问题总结-需要领导重点关注
	private static final String[] Table_leadershipDecision = {
			"Table_projectConcernsIssues","2",
		"rowIdAA", "riskPointAA", "riskContentAA", "pointsAndProblemsAA", "opinionAndCommitmentAA",
		"rowIdAB", "riskPointAB", "riskContentAB", "pointsAndProblemsAB", "opinionAndCommitmentAB"
	};
	// 风险及问题总结-一般性提示
	private static final String[] Table_investmentConditions = {
			"Table_projectConcernsIssues","4",
		"rowIdBA", "riskPointBA", "riskContentBA", "pointsAndProblemsBA", "opinionAndCommitmentBA",
		"rowIdBB", "riskPointBB", "riskContentBB", "pointsAndProblemsBB", "opinionAndCommitmentBB"
	};
	private static final String[] Table_prompt = {
			"Table_projectConcernsIssues","6",
		"rowIdCA", "riskPointCA", "riskContentCA", "pointsAndProblemsCA", "opinionAndCommitmentCA",
		"rowIdCB", "riskPointCB", "riskContentCB", "pointsAndProblemsCB", "opinionAndCommitmentCB"
	};
	private static final String[] Table_implementationRequirements = {
			"Table_projectConcernsIssues","8",
		"rowIdDA", "riskPointDA", "riskContentDA", "pointsAndProblemsDA", "opinionAndCommitmentDA",
		"rowIdDB", "riskPointDB", "riskContentDB", "pointsAndProblemsDB", "opinionAndCommitmentDB"
	};
	//===============================
	
	private static final String[] Table_optionList = {
			"Table_optionList","1",
		"rowId","option_content","option_execute"
	};
	
	private static final String[] Table_requireList = {
			"Table_requireList","1",
		"rowId","require_content","require_execute"
	};
	
	//==professionalOpinions==
	private static final String[] Table_lawList = {
			"Table_lawList","1",
		"rowId","lawOpinion","pendingConfirmation","canNotBeModified"
	};
	private static final String[] Table_technologyList = {
			"Table_technologyList","1",
		"rowId","technology_opinion"
	};
	private static final String[] Table_financeList = {
			"Table_financeList","1",
		"rowId","finance_opinion"
	};
	private static final String[] Table_drainageList = {
			"Table_drainageList","1",
		"rowId","drainage_opinion"
	};

	private static final String[] Table_drugList = {
			"Table_costEstimate", "5",
			"reagentCostNameAA",
			"reagentCostYearAA",
			"reagentCostTonsWaterAA",
			"reagentCostRemarksAA",
			"reagentCostExplainAA",
			"reagentCostNameAB",
			"reagentCostYearAB",
			"reagentCostTonsWaterAB",
			"reagentCostRemarksAB",
			"reagentCostExplainAB"
	};
	//============================

	protected static final String[] Table_costEstimate = {
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
	
	@Override
	public String generateReport(String ObjectId) {
		logger.info("There must be something wrong. FormalAssessmentReport's generate method should not be call!");
		return null;
	}

	protected void initData(String ObjectId){
		connectMongoDB();
		
		FormalReport dataSource = new FormalReport();
		reportData = (Document) dataSource.getByID(ObjectId);
		dateFormate(reportData, "create_date");
		if(reportData.containsKey("update_date")){
			reportData.put("create_date", getStandardDateFormate(reportData.getString("update_date")));
		}
		
		storeObjectName(reportData, "invertmentUnit", MongoKeys.DocumentName);
		storeObjectName(reportData, "operationsUnit", MongoKeys.DocumentName);
		
		closeMongoDB();
	}
	
	@SuppressWarnings("unchecked")
	protected void insertProjectConcernsIssues(){
		Document projectConcernsIssues = (Document)reportData.get(MongoKeys.ProjectConcernsIssues);
		List<Document> leadershipDecision = (List<Document>)projectConcernsIssues.get("leadershipDecision");
		List<Document> investmentConditions = (List<Document>)projectConcernsIssues.get("investmentConditions");
		List<Document> prompt = (List<Document>)projectConcernsIssues.get("prompt");
		replaceTableTexts_haveStableColumn(Table_leadershipDecision, leadershipDecision);
		replaceTableTexts_haveStableColumn(Table_investmentConditions, investmentConditions);
		replaceTableTexts_haveStableColumn(Table_prompt, prompt);
	}
	
	@SuppressWarnings("unchecked")
	protected void insertProjectConcernsIssuesAll(){
		Document projectConcernsIssues = (Document)reportData.get(MongoKeys.ProjectConcernsIssues);
		insertProjectConcernsIssues();
		List<Document> implementationRequirements = (List<Document>)projectConcernsIssues.get("implementationRequirements");
		replaceTableTexts_haveStableColumn(Table_implementationRequirements, implementationRequirements);
			
	}
	
	protected void insertPotentialSpace(){
		String [] potentialSpace = {"potentialSpace"};
		
		if(!reportData.containsKey("potentialSpace") || reportData.getString("potentialSpace").equals("")){
			wordFile.removeParagraphByBookmark("h1_potentialSpace");
		}else{
			wordFile.insertBeforeBookmark(potentialSpace, reportData);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void insertOptionList(){
		List<Document> data = (List<Document>)reportData.get(MongoKeys.FormalAssessment_OptionList);
		addRowId(data);
		wordFile.fillTableByRow(Table_optionList, data, false);
	}

	protected  void insertDrugList(){
		List<Document> data = (List<Document>)reportData.get(MongoKeys.FormalAssessment_DrugList);
		replaceTableTexts_haveStableColumn(Table_drugList, data);
	}
	
	@SuppressWarnings("unchecked")
	protected void insertRequireList(){
		List<Document> data = (List<Document>)reportData.get(MongoKeys.FormalAssessment_RequireList);
		addRowId(data);
		wordFile.fillTableByRow(Table_requireList, data, false);
	}
	
	@SuppressWarnings("unchecked")
	protected void insertProfessionalOpinions(){
		List<Document> lawList = (List<Document>)reportData.get(MongoKeys.FormalAssessment_LawList);
		List<Document> technoligyList = (List<Document>)reportData.get(MongoKeys.FormalAssessment_TechnologyList);
		List<Document> financeList = (List<Document>)reportData.get(MongoKeys.FormalAssessment_FinanceList);
		addRowId(lawList);
		addRowId(technoligyList);
		addRowId(financeList);
		
		if(lawList != null){
			for(Document law : lawList){
				if(law.getBoolean("pendingConfirmation")){
					law.put("pendingConfirmation", "√");
				}else{
					law.put("pendingConfirmation", "");
				}
				
				if(law.getBoolean("canNotBeModified")){
					law.put("canNotBeModified", "√");
				}else{
					law.put("canNotBeModified", "");
				}
			}
		}
		
		wordFile.fillTableByRow(Table_lawList, lawList, false);
		wordFile.fillTableByRow(Table_technologyList, technoligyList, false);
		wordFile.fillTableByRow(Table_financeList, financeList, false);
	}
	
	@SuppressWarnings("unchecked")
	protected void insertProfessionalOpinionsAll(){
		insertProfessionalOpinions();
		List<Document> hrList = (List<Document>)reportData.get(MongoKeys.FormalAssessment_DrainageList);
		addRowId(hrList);
		wordFile.fillTableByRow(Table_drainageList, hrList, false);
	}
	
	protected void insertCostEstimate(){
		wordFile.replaceTableTexts(Table_costEstimate,(Document)reportData.get(MongoKeys.CostEstimate));
	}
	
	@SuppressWarnings("unchecked")
	public List<Document> prepareTableColumnData(String[] marks, boolean haveUnit){
		List<Document> data = new ArrayList<Document>();
		if((List<Document>)reportData.get(marks[1]) == null){
			return null;
		}
		int size = ((List<Document>)reportData.get(marks[1])).size();
		
		for(int i = 0; i < size; i++){
			Document columnData = new Document();
			for(int j = 1; j < marks.length; j++){
				List<Document> markData = (List<Document>)reportData.get(marks[j]);
				columnData.put(marks[j], markData.get(i).getString("value"));
				if(haveUnit){
					j++;
					columnData.put(marks[j-1], markData.get(i).getString("value") + marks[j]);
				}
			}
			data.add(columnData);
		}
		
		return data;
	}
	
	public String getReportType(){
		return reportData.getString("controllerVal");
	}
	
	private void addRowId(List<Document> data){
		int i = 1;
		if(data == null){
			return;
		}
		for(Document rowData : data){
			rowData.put("rowId", i+"");
			i++;
		}
	}
}
