package report;

import java.util.List;

import org.bson.Document;

class FormalReviewReport extends FormalAssessmentReport{

	public FormalReviewReport(String ObjectId) {
		super(ObjectId);
	}

	private static final String[] refillBookmark = {
			"projectName","create_name","create_date",
			"projectOverview",
			"potentialSpace",
			"lawContent",
			"technicalReviewOpinions",
			"financialProfessionalReviewOpinion",
			"humanResourceOpinion",
			"costCostReview",
			"explain"
	};
	private static final String[] Table_1 = {
		"Table_1",
		"projectSource", 
		"regionalBackground", "regionalBackground2", 
		"ownerSituation",
		"invertmentUnit", "operationsUnit"
	};
	private static final String bookmarkNameOfInsertPosition = "insertMark";
	private int insertCursor[] = {-1, -1};
	
	@Override
	public String generateReport(String ObjectId) {
		wordFile.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_FormalReview);

		wordFile.addHeader(reportData.getString(MongoKeys.ProjectName), reportData.getString("create_date"));
		
		wordFile.insertBeforeBookmark(refillBookmark, reportData);
		wordFile.replaceTableTexts(Table_1, reportData);
		
		insertBTppp();
		insertSupply("WATERSUPPLYBOT");
		insertSupply("WATERSUPPLYTOT");
		insertSanitation();
		insertEntrustedManagement();
		insertSewage("WSBOT");
		insertSewage("WSTOT");
		
		insertProjectConcernsIssuesAll();
		insertPotentialSpace();
		insertOptionList();
		insertRequireList();
		insertProfessionalOpinionsAll();
		insertCostEstimate();
		
