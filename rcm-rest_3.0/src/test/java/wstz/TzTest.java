package wstz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
		investmentManager.put("name", "唐春江");
		investmentManager.put("value", "0001N610000000001DRM");
		apply.put("investmentManager", investmentManager);
		
		apply.put("projectNo", "CTSW20160321188");
		apply.put("investmentModel", true);
		
		List<JSONObject> projectModelList = new ArrayList<JSONObject>();
		JSONObject projectModel1 = new JSONObject();
		projectModel1.put("VALUE", "BOT");
		projectModel1.put("KEY", "1");
		JSONObject projectModel2 = new JSONObject();
		projectModel2.put("VALUE", "TOT");
		projectModel2.put("KEY", "2");
		projectModelList.add(projectModel1);
		projectModelList.add(projectModel2);
		apply.put("projectModel", projectModelList);
		
		apply.put("projectSize", "43吨/日");
		apply.put("projectNum", "2");
		
		json.put("apply", apply);
		json.put("is_supplement_review", "0");
		json.put("create_by", "c4943b9e-e491-457a-b804-579da173d017");
		json.put("create_name", "test001");
		
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
		apply.put("projectNo", "CTSW201512031");

		// investmentModel
		apply.put("investmentModel", true);

		// projectSize
		apply.put("projectSize", "43吨/日");

		// projectNum
		apply.put("projectNum", "2");

		json.put("apply", apply);

		// is_supplement_review
		json.put("is_supplement_review", "0");

		// create_by
		json.put("create_by", "c4943b9e-e491-457a-b804-579da173d017");

		// create_name
		json.put("create_name", "test001");

		String preResult = projectServiceForTz.createPre(JSON.toJSONString(json));
		System.out.println(preResult);
	}
}
