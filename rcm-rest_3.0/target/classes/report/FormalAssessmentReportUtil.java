package report;

import java.util.HashMap;
import java.util.Map;

public class FormalAssessmentReportUtil{
	private static Map<String, Integer> reportTypeMap;
	
	static {
		reportTypeMap = new HashMap<String, Integer>();
		reportTypeMap.put("FormalReviewReport", 0);
		reportTypeMap.put("TecTransformReport", 1);
		reportTypeMap.put("DropOutReport", 2);
		reportTypeMap.put("HazardousWasteReport", 3);
		reportTypeMap.put("EquityAcquisitionReport", 4);
		reportTypeMap.put("SupplementReport", 5);
	}

	/**
	 * @param ObjectId _id of Constants.RCM_FORMALREPORT_INFO
	 * @return outputPath Path.formalAssessmentReportPath()+xxx.docx
	 */
	public static String generateReport(String ObjectId) {
		FormalAssessmentReport formalAssessmentReport = new FormalAssessmentReport(ObjectId);
		String outputPath;
		switch(reportTypeMap.get(formalAssessmentReport.getReportType())){
		case 0:
			formalAssessmentReport = new FormalReviewReport(ObjectId);
			break;
		case 1:
			formalAssessmentReport = new TecTransformReport(ObjectId);
			break;
		case 2:
			formalAssessmentReport = new DropOutReport(ObjectId);
			break;
		case 3:
			formalAssessmentReport = new HazardousWasteReport(ObjectId);
			break;
		case 4:
			formalAssessmentReport = new EquityAcquisitionReport(ObjectId);
			break;
		case 5:
			formalAssessmentReport = new SupplementReport(ObjectId);
			break;
		}
		
		outputPath = formalAssessmentReport.generateReport(ObjectId);
		return outputPath;
	}

}
