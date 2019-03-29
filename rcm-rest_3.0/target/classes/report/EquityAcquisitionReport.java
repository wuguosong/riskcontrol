package report;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;


class EquityAcquisitionReport extends FormalAssessmentReport{

	public EquityAcquisitionReport(String ObjectId) {
		super(ObjectId);
	}

	private static final String[] refillBookmark = {
			"projectName","create_name","create_date",
			"projectOverview",
			"projectValueEstimation",
			"valueEstimation_discount",
			"returnInvestment",
			"dividendCashFlowContent",
			"risksProblems",
			"potentialSpace",
			"conclusionAndSuggestions",
			"lawContent",
			"technicalReviewOpinions",
			"financialProfessionalReviewOpinion",
			"humanResourceOpinion"
	};
	private static final String[] Table_1 = {
		"Table_1",
		"projectSource", "regionalBackground", "regionalBackground2", 
		"nameOfCompany", "ownershipStructure", "mainBusiness", 
		"financialSituation", "financialSituationEnd", 
		"invertmentUnit", "operationsUnit"
	};
	private static final String[] Table_2 = {
			"Table_2",
		"equityAcquisitionMode","averageMarketRatePB","managementPlan"
	};
	private static final String[] Table_3 = {
			"Table_3",
		"fProjectName",
		"businessModel",
		"waterStandard",
		"technologyType",
		"operationLife",
		"operationStatus",
		"residualConcessionPeriod",
		"evaluationScale",
		"contractScale",
		"actualWaterTreatment",
		"measuringWaterQuantity",
		"agreementPrice",
		"priceAdjustment"
	};
	private static final String[] Table_3prepareData = {
			"Table_3",
		"fProjectName","",
		"businessModel","",
		"waterStandard","",
		"technologyType","",
		"operationLife","",
		"operationStatus","",
		"residualConcessionPeriod","年",
		"evaluationScale","万吨/日",
		"contractScale","万吨/日",
		"actualWaterTreatment","",
		"measuringWaterQuantity","",
		"agreementPrice","元/吨",
		"priceAdjustment",""
	};
	private static final String[] Table_4_Replace = {
			"Table_4",
		"valuationA","valuationB","valuationC"
	};
	private static final String[] Table_4_FillTable = {
			"Table_4","1",
		"valueEstimation_discount", "valueEstimation_A", "valueEstimation_B", "valueEstimation_C", 
		"%","","",""
	};
	private static final String[] Table_5 = {
			"Table_5",
		"previousDataIncome","previousDataCost","previousDataNetProfit",
		"forecastDataIncome","forecastDataCost","forecastDataNetProfit",
		"comparisonResultIncome","comparisonResultCost","comparisonResultNetProfit"
	};
	private static final String[] Table_6 = {
			"Table_6",
		"totalInvestmentYieldIRR","returnOnItsOwnFundsIRR","totalInvestmentPaybackPeriod",
		"averageROE","averageROI","netCashFlowForTheFirstFiveYears"
	};
	private static final String[] Table_7 = {
			"Table_7",
		"oldStandardNetProfit","oldStandardNetProfitOne","oldStandardNetProfitTwo","oldStandardNetProfitThree","oldStandardNetProfitFour","oldStandardNetProfitFive",
		"twoExplainNetProfit","twoExplainNetProfitOne","twoExplainNetProfitTwo","twoExplainNetProfitThree","twoExplainNetProfitFour","twoExplainNetProfitFive",
		"twoExplanationROI","twoExplanationROIOne","twoExplanationROITwo","twoExplanationROIThree","twoExplanationROIFour","twoExplanationROIFive",
		"cashFlowFromInvestingActivities","cashFlowFromInvestingActivitiesOne","cashFlowFromInvestingActivitiesTwo","cashFlowFromInvestingActivitiesThree","cashFlowFromInvestingActivitiesFour","cashFlowFromInvestingActivitiesFive",
		"theNetCashFlowManagement","theNetCashFlowManagementOne","theNetCashFlowManagementTwo","theNetCashFlowManagementThree","theNetCashFlowManagementFour","theNetCashFlowManagementFive",
		"ownFunds",
		"ownOldStandardNetProfit","ownOldStandardNetProfitOne","ownOldStandardNetProfitTwo","ownOldStandardNetProfitThree","ownOldStandardNetProfitFour","ownOldStandardNetProfitFive",
		"ownTwoExplainNetProfit","ownTwoExplainNetProfitOne","ownTwoExplainNetProfitTwo","ownTwoExplainNetProfitThree","ownTwoExplainNetProfitFour","ownTwoExplainNetProfitFive",
		"ownTwoExplanationROE","ownTwoExplanationROEOne","ownTwoExplanationROETwo","ownTwoExplanationROEThree","ownTwoExplanationROEFour","ownTwoExplanationROEFive",
		"ownCashFlowFromInvestingActivities","ownCashFlowFromInvestingActivitiesOne","ownCashFlowFromInvestingActivitiesTwo","ownCashFlowFromInvestingActivitiesThree","ownCashFlowFromInvestingActivitiesFour","ownCashFlowFromInvestingActivitiesFive",
		"dividendCashFlow","dividendCashFlowOne","dividendCashFlowTwo","dividendCashFlowThree","dividendCashFlowFour","dividendCashFlowFive"
	};
	
