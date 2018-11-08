package report;

import org.bson.Document;

import projectPreReview.ProjectPreReview;

public class PreApplication extends BaseApplication{

	@Override
	/**
	 * @param id _id of Constants.PREASSESSMENT
	 * @return outputPath Path.preAssessmentReportPath()+xxx.docx
	 */
	public String generateReport(String ObjectId) {
		logger.info("---start generate PreApplication---");
		long startTime = System.currentTimeMillis();
		wordFile.setTemplate(Path.PRE_APPLICATION);
		
		super.generateReport(ObjectId);
		
		String outputPath = Path.preAssessmentReportPath() + "PreApplication"+ObjectId+System.currentTimeMillis()+".docx";
		wordFile.saveAs(outputPath);
		logger.info("---end " + (System.currentTimeMillis() - startTime) + "---");
		logger.info("The application is generated at '" + outputPath + "'.");
		return outputPath;
	}

	@Override
	protected void initData(String id) {
		ProjectPreReview dataSource = new ProjectPreReview();
		Document projectData = (Document) dataSource.getProjectPreReviewByID(id);
		reportData = (Document) projectData.get(MongoKeys.ApplyInfo);

		super.initData(id);
	}
	
}
