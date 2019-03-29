package report;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import rcm.ProjectInfo;

class BaseApplication extends BaseReport{

	private static final String[] refillBookmark = {
			"projectName"
	};
	
	private static final String[] Table = {
			"Table",
		"reportingUnit",
		"companyCategory",
		"companyHeader",
		"investmentManager","grassrootsLegalStaff",
		"projectAddress",
		"projectName",
		"serviceType","projectType",
		"investmentModel","projectModel",
		"projectSize","projectNum",
		"competitor",
		"tenderTime",
		"paymentTime",
		"investmentPerson","directPerson",
		"opinion"
	};
	
	private static final String[] Names = {
		"reportingUnit",
		"companyHeader",
		"investmentManager","grassrootsLegalStaff",
		"investmentPerson","directPerson",
	};
	
	private static final String[] TypeLists = {
			"serviceType", 
			"projectType", 
			"projectModel"
	};
	
	private static final String[] ItemNames = {
		"companyCategory"
	};
	
	@Override
	public String generateReport(String ObjectId) {
		initData(ObjectId);
		
		wordFile.insertBeforeBookmark(refillBookmark, reportData);
		wordFile.replaceTableTexts(Table, reportData);
		
		return "";
	}

	protected void initData(String id){
		connectMongoDB();
		
		JSONObject requirement = new JSONObject();
		try {
			requirement.put("taskDefKey", "usertask16");
			requirement.put("businessId", id);
		} catch (JSONException e) {
			logger.info("requirement for get task opinion is wrong.");
			e.printStackTrace();
		}
		
		List<Map<String, Object>> taskOpinion = new ProjectInfo().getTaskOpinion(requirement.toString());
		String opinion = "";
		if(!taskOpinion.isEmpty()){
			opinion = taskOpinion.get(0).get("OPINION").toString();
		}
		
		reportData.put("opinion", opinion);
		
		storeObjectName(reportData, Names, MongoKeys.DocumentName);
		storeObjectName(reportData, ItemNames, MongoKeys.ItemName);
		storeListName(reportData, TypeLists, MongoKeys.TypeName, "/");
		dateFormate(reportData, "tenderTime");
		dateFormate(reportData, "paymentTime");
		
		if(reportData.getBoolean("investmentModel")){
			reportData.put("investmentModel", "");
		}else{
			reportData.put("investmentModel", "非");
		}
		
		closeMongoDB();
	}
}
