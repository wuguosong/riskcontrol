package report;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class WaterEnvironmentSelfBuiltReport extends FormalAssessmentReport {

    public WaterEnvironmentSelfBuiltReport(String ObjectId) {
        super(ObjectId);
    }

    //数组值对应word文档中的标签（依次点击插入、标签进行查看编写）
    private static final String[] refillBookmark = {
            "projectName", "create_name", "create_date",
            "projectOverview",
            "basicInformationDescription",
            "projectInvestmentDescription",
            "projectRevenueForecastStatement",
            "incomeEvaluation",
            "content",
            "lawContent",
            "technicalReviewOpinions",
            "financialProfessionalReviewOpinion",
            "drainageOpinion",
            "costCostReview",
            "explain",
            "conclusionAndSuggestions"
    };
    //Table_1对应为标签内容，其他内容对应表格中的要输入的字段，应与mengo中存储的一致（即与页面字段对应）
    //Table_1设置在表头第一行第一个单元格文字的后面系统可以定位
    private static final String[] Table_1 = {
            "Table_1",
            "projectType",
            "regionalBackground",
            "governmentProcurement", "unFillReasonOne", "projectProcedure", "unFillReasonTwo", "projectWarehouse", "unFillReasonThree", "signSubject", "unFillReasonFour",
            "serviceScope", "whetherConstructionIsCompleted", "whetherWeNeedToBuild",
            "projectProcess",
            "operationsUnit",
            "franchisePeriod", "isContainsConstruction",
            "qualityTarget", "waterStandard",
            "projectScale", "reviewScale"
    };
    //项目投资信息表信息Project investment
    private static final String[] Table_ProjectInvestment = {
            "Table_ProjectInvestment",
            "tenderCompany", "shareholding",
            "oweMoney", "bankLoan", "intentionBank", "annualInterestRate", "loanTimeLimit", "whetherToObtainLoanTermSheet", "whetherNeedAddLetter",
            "totalInvestment", "oneKindOfCost", "twoKindOfCost", "reserveFund", "supplementaryCommissioningFee", "secondaryFloorLiquidity", "consultingServiceFee", "other",
            "operatingCostOfWater", "totalWaterCost",
            "waterManagementFee", "profitOfWater",
            "waterLaborCost", "theAverageProfit"
    };
    //项目收入预测表信息Project revenue forecast
    private static final String[] Table_ProjectRevenueForecast = {
            "Table_ProjectRevenueForecast",
            "revenueForecastsLogicOne", "wetherCommissioningCharge", "commissioningCharge", "agreedWaterPrice", "bottomWater",
            "revenueForecastsLogicTwo", "wetherCommissioningChargeTwo", "commissioningChargeTwo", "billingBase", "billingBaseContent", "yearsOfPayment", "returnOnInvestment", "operationalProfitMargins",
            "revenueForecastsLogicThree", "tapWaterPrice", "productionAndMarketingRate", "waterRecovery"
    };
    //项目收入预测表可能需要根据项目类型分为3三个表
    private static final String[] Table1_ProjectRevenueForecast = {
            "Table1_ProjectRevenueForecast",
            "revenueForecastsLogicOne", "wetherCommissioningCharge", "commissioningCharge", "agreedWaterPrice", "bottomWater"
    };
    private static final String[] Table2_ProjectRevenueForecast = {
            "Table2_ProjectRevenueForecast",
            "revenueForecastsLogicTwo", "wetherCommissioningChargeTwo", "commissioningChargeTwo", "billingBase", "billingBaseContent", "yearsOfPayment", "returnOnInvestment", "operationalProfitMargins"
    };
    private static final String[] Table3_ProjectRevenueForecast = {
            "Table3_ProjectRevenueForecast",
            "revenueForecastsLogicThree", "tapWaterPrice", "productionAndMarketingRate", "waterRecovery"
    };
    //资金使用表fundUse
    private static final String[] Table_fundUse = {
            "Table_fundUse",
            "constructionPhase", "transitionPhase", "operationStage", "sumCapitalRequirement"
    };
    //建设阶段或移交阶段
    private static final String[] Table_fundUsetableOneConditions = {
            "Table_fundUse", "2",
            "fundsUseTimeAA", "capitalRequirementAA", "fundUseAA", "paymentTermsAA", "sourceOfFundsAA",
            "fundsUseTimeAB", "capitalRequirementAB", "fundUseAB", "paymentTermsAB", "sourceOfFundsAB"

    };
    //运营阶段表格信息
    private static final String[] Table_fundUsetableTwoConditions = {
            "Table_fundUse", "5",
            "fundsUseTimeBA", "capitalRequirementBA", "fundUseBA", "paymentTermsBA", "sourceOfFundsBA",
            "fundsUseTimeBB", "capitalRequirementBB", "fundUseBB", "paymentTermsBB", "sourceOfFundsBB"
    };
    //项目收益表 项目各项关键财务指标表
    private static final String[] Table5_IncomeEvaluation = {
            "Table5_IncomeEvaluation",
            "allInternalRateOfReturn",
            "haveInternalRateOfReturn",
            "staticPaybackPeriod",
            "averageROE",
            "averageROI",
            "netCashFlow"
    };
    //项目运营前5年关键财务指标汇总表（单位：人民币万元）
    private static final String[] Table6_IncomeEvaluation = {
            "Table6_IncomeEvaluation",
            "netProfitAll", "netProfitAllOne", "netProfitAllTwo", "netProfitAllThree", "netProfitAllFour", "netProfitAllFive",
            "roi", "ROIOne", "ROITwo", "ROIThree", "ROIFour", "ROIFive",
            "activeCashFlow", "activeCashFlowOne", "activeCashFlowTwo", "activeCashFlowThree", "activeCashFlowFour", "activeCashFlowFive",
            "operatingCashFlow", "operatingCashFlowOne", "operatingCashFlowTwo", "operatingCashFlowThree", "operatingCashFlowFour", "operatingCashFlowFive",
            "ownFunds",
            "netProfit", "netProfitOne", "netProfitTwo", "netProfitThree", "netProfitFour", "netProfitFive",
            "roe", "ROEOne", "ROETwo", "ROEThree", "ROEFour", "ROEFive",
            "itsNetCashFlow", "itsNetCashFlowOne", "itsNetCashFlowTwo", "itsNetCashFlowThree", "itsNetCashFlowFour", "itsNetCashFlowFive",
            "dividendCashFlow", "dividendCashFlowOne", "dividendCashFlowTwo", "dividendCashFlowThree", "dividendCashFlowFour", "dividendCashFlowFive"
    };
    //建设期对集团会计利润贡献情况表
    private static final String[] Table7_profitContribution = {
            "Table7_profitContribution",
            "year", "expectedCompletionRatio", "expectedOutputValue", "grossProfit",
            "yearTwo", "expectedCompletionRatioTwo", "expectedOutputValueTwo", "grossProfitTwo",
            "sumExpectedCompletionRatio"
    };
    //风险及问题总结
    private static final String[] Table_leadershipDecision_new = {
            "Table_projectConcernsIssues", "2",
            "rowIdAA", "riskPointAA", "riskContentAA", "pointsAndProblemsAA",
            "rowIdAB", "riskPointAB", "riskContentAB", "pointsAndProblemsAB"
    };
    private static final String[] Table_implementationRequirements_new = {
            "Table_projectConcernsIssues", "4",
            "rowIdBA", "riskPointBA", "riskContentBA", "pointsAndProblemsBA",
            "rowIdBB", "riskPointBB", "riskContentBB", "pointsAndProblemsBB"
    };
    private static final String[] Table9_riskAnalysis = {
            "Table9_riskAnalysis",
            "riskOfTransshipmentOne", "riskLevelOne",
            "riskOfTransshipmentTwo",
            "riskOfTransshipmentThree",
            "riskOfTransshipmentFour",
            "riskOfTransshipmentFive",
            "riskOfTransshipmentSix",
            "paymentRiskOne", "riskLevelTwo",
            "paymentRiskTwo",
            "paymentRiskThree",
            "financingRiskOne", "riskLevelThree",
            "financingRiskTwo",
            "financingRiskThree",
            "financingRiskFour",
            "financingRiskFive",
    };
    private static final String bookmarkNameOfInsertPosition = "insertMark";
    private int insertCursor[] = {-1, -1};

    @SuppressWarnings("unchecked")
    @Override
    public String generateReport(String ObjectId) {
        //设置模板
        wordFile.setTemplate(Path.FORMAL_ASSESSMENT_REPORT_WaterEnvironmentSelfBuilt);
        //设置页眉
        wordFile.addHeader(reportData.getString(MongoKeys.ProjectName), reportData.getString("create_date"));
        // add by lipan92 2019/11/19 start
        reportData = this.resetDocumentProperty(reportData);
        // add by lipan92 2019/11/19 end
        //设置直接显示内容；如：项目边界条件 下的文字描述
        wordFile.insertBeforeBookmark(refillBookmark, reportData);
        //设置字段中的Boolean值为对应的String内容
        setBooleanToString(reportData);
        //设置数据字典值
        //storeObjectName(reportData, "waterStandard", MongoKeys.ItemName);
        //storeObjectName(reportData, "tenderCompany", MongoKeys.DocumentName);
        //替换表格内容（项目基础信息）
        wordFile.replaceTableTexts(Table_1, reportData);
        //项目投资信息内容写入
        wordFile.replaceTableTexts(Table_ProjectInvestment, reportData);
        //项目收入预测内容写入
        wordFile.replaceTableTexts(Table_ProjectRevenueForecast, reportData);
        //资金使用计划表内容写入
        wordFile.replaceTableTexts(Table_fundUse, reportData);
        Document projectConcernsIssues = (Document) reportData.get(MongoKeys.ProjectConcernsIssues);
        List<Document> fundUsetableOneConditions = (List<Document>) projectConcernsIssues.get("fundUsetableOneConditions");
        List<Document> fundUsetableTwoConditions = (List<Document>) projectConcernsIssues.get("fundUsetableTwoConditions");
        this.replaceTableTexts_haveStableColumn_new(Table_fundUsetableOneConditions, fundUsetableOneConditions);
        this.replaceTableTexts_haveStableColumn_new(Table_fundUsetableTwoConditions, fundUsetableTwoConditions);
        //项目收益内容写入
        //storeLowercaseKey(reportData, "ROI");
        //storeLowercaseKey(reportData, "ROE");
        //if(reportData.containsKey("dividendCashFlow")){
        //reportData.put("dividendCashFlo", reportData.get("dividendCashFlow"));
        //}
        wordFile.replaceTableTexts(Table5_IncomeEvaluation, reportData);
        wordFile.replaceTableTexts(Table6_IncomeEvaluation, reportData);

        //建设期对集团会计利润贡献情况表内容写入
        wordFile.replaceTableTexts(Table7_profitContribution, reportData);

        insertProjectConcernsIssuesAllNew();
        insertPotentialSpace();
        insertOptionList();
        insertRequireList();
        insertProfessionalOpinionsAll();
        insertCostEstimate();
        // insertDrugList();
        String outputPath = Path.formalAssessmentReportPath() + "WaterEnvironmentSelfBuilt" + ObjectId + System.currentTimeMillis() + ".docx";
        wordFile.saveAs(outputPath);
        return outputPath;
    }

    private static final String STR_1 = "1";

    private void setBooleanToString(Document data) {
        //设置项目合规性四个选择框的值
        if (data.containsKey("governmentProcurement") &&
                STR_1.equals(data.getString("governmentProcurement"))) {
            data.put("governmentProcurement", "已");
        } else {
            data.put("governmentProcurement", "未");
        }
        if (data.containsKey("projectProcedure") &&
                STR_1.equals(data.getString("projectProcedure"))) {
            data.put("projectProcedure", "已");
        } else {
            data.put("projectProcedure", "未");
        }
        if (data.containsKey("projectWarehouse") &&
                STR_1.equals(data.getString("projectWarehouse"))) {
            data.put("projectWarehouse", "已");
        } else {
            data.put("projectWarehouse", "未");
        }
        if (data.containsKey("signSubject") &&
                STR_1.equals(data.getString("signSubject"))) {
            data.put("signSubject", "已");
        } else {
            data.put("signSubject", "未");
        }
        //设置服务范围两个选项
        if (data.containsKey("whetherConstructionIsCompleted") &&
                STR_1.equals(data.getString("whetherConstructionIsCompleted"))) {
            data.put("whetherConstructionIsCompleted", "是");
        } else {
            data.put("whetherConstructionIsCompleted", "否");
        }
        if (data.containsKey("whetherWeNeedToBuild") &&
                STR_1.equals(data.getString("whetherWeNeedToBuild"))) {
            data.put("whetherWeNeedToBuild", "是");
        } else {
            data.put("whetherWeNeedToBuild", "否");
        }
        //设置特许经营期选择框
        if (data.containsKey("isContainsConstruction") &&
                STR_1.equals(data.getString("isContainsConstruction"))) {
            data.put("isContainsConstruction", "");
        } else {
            data.put("isContainsConstruction", "未");
        }
        //设置项目投资信息中资金来源的选择框
        if (data.containsKey("whetherToObtainLoanTermSheet") &&
                STR_1.equals(data.getString("whetherToObtainLoanTermSheet"))) {
            data.put("whetherToObtainLoanTermSheet", "已");
        } else {
            data.put("whetherToObtainLoanTermSheet", "未");
        }
        if (data.containsKey("whetherNeedAddLetter") &&
                STR_1.equals(data.getString("whetherNeedAddLetter"))) {
            data.put("whetherNeedAddLetter", "");
        } else {
            data.put("whetherNeedAddLetter", "不");
        }
        //设置项目收入预测中的选择框
        if (data.containsKey("wetherCommissioningCharge") && data.getBoolean("wetherCommissioningCharge")) {
            data.put("wetherCommissioningCharge", "是");
        } else {
            data.put("wetherCommissioningCharge", "否");
        }
        if (data.containsKey("wetherCommissioningChargeTwo") &&
                STR_1.equals(data.getString("wetherCommissioningChargeTwo"))) {
            data.put("wetherCommissioningChargeTwo", "是");
        } else {
            data.put("wetherCommissioningChargeTwo", "否");
        }
    }

    //用于表格中存在新的表格的内容，且表格从第一列开始的（如资金使用计划表）
    protected void replaceTableTexts_haveStableColumn_new(String[] marks, List<Document> data) {
        int rowSize = 0;
        if (data != null) {
            rowSize = data.size();
        }
        int cellSize = (marks.length - 2) / 2;
        if (rowSize == 0) {
            wordFile.removeTableRowByBookmark(marks[2]);
            wordFile.removeTableRowByBookmark(marks[cellSize + 2]);
        } else if (rowSize == 1) {
            replace1stRowNew(marks, data.get(0));
            wordFile.removeTableRowByBookmark(marks[cellSize + 2]);
        } else {
            replaceFrom2ndRowNew(marks, data);
        }
    }

    private void replace1stRowNew(String[] marks, Document data) {
        int contentSize = (marks.length - 2) / 2;
        int cellSize = contentSize;
        String[] newMarks = new String[cellSize + 2];

        newMarks[0] = marks[0];
        newMarks[1] = wordFile.getRowNumOfBookmark(marks[2]) + "";
        for (int i = 2; i < newMarks.length; i++) {
            newMarks[i] = marks[i];
        }

        List<Document> content = new ArrayList<Document>();
        int endPos = marks.length - contentSize;
        int bookmarkPos = 2;
        Document row = new Document();
        for (int j = bookmarkPos; j < endPos; j++) {
            String key = marks[j].substring(0, marks[j].length() - 2);
            if (data.containsKey(key)) {
                row.put(marks[j], data.getString(key));
            } else {
                row.put(marks[j], "");
            }
        }
        content.add(row);


        wordFile.fillTableByRow(newMarks, content, false);
    }

    private void replaceFrom2ndRowNew(String[] marks, List<Document> data) {
        replace1stRowNew(marks, data.get(0));

        int contentSize = (marks.length - 2) / 2;
        int cellSize = contentSize;
        String[] newMarks = new String[cellSize + 2];

        newMarks[0] = marks[0];
        newMarks[1] = wordFile.getRowNumOfBookmark(marks[contentSize + 2]) + "";
        for (int i = 2; i < newMarks.length; i++) {
            newMarks[i] = marks[i + contentSize];
        }

        List<Document> content = new ArrayList<Document>();
        int endPos = marks.length;
        int bookmarkPos = contentSize + 2;
        for (int i = 1; i < data.size(); i++) {
            Document row = new Document();
            Document rowData = data.get(i);
            for (int j = bookmarkPos; j < endPos; j++) {
                String key = marks[j].substring(0, marks[j].length() - 2);
                if (rowData.containsKey(key)) {
                    row.put(marks[j], rowData.getString(key));
                } else {
                    row.put(marks[j], "");
                }
            }
            content.add(row);
        }

        wordFile.fillTableByRow(newMarks, content, false);

    }

    private void storeLowercaseKey(Document data, String key) {
        if (data.containsKey(key)) {
            data.put(key.toLowerCase(), data.get(key));
        }
    }

    @SuppressWarnings("unchecked")
    protected void insertProjectConcernsIssuesNew() {
        Document projectConcernsIssues = (Document) reportData.get(MongoKeys.ProjectConcernsIssues);
        List<Document> leadershipDecision = (List<Document>) projectConcernsIssues.get("leadershipDecision");
        replaceTableTexts_haveStableColumn(Table_leadershipDecision_new, leadershipDecision);
    }

    @SuppressWarnings("unchecked")
    protected void insertProjectConcernsIssuesAllNew() {
        Document projectConcernsIssues = (Document) reportData.get(MongoKeys.ProjectConcernsIssues);
        insertProjectConcernsIssuesNew();
        List<Document> prompt = (List<Document>) projectConcernsIssues.get("prompt");
        replaceTableTexts_haveStableColumn(Table_implementationRequirements_new, prompt);
        //风险分析数据xi
        wordFile.replaceTableTexts(Table9_riskAnalysis, reportData);

    }

    /**
     * @param doc Mongo原始结构
     * @return Document
     * @author lipan92
     * @description 将Mongo中存储的数据结构重构为生成Word需要的数据结构
     * @date 2019/11/20 0020 9:48
     **/
    private Document resetDocumentProperty(Document doc) {
        Document newDoc = new Document();
        newDoc.putAll(doc);
        // boundary1
        Document boundary1 = (Document) newDoc.get("boundary1");
        if (boundary1 != null) {
            // start
            String projectType = "";
            List<Document> projectMode = (List<Document>) boundary1.get("projectModel");
            if (CollectionUtils.isNotEmpty(projectMode)) {
                StringBuilder sb = new StringBuilder("");
                for (Document document : projectMode) {
                    sb.append(document.getString("VALUE"));
                    sb.append(" ");
                }
                projectType = sb.toString();
            }
            newDoc.put("projectType", projectType);
            String waterStandard = "";
            Document waterStandardDoc = (Document) boundary1.get("waterStandard");
            if (waterStandardDoc != null) {
                waterStandard = waterStandardDoc.getString("ITEM_NAME");
            }
            newDoc.put("waterStandard", waterStandard);
            String tenderCompany = "";
            Document tenderCompanyDoc = (Document) boundary1.get("tenderCompany");
            if (tenderCompanyDoc != null) {
                tenderCompany = tenderCompanyDoc.getString("name");
            }
            newDoc.put("tenderCompany", tenderCompany);
            Document projectConcernsIssuesDoc = (Document) boundary1.get("projectConcernsIssues");
            newDoc.put("projectConcernsIssues", projectConcernsIssuesDoc);
            String basicInformationDescription = boundary1.getString("basicInformationDescription");
            if (StringUtils.isBlank(basicInformationDescription)) {
                basicInformationDescription = "";
            }
            newDoc.put("basicInformationDescription", basicInformationDescription);
            String projectInvestmentDescription = boundary1.getString("projectInvestmentDescription");
            if (StringUtils.isBlank(projectInvestmentDescription)) {
                projectInvestmentDescription = "";
            }
            newDoc.put("projectInvestmentDescription", projectInvestmentDescription);
            String projectRevenueForecastStatement = boundary1.getString("projectRevenueForecastStatement");
            if (StringUtils.isBlank(projectRevenueForecastStatement)) {
                projectRevenueForecastStatement = "";
            }
            newDoc.put("projectRevenueForecastStatement", projectRevenueForecastStatement);
            // end
            List<String> keys = new ArrayList();
            keys = this.arrayAddToList(Table_1, keys);
            keys = this.arrayAddToList(Table_ProjectInvestment, keys);
            keys = this.arrayAddToList(Table_ProjectRevenueForecast, keys);
            keys = this.arrayAddToList(Table_fundUse, keys);
            for (String key : keys) {
                if (!newDoc.containsKey(key)) {
                    newDoc.put(key, boundary1.get(key));
                }
            }
        }
        // earnings2
        Document earnings2 = (Document) newDoc.get("earnings2");
        if (earnings2 != null) {
            String incomeEvaluation = earnings2.getString("incomeEvaluation");
            if (StringUtils.isBlank(incomeEvaluation)) {
                incomeEvaluation = "";
            }
            newDoc.put("incomeEvaluation", incomeEvaluation);
            // 项目收益评估
            List<String> keys = new ArrayList();
            keys = this.arrayAddToList(Table5_IncomeEvaluation, keys);
            keys = this.arrayAddToList(Table6_IncomeEvaluation, keys);
            for (String key : keys) {
                if (!newDoc.containsKey(key)) {
                    newDoc.put(key, earnings2.get(key));
                }
            }
            // 单独设置roi和roe
            newDoc.put("roi", earnings2.get("ROI"));
            newDoc.put("roe", earnings2.get("ROE"));
        }
        // risk3
        Document risk3 = (Document) newDoc.get("risks3");
        if (risk3 != null) {
            List<String> keys = new ArrayList();
            keys = this.arrayAddToList(Table9_riskAnalysis, keys);
            for (String key : keys) {
                if (!newDoc.containsKey(key)) {
                    newDoc.put(key, risk3.get(key));
                }
            }
            newDoc.put("projectConcernsIssues", risk3.get("projectConcernsIssues"));
        }
        // interspace4
        String potentialSpace = "";// 挖潜空间
        Document interspace4 = (Document) newDoc.get("interspace4");
        if (interspace4 != null) {
            potentialSpace = interspace4.getString("potentialSpace");
        }
        newDoc.put("potentialSpace", potentialSpace);
        // conclusion5
        String conclusionAndSuggestions = "";// 结论与建议
        Document conclusion5 = (Document) newDoc.get("conclusion5");
        if (conclusion5 != null) {
            conclusionAndSuggestions = conclusion5.getString("conclusionAndSuggestions");
            newDoc.put("optionList", conclusion5.get("optionList"));// 项目投前必须落实事项
        }
        newDoc.put("conclusionAndSuggestions", conclusionAndSuggestions);
        // execute6
        Document execute6 = (Document) newDoc.get("execute6");
        if (execute6 != null) {
            newDoc.put("requireList", execute6.get("requireList"));// 后续要求
        }
        // attch7
        Document attch7 = (Document) newDoc.get("attch7");
        if (attch7 != null) {
            String lawContent = attch7.getString("lawContent");
            if (StringUtils.isBlank(lawContent)) {
                lawContent = "";
            }
            newDoc.put("lawContent", lawContent);
            String technicalReviewOpinions = attch7.getString("technicalReviewOpinions");
            if (StringUtils.isBlank(technicalReviewOpinions)) {
                technicalReviewOpinions = "";
            }
            newDoc.put("technicalReviewOpinions", technicalReviewOpinions);
            String financialProfessionalReviewOpinion = attch7.getString("financialProfessionalReviewOpinion");
            if (StringUtils.isBlank(financialProfessionalReviewOpinion)) {
                financialProfessionalReviewOpinion = "";
            }
            newDoc.put("financialProfessionalReviewOpinion", financialProfessionalReviewOpinion);
            String drainageOpinion = attch7.getString("drainageOpinion");
            if (StringUtils.isBlank(drainageOpinion)) {
                drainageOpinion = "";
            }
            newDoc.put("drainageOpinion", drainageOpinion);
            newDoc.put("lawList", attch7.get("lawList"));// 法律专业评审意见
            newDoc.put("technologyList", attch7.get("technologyList"));// 技术专业评审意见
            newDoc.put("financeList", attch7.get("financeList"));// 财务专业评审意见
            newDoc.put("drainageList", attch7.get("drainageList"));// 建管专业评审意见
        }
        // attch8
        Document attch8 = (Document) newDoc.get("attch8");
        if (attch8 != null) {
            String costCostReview = attch8.getString("costCostReview");
            if (StringUtils.isBlank(costCostReview)) {
                costCostReview = "";
            }
            newDoc.put("costCostReview", costCostReview);
            String explain = attch8.getString("explain");
            if (StringUtils.isBlank(explain)) {
                explain = "";
            }
            newDoc.put("explain", explain);
            // 评审用成本及费用
            Document costEstimateDoc = (Document) attch8.get("costEstimate");
            newDoc.put("costEstimate", costEstimateDoc);
            if(costEstimateDoc != null){
                newDoc.put("drugList", costEstimateDoc.get("drugList"));
            }
        }
        return newDoc;
    }

    /**
     * @param array 数组
     * @param list  集合
     * @return List<E>
     * @author lipan92
     * @description 将数组添加到集合中
     * @date 2019/11/20 0020 9:50
     **/
    private <E> List<E> arrayAddToList(E[] array, List<E> list) {
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList();
        }
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                list.add(array[i]);
            }
        }
        return list;
    }
}
