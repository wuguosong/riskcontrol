package report;


import org.junit.Test;


import report.PreAssessmentReport;

public class ReportTest {
	
	@Test
	public void genPreAssessmentReport(){
		PreAssessmentReport test = new PreAssessmentReport();
		test.generateReport("58180783b1baea1eb8f5e89b");
	}
	
	@Test
	public void genPreApplication(){
//		PreApplication test = new PreApplication();
//		test.generateReport("58512828c8325f18b4ad08ab");//Pre
		
		FormalApplication test2 = new FormalApplication();
		test2.generateReport("5851ffdcc8325f17d0a1baed");//Formal
	}
	
	@Test
	public void genFormalAssessmentReport(){
		FormalAssessmentReportUtil.generateReport("581aed06b2e4171d1856cffc"); //SupplementReport OK
		FormalAssessmentReportUtil.generateReport("58131e3d9554bc260828a5a8"); //DropOutReport
		FormalAssessmentReportUtil.generateReport("5823ecc71db88c0dd0ee9171"); //TecTransformReport
		FormalAssessmentReportUtil.generateReport("581324e39554bc00cca54991"); //HazardousWasteReport  
		FormalAssessmentReportUtil.generateReport("581c344e2ec08f1ed07ba625"); //EquityAcquisitionReport
		FormalAssessmentReportUtil.generateReport("581301c99554bc1a084250c9"); //FormalAssessmentReport
	}
	
}
