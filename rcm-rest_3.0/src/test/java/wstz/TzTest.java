package wstz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yk.rcm.ws.client.tz.RiskService;
import com.yk.rcm.ws.client.tz.RiskServiceSoap;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ws.tz.client.ProjectServiceForTz;
import ws.tz.client.ProjectServiceForTzSoap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/7/5 0005.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class TzTest {
	@Test
	public void createPfrTest() {
		ProjectServiceForTz projectServiceForTz = new ProjectServiceForTzSoap().getProjectServiceForTzImplPort();
		JSONObject json = new JSONObject();
		// 构造正式评审信息
		JSONObject apply = new JSONObject();
		// 投资经理
		JSONObject investmentManager = new JSONObject();
		investmentManager.put("name", "梁晶晶");
		investmentManager.put("value", "0001N61000000000O93V");
		apply.put("investmentManager", investmentManager);

		apply.put("projectNo", "CTSW20160321188");
		apply.put("investmentModel", true);

		List<JSONObject> projectModelList = new ArrayList<JSONObject>();
		JSONObject projectModel1 = new JSONObject();
		projectModel1.put("VALUE", "BOT");
		projectModel1.put("KEY", "1");
//		JSONObject projectModel2 = new JSONObject();
//		projectModel2.put("VALUE", "TOT");
//		projectModel2.put("KEY", "2");
		projectModelList.add(projectModel1);
//		projectModelList.add(projectModel2);
		apply.put("projectModel", projectModelList);

		apply.put("projectSize", "10.0000万吨");
		apply.put("projectNum", "1");
		apply.put("investMoney", "48.0000");
		apply.put("supplementaryItem", "2222222222");

		json.put("apply", apply);
		json.put("is_supplement_review", "1");
		json.put("create_by", "0001N61000000000O93V");
		json.put("create_name", "梁晶晶");
//		json.put("businessid", "5d37dadbbdbe5808244c0d36");

		String pfrResult = projectServiceForTz.createPfr(JSON.toJSONString(json));
		System.out.println(pfrResult);
	}

	@Test
	public void createPreTest() {
		ProjectServiceForTz projectServiceForTz = new ProjectServiceForTzSoap().getProjectServiceForTzImplPort();
		JSONObject json = new JSONObject();
		// 构造正式评审信息
		JSONObject apply = new JSONObject();

		// 投资经理
		JSONObject investmentManager = new JSONObject();
		investmentManager.put("name", "唐春江");
		investmentManager.put("value", "0001N610000000001DRM");
		apply.put("investmentManager", investmentManager);

		// 创建人
		apply.put("create_name", "唐春江");
		apply.put("create_by", "0001N610000000001DRM");

		// 创建projectNo
		apply.put("projectNo", "CTSW20160321188");

		// investmentModel
		apply.put("investmentModel", false);

		List<JSONObject> projectModelList = new ArrayList<JSONObject>();
		JSONObject projectModel1 = new JSONObject();
		projectModel1.put("VALUE", "BOT");
		projectModel1.put("KEY", "1");
//		JSONObject projectModel2 = new JSONObject();
//		projectModel2.put("VALUE", "TOT");
//		projectModel2.put("KEY", "2");
		projectModelList.add(projectModel1);
//		projectModelList.add(projectModel2);
		apply.put("projectModel", projectModelList);

		apply.put("projectSize", "1000");

		json.put("apply", apply);
		// create_by
		json.put("create_by", "0001N610000000001DRM");
		// create_name
		json.put("create_name", "唐春江");

		String preResult = projectServiceForTz.createPre(JSON.toJSONString(json));
		System.out.println(preResult);
	}

	@Test
	public void deletePfrTest() {
		ProjectServiceForTz projectServiceForTz = new ProjectServiceForTzSoap().getProjectServiceForTzImplPort();
		JSONObject json = new JSONObject();
		json.put("businessid", "5d36d750ddd03454c9a257d6");
		String pfrResult = projectServiceForTz.deletePfr(JSON.toJSONString(json));
		System.out.println(pfrResult);
	}

	@Test
	public void deletePreTest() {
		ProjectServiceForTz projectServiceForTz = new ProjectServiceForTzSoap().getProjectServiceForTzImplPort();
		JSONObject json = new JSONObject();
		json.put("businessid", "5d36cf2dddd0346444c9a02f");
		String pfrResult = projectServiceForTz.deletePre(JSON.toJSONString(json));
		System.out.println(pfrResult);
	}

	@Test
	public void updatePfrRiskAuditInfo() {
		RiskServiceSoap rs = new RiskService().getRiskServiceSoap();
		String customerId = "5d381af2ddd034596f5abcda";
		String riskStatus = "1";
		String auditReport = "";
		String result = rs.updateRiskAuditInfo(customerId, riskStatus, auditReport);
		System.out.println(result);
	}

	@Test
	public void updatePreRiskAuditInfo() {
		RiskServiceSoap rs = new RiskService().getRiskServiceSoap();
		String customerId = "5d37bc43ddd03423bf52dedd";
		String riskStatus = "1";
		String auditReport = "";
		String result = rs.updateRiskAuditInfo(customerId, riskStatus, auditReport);
		System.out.println(result);
	}

	@Test
	public void getYesPfrDecisionInfo() {
//		DecisionOpinion
//		1 : 同意投资
//		2 : 不同意投资
//		3 : 同意有条件投资
//		4 : 择期决议
		RiskServiceSoap rs = new RiskService().getRiskServiceSoap();
		JSONObject json = new JSONObject();
		json.put("CusTomerId", "5d381af2ddd034596f5abcda");
		json.put("IsHaveMeeting", true);// true : false
		json.put("DecisionOpinion", "1");
		json.put("AuditCompleteDate", "2019-07-24");
		String result = rs.getPfrDecisionInfo(JSON.toJSONString(json));
		System.out.println(result);
	}

	@Test
	public void getNoPfrDecisionInfo() {
		RiskServiceSoap rs = new RiskService().getRiskServiceSoap();
		JSONObject json = new JSONObject();
		json.put("CusTomerId", "5d381af2ddd034596f5abcda");
		json.put("IsHaveMeeting", false);// true : false
		String result = rs.getPfrDecisionInfo(JSON.toJSONString(json));
		System.out.println(result);
	}

	@Test
	public void getYesPreDecisionInfo() {
		RiskServiceSoap rs = new RiskService().getRiskServiceSoap();
		JSONObject json = new JSONObject();
		json.put("CusTomerId", "5d37bc43ddd03423bf52dedd");
		json.put("IsHaveMeeting", true);// true : false
		json.put("DecisionOpinion", "2");
		json.put("Remark", "这是我通过接口传递的数据");
		json.put("AuditCompleteDate", "2019-07-24");
		String result = rs.getPreDecisionInfo(JSON.toJSONString(json));
		System.out.println(result);
	}
	
	@Test
	public void getNoPreDecisionInfo() {
		RiskServiceSoap rs = new RiskService().getRiskServiceSoap();
		JSONObject json = new JSONObject();
		json.put("CusTomerId", "5d37bc43ddd03423bf52dedd");
		json.put("IsHaveMeeting", false);// true : false
		json.put("Remark", "这是我通过接口传递的数据");
		String result = rs.getPreDecisionInfo(JSON.toJSONString(json));
		System.out.println(result);
	}

}