	@SuppressWarnings("unchecked")
	@Override
	public String generateReport(String ObjectId) {
		wordFile.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_EquityAcquisition);
		
		wordFile.addHeader(reportData.getString(MongoKeys.ProjectName), reportData.getString("create_date"));
		
		wordFile.insertBeforeBookmark(refillBookmark, reportData);
		wordFile.replaceTableTexts(Table_1, reportData);
		wordFile.replaceTableTexts(Table_2, reportData);
		wordFile.fillTableByColumn(Table_3, prepareTableColumnData(Table_3prepareData, true), 1, 0);
		wordFile.replaceTableTexts(Table_4_Replace, reportData);
		wordFile.fillTableByRow(Table_4_FillTable, (List<Document>)reportData.get("valueEstimationList"), true);
		wordFile.replaceTableTexts(Table_5, reportData);
		wordFile.replaceTableTexts(Table_6, reportData);
		wordFile.replaceTableTexts(Table_7, reportData);
		
		wordFile.setMinusToRed(Table_7[0]);
		
		insertProjectConcernsIssuesAll();
		insertPotentialSpace();
		insertOptionList();
		insertRequireList();
		insertProfessionalOpinionsAll();
		insertCostEstimate();
		
		String outputPath = Path.formalAssessmentReportPath() + "EquityAcquisition"+ObjectId+System.currentTimeMillis()+".docx";
		wordFile.saveAs(outputPath);
		return outputPath;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initData(String ObjectId){
		super.initData(ObjectId);
		
		if(reportData.containsKey("businessModel")){
			List<Document> businessModel = (List<Document>)reportData.get("businessModel");
			restoreItemName(businessModel);
			List<Document> waterStandard = (List<Document>)reportData.get("waterStandard");
			restoreItemName(waterStandard);
		}
	}
	
	private void restoreItemName(List<Document> data){
		for(Document rowData : data){
			Document value = (Document)rowData.get("value");
			if(value == null){
				rowData.put("value", "");
			}else{
				rowData.put("value", value.get(MongoKeys.ItemName));
			}
			
		}
	}
	
	
	private static final String[] Table_costEstimate = {
			"Table_costEstimate",
			"fProjectName","blank",
			"title1","title2",
			"electricityTariffYear","electricityTariffTonsWater",
			"basicElectricityYear","basicElectricityTonsWater",
			"reagentCostYear","reagentCostTonsWater",
			"disposalFeeYear","disposalFeeTonsWater",
			"waterRatesYear","waterRatesTonsWater",
			"laborCostYear","laborCostTonsWater",
			"maintenanceCostYear","maintenanceCostTonsWater",
			"laboratoryTestingFeeYear","laboratoryTestingFeeTonsWater",
			"propertyInsurancePremiumYear","propertyInsurancePremiumTonsWater",
			"taxationYear","taxationTonsWater","taxationRemarks",
			"heatingFeeYear","heatingFeeTonsWater",
			"managementExpenseYear","managementExpenseTonsWater",
			"valueAddedTaxYear","valueAddedTaxTonsWater",
			"incomeTaxYear","incomeTaxTonsWater",
	};
	private static final String[] Table_costEstimateMongoKeys = {
		"fProjectNames",
		"title",
		"electricityTariff",
		"basicElectricity",
		"reagentCost",
		"disposalFee",
		"waterRates",
		"laborCost",
		"maintenanceCost",
		"laboratoryTestingFee",
		"propertyInsurancePremium",
		"taxation",
		"heatingFee",
		"managementExpense",
		"valueAddedTax",
		"incomeTax"
	};
	@Override
	protected void insertCostEstimate(){
		List<Document> data = prepareCostEstimateData(Table_costEstimateMongoKeys, Table_costEstimate);
		wordFile.fillTableByColumn(Table_costEstimate, data, 2, 1);
	}
	
	@SuppressWarnings("unchecked")
	private List<Document> prepareCostEstimateData(String[] dataMarks, String[] fileMarks){
		List<Document> data = new ArrayList<Document>();
		Document costEstimateData = (Document) reportData.get(MongoKeys.CostEstimate);
		
		if((List<Document>)costEstimateData.get(dataMarks[0]) == null){
			return null;
		}
		
		
		
		int size = ((List<Document>)costEstimateData.get(dataMarks[0])).size();
		for(int i = 0; i < size; i++){
			Document twoColumnData = new Document();
			List<Document> titleData = (List<Document>)costEstimateData.get(dataMarks[0]);
			twoColumnData.put(fileMarks[1], titleData.get(i).getString("value"));
			twoColumnData.put(fileMarks[2], "");
			
			for(int j = 1; j < dataMarks.length; j++){
				List<Document> markData = (List<Document>)costEstimateData.get(dataMarks[j]);
				twoColumnData.put(fileMarks[j*2 + 1], markData.get(i*2).getString("value"));
				twoColumnData.put(fileMarks[j*2 + 2], markData.get(i*2+1).getString("value"));
			}
			data.add(twoColumnData);
		}
		
		return data;
	}
}