		String outputPath = Path.formalAssessmentReportPath() + "FormalReview"+ObjectId+System.currentTimeMillis()+".docx";
		wordFile.saveAs(outputPath);
		return outputPath;
	}

	private static final String[] BasicSubRefillBookmark = {
		"title",
		"incomeEvaluation"
	};
	private static final String[] BTPPP_Table_1 = {
			"BTPPP_Table_1",
		"tenderCompany","shareholding",
		"bankLoan","annualInterestRate","privateCapital",
		"constructionContent",
		"totalInvestment","investmentComposition",
		"paymentSchedule",
		"engineeringProfit",
		"managementExpense",
		"variousTaxesFees",
		"constructionPeriod","buyBackPeriod","buyBackDate",
		"buyBack",
		"buyBackGuarantee"
	};
	private static final String[] BTPPP_Table_2 = {
			"BTPPP_Table_2",
		"normalFullInvestmentIRR", "normalAverageROI",
		"buyBackProfitOne", "buyBackProfitTwo", "buyBackProfitThree", "buyBackProfitFour", "buyBackProfitFive",
		"buyBackCashFlowOne", "buyBackCashFlowTwo", "buyBackCashFlowThree", "buyBackCashFlowFour", "buyBackCashFlowFive",
		"otherFullInvestmentIRR","otherAverageROI",
		"otherModeProfitOne", "otherModeProfitTwo", "otherModeProfitThree", "otherModeProfitFour", "otherModeProfitFive",
		"otherModeCashOne", "otherModeCashTwo", "otherModeCashThree", "otherModeCashFour", "otherModeCashFive",
	};
	@SuppressWarnings("unchecked")
	private void insertBTppp(){
		if(!reportData.containsKey("BTPPP")){
			return;
		}
		
		List<Document> btPPPList = (List<Document>)reportData.get("BTPPP");
		for(Document btPPP : btPPPList){
			storeObjectName(btPPP, "tenderCompany", MongoKeys.DocumentName);
			dateFormate(btPPP, "buyBackDate");
			
			MSWord subDoc = new MSWord();
			subDoc.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_BTPPP);
			
			subDoc.insertBeforeBookmark(BasicSubRefillBookmark, btPPP);
			subDoc.replaceTableTexts(BTPPP_Table_1, btPPP);
			subDoc.replaceTableTexts(BTPPP_Table_2, btPPP);
			subDoc.setMinusToRed(BTPPP_Table_2[0]);
			
			wordFile.addProfile(subDoc.getDocument(), bookmarkNameOfInsertPosition, insertCursor);
		}
	}
	
	private static final String[] BOTTOT_refillBookmark = {
			"title",
			"incomeEvaluation",
			"content"
	};
	private static final String[] Supply_Table_1 = {
			"Supply_Table_1",
		"scopeOfService",
		"operationStatus",
		"waterIntake",
		"runningWaterPrice",
		"incomeCompositionAndForecast","incomeComposition",
		"franchisePeriod","containConstructionPeriod","constructionPeriod",
		"operatingPeriod",
		"salesSlip","waterRateRecovery",
		"contractScale","evaluationScale"
	};
	private static final String[] Supply_Table_2 = {
			"Supply_Table_2",
		"tenderCompany","investmentSubjectPercentage","shareholding",
		"bankLoan","privateCapital",
		"sourcesOfFunds","annualInterestRate","sourcesOfFundsPercentage",
		"totalInvestment","oneKindOfCost","twoKindOfCost","supplementaryCommissioningFee","secondaryFloorLiquidity","other",
		"debuggingCost","workingCapital",
		"personnelPlacementProgram",
		"operatingCostOfWater","totalWaterCost",
		"waterManagementFee","profitOfWater",
		"waterLaborCost","theAverageProfit"
	};
	private static final String[] Supply_Table_3 = {
			"Supply_Table_3",
		"allInternalRateOfReturn",
		"haveInternalRateOfReturn",
		"staticPaybackPeriod",
		"averageROE",
		"averageROI",
		"netCashFlow"
	};
	private static final String[] Supply_Table_4 = {
			"Supply_Table_4",
		"netProfitAll","netProfitAllOne","netProfitAllTwo","netProfitAllThree","netProfitAllFour","netProfitAllFive",
		"roi","ROIOne","ROITwo","ROIThree","ROIFour","ROIFive",
		"activeCashFlow","activeCashFlowOne","activeCashFlowTwo","activeCashFlowThree","activeCashFlowFour","activeCashFlowFive",
		"operatingCashFlow","operatingCashFlowOne","operatingCashFlowTwo","operatingCashFlowThree","operatingCashFlowFour","operatingCashFlowFive",
		"ownFunds",
		"netProfit","netProfitOne","netProfitTwo","netProfitThree","netProfitFour","netProfitFive",
		"roe","ROEOne","ROETwo","ROEThree","ROEFour","ROEFive",
		"itsNetCashFlow","itsNetCashFlowOne","itsNetCashFlowTwo","itsNetCashFlowThree","itsNetCashFlowFour","itsNetCashFlowFive",
		"dividendCashFlo","dividendCashFlowOne","dividendCashFlowTwo","dividendCashFlowThree","dividendCashFlowFour","dividendCashFlowFive"
	};
	
	private void insertSupply(String supplyType){
		@SuppressWarnings("unchecked")
		List<Document> supplyList = (List<Document>)reportData.get(supplyType);
		if(supplyList == null){
			return;
		}
		
		for(Document supply : supplyList){
			storeObjectName(supply, "tenderCompany", MongoKeys.DocumentName);
			isContainConstructionPeriod(supply);
			storeLowercaseKey(supply, "ROI");
			storeLowercaseKey(supply, "ROE");
			
			if(supply.containsKey("dividendCashFlow")){
				supply.put("dividendCashFlo", supply.get("dividendCashFlow"));
			}
			
			MSWord subDoc = new MSWord();
			if(supplyType.equals("WATERSUPPLYBOT")){
				subDoc.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_BOTSupply);
			}else if(supplyType.equals("WATERSUPPLYTOT")){
				subDoc.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_TOTSupply);
			}else{
				return;
			}
			
			subDoc.insertBeforeBookmark(BOTTOT_refillBookmark, supply);
			subDoc.replaceTableTexts(Supply_Table_1, supply);
			subDoc.replaceTableTexts(Supply_Table_2, supply);
			subDoc.replaceTableTexts(Supply_Table_3, supply);
			subDoc.replaceTableTexts(Supply_Table_4, supply);
			subDoc.setMinusToRed(Supply_Table_4[0]);
			
			wordFile.addProfile(subDoc.getDocument(), bookmarkNameOfInsertPosition, insertCursor);
		}
	}
	
	private static final String[] Sewage_Table_1 = {
			"Sewage_Table_1",
		"serviceArea",
		"projectRunState",
		"concessionaryTime","containConstructionPeriod","construction",
		"projectCraft","waterStandard",
		"agreementWaterprice","priceCicle",
		"guaranteedWater","measuringWater",
		"projectScale","reviewScale"
	};
	private static final String[] Sewage_Table_2 = {
			"Sewage_Table_2",
		"tenderCompany","bewg",
		"bankLoan","annualInterestRate","oweMoney",
		"totleMoney","totMoney","modificationCost",
		"oneKindOfCost","twoKindOfCost",
		"totalInvestment","oneKindCost","twoKindCost","supplementaryCommissioningFee","secondaryFloorLiquidity","other",
		"personnelPlacement",
		"operatingCost","totalCost",
		"managementFees","profits",
		"laborCost","laborCreate"
	};
	private static final String[] Sewage_Table_3 = {
			"Sewage_Table_3",
		"investmentProfitIRR",
		"owninvestmentProfitIRR",
		"staticInvestmentProfit",
		"averageROE",
		"averageROI",
		"profitsOne"
	};
	private static final String[] Sewage_Table_4 = {
			"Sewage_Table_4",
		"investment","investmentOne","investmentTwo","investmentThree","investmentFour","investmentFive",
		"investmentAll","investmentAllOne","investmentAllTwo","investmentAllThree","investmentAllFour","investmentAllFive",
		"roi","ROIOne","ROITwo","ROIThree","ROIFour","ROIFive",
		"investmentActivitesFlow","investmentActivitesFlowOne","investmentActivitesFlowTwo","investmentActivitesFlowThree","investmentActivitesFlowFour","investmentActivitesFlowFive",
		"investmentActivitesCleanFlow","investmentActivitesCleanFlowOne","investmentActivitesCleanFlowTwo","investmentActivitesCleanFlowThree","investmentActivitesCleanFlowFour","investmentActivitesCleanFlowFive",
		"ownFunds",
		"cleanIncome","cleanIncomeOne","cleanIncomeTwo","cleanIncomeThree","cleanIncomeFour","cleanIncomeFive",
		"othercleanIncome","othercleanIncomeOne","othercleanIncomeTwo","othercleanIncomeThree","othercleanIncomeFour","othercleanIncomeFive",
		"roe","ROEOne","ROETwo","ROEThree","ROEFour","ROEFive",
		"ownFundsInCashFlows","ownFundsInCashFlowsOne","ownFundsInCashFlowsTwo","ownFundsInCashFlowsThree","ownFundsInCashFlowsFour","ownFundsInCashFlowsFive",
		"dividendCashFlow","dividendCashFlowOne","dividendCashFlowTwo","dividendCashFlowThree","dividendCashFlowFour","dividendCashFlowFive"
	};
	private void insertSewage(String sewageType){
		@SuppressWarnings("unchecked")
		List<Document> sewageList = (List<Document>)reportData.get(sewageType);
		if(sewageList == null){
			return;
		}
		
		for(Document sewage : sewageList){
			storeObjectName(sewage, "tenderCompany", MongoKeys.DocumentName);
			isContainConstructionPeriod(sewage);
			storeObjectName(sewage, "waterStandard", MongoKeys.ItemName);
			storeLowercaseKey(sewage, "ROI");
			storeLowercaseKey(sewage, "ROE");
			
			MSWord subDoc = new MSWord();
			if(sewageType.equals("WSBOT")){
				subDoc.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_BOTSewage);
			}else if(sewageType.equals("WSTOT")){
				subDoc.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_TOTSewage);
			}else{
				return;
			}
			
			subDoc.insertBeforeBookmark(BOTTOT_refillBookmark, sewage);
			subDoc.replaceTableTexts(Sewage_Table_1, sewage);
			subDoc.replaceTableTexts(Sewage_Table_2, sewage);
			subDoc.replaceTableTexts(Sewage_Table_3, sewage);
			subDoc.replaceTableTexts(Sewage_Table_4, sewage);
			subDoc.setMinusToRed(Sewage_Table_4[0]);
			
			wordFile.addProfile(subDoc.getDocument(), bookmarkNameOfInsertPosition, insertCursor);
		}
			
	}
	
	private static final String[] Sanitation_Table_1 = {
			"Sanitation_Table_1",
		"scopeOfService",
		"serviceFee",
		"payMethod",
		"contractSize","reviewSize"
	};
	private static final String[] Sanitation_Table_2 = {
			"Sanitation_Table_2",
		"tenderCompany","bewg",
		"bankLoan","annualInterestRate","ownMoney",
		"aggregateInvestment","equipmentNew","takingLowLiquidity","other",
		"rentalFees",
		"personnelPlacementProgram",
		"operatingCostOfWater","totalWaterCost",
		"waterManagementFee","profitOfWater",
		"waterLaborCost","theAverageProfit"
	};
	private static final String[] Sanitation_Table_3 = {
			"Sanitation_Table_3",
		"netProfit","netProfitOne","netProfitTwo","netProfitThree","netProfitFour","netProfitFive",
		"investmentBase","investmentBaseOne","investmentBaseTwo","investmentBaseThree","investmentBaseFour","investmentBaseFive",
		"roi","ROIOne","ROITwo","ROIThree","ROIFour","ROIFive",
		"investmentActivity","investmentActivityOne","investmentActivityTwo","investmentActivityThree","investmentActivityFour","investmentActivityFive",
		"itsNetCashFlow","itsNetCashFlowOne","itsNetCashFlowTwo","itsNetCashFlowThree","itsNetCashFlowFour","itsNetCashFlowFive"
	};
	@SuppressWarnings("unchecked")
	private void insertSanitation(){
		if(!reportData.containsKey("HW")){
			return;
		}
		
		List<Document> sanitationList = (List<Document>)reportData.get("HW");
		for(Document sanitation : sanitationList){
			storeObjectName(sanitation, "tenderCompany", MongoKeys.DocumentName);
			storeLowercaseKey(sanitation, "ROI");
			
			MSWord subDoc = new MSWord();
			subDoc.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_sanitation);
			
			subDoc.insertBeforeBookmark(BasicSubRefillBookmark, sanitation);
			subDoc.replaceTableTexts(Sanitation_Table_1, sanitation);
			subDoc.replaceTableTexts(Sanitation_Table_2, sanitation);
			subDoc.replaceTableTexts(Sanitation_Table_3, sanitation);
			
			wordFile.addProfile(subDoc.getDocument(), bookmarkNameOfInsertPosition, insertCursor);
		}
	}
	
	private static final String[] em_Table_1 = {
			"em_Table_1",
		"entrustedOperatingPeriod","paymentMethod",
		"projectCraft","waterStandard",
		"agreementWaterprice","priceCicle",
		"guaranteedWater","measuringWater",
		"projectScale","reviewScale",
		"operatingCost","totalCost",
		"managementFees","profits",
		"laborCost","laborCreate"
	};
	private static final String[] em_Table_2 = {
			"em_Table_2",
		"npv",
		"netProfit","netProfitOne","netProfitTwo","netProfitThree","netProfitFour","netProfitFive"
	};
	@SuppressWarnings("unchecked")
	private void insertEntrustedManagement(){
		if(!reportData.containsKey("WTYY")){
			return;
		}
		
		List<Document> entrustedManagementList = (List<Document>)reportData.get("WTYY");
		for(Document entrustedManagement : entrustedManagementList){
			storeObjectName(entrustedManagement, "paymentMethod", MongoKeys.ItemName);
			storeObjectName(entrustedManagement, "waterStandard", MongoKeys.ItemName);
			storeLowercaseKey(entrustedManagement, "NPV");
			
			MSWord subDoc = new MSWord();
			subDoc.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_EntrustedManagement);
			
			subDoc.insertBeforeBookmark(BasicSubRefillBookmark, entrustedManagement);
			subDoc.replaceTableTexts(em_Table_1, entrustedManagement);
			subDoc.replaceTableTexts(em_Table_2, entrustedManagement);
			subDoc.setMinusToRed(em_Table_2[0]);
			
			wordFile.addProfile(subDoc.getDocument(), bookmarkNameOfInsertPosition, insertCursor);
		}
	}
	
	private void isContainConstructionPeriod(Document data){
		if(data.containsKey("containConstructionPeriod")){
			data.put("containConstructionPeriod", "");
		}else{
			data.put("containConstructionPeriod", "不");
		}
	}
	
	/**
	 * To avoid conflict with title in Template
	 * @param data
	 * @param key
	 */
	private void storeLowercaseKey(Document data, String key){
		if(data.containsKey(key)){
			data.put(key.toLowerCase(), data.get(key));
		}
	}
}
