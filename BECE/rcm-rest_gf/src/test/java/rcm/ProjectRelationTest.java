
/**
 * 
 */
package rcm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;

import common.Constants;
import common.commonMethod;

/**
 * @Description: TODO
 * @Author zhangkewei
 * @Date 2016年10月19日 上午11:07:52  
 */
public class ProjectRelationTest {
	@Test
	public void testGetRelationUser(){
		ProjectRelation pr = new ProjectRelation();
		try {
//			pr.insertWhenStart("58088ddcd0e3dc34d88a24ac", "preAssessment:26:760004");
//			pr.insertWhenReviewLeaderComplete("5810b90afdcd9312649321d8", "formalAssessment:5:1047504");
			String json = "{businessId:'58088161d0e3dc3b146508b6',relationType:['0703','0707']}";
			List<Map<String, Object>> list = pr.findRelationUserByBusinessId(json);
			System.out.println(list.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testDeleteProjectRelation(){
		try {
			ProjectRelation pr = new ProjectRelation();
			pr.deleteProjectInfoByBusinessId("5808bac8fdcd9301a804ed32");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetRoleuserByCode(){
		commonMethod c = new commonMethod();
		List<Map<String, Object>> assigneeManagerList = c.getRoleuserByCode(Constants.TASK_ASSIGNEE_MANAGER);
		System.out.println(assigneeManagerList.get(0).toString());
	}
	/*******************************************测试ProjectInfo类*********************************************/
	@Test
	public void testInsertPreReviewBaseInfo(){
		try {
			String businessId = "58086f52fdcd930c98d0d1bd";
			ProjectInfo pre = new ProjectInfo();
			pre.saveReviewBaseInfo2Oracle(businessId,Constants.PRE_ASSESSMENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUpdaeProjectInfo(){
		String businessId = "5808a04ed0e3dc506854457e";
		try {
			ProjectInfo prj = new ProjectInfo();
			Map<String, Object> params = new HashMap<String, Object>();
			Date date = new Date();
			/*params.put("wfState", "1");
			params.put("applyDate", date);
			params.put("completeDate", date);*/
			params.put("reportCreateDate", date);
			prj.updateProjectInfo(businessId, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testGetPrjInfoByBid(){
		String businessId = "58074649d0e3dc3b582bb17a";
		try {
			ProjectInfo prj = new ProjectInfo();
			Map<String, Object> map = prj.selectByBusinessId(businessId);
			System.out.println("WF_STATE:"+map.get("WF_STATE"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void isAllreadyStartWithBusiness(){
		String businessId = "58074649d0e3dc3b582bb17g";
		try {
			ProjectInfo prj = new ProjectInfo();
			System.out.println(prj.isAllreadyStartWithBusiness(businessId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testDeleteProjectInfo(){
		String businessId = "5808bac8fdcd9301a804ed32";
		try {
			ProjectInfo prj = new ProjectInfo();
			prj.deleteProjectInfoByBusinessId(businessId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testSplit(){
		String str = "apply.grassrootsLegalStaff";
		String s[] =str.split("\\.");
		for(String d : s){
			System.out.println(d);
		}
	}
	
	@Test
	public void testUpdateReportInfo2Blank(){
		String businessId = "5816ed66da77c609f41aa76a";
		try {
			ProjectInfo prj = new ProjectInfo();
			prj.updateReportInfo2Blank(businessId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*@Test
	public void testProjectReportWithReviewLeader(){
		try {
			ProjectInfo prj = new ProjectInfo();
			List<Map<String, Object>> list = prj.getProjectReportWithReviewLeader();
			Gson gson = new Gson();
			System.out.println(gson.toJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
