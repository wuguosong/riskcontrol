package wstz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
        // 项目单位负责人
        JSONObject companyHeader = new JSONObject();
        companyHeader.put("name", "唐春江");
        companyHeader.put("value", "0001N610000000001DRM");
        apply.put("companyHeader", companyHeader);
        // 一级业务类型
        List<JSONObject> serviceType = new ArrayList();
        JSONObject serviceType0 = new JSONObject();
        serviceType0.put("KEY", "1402");
        serviceType0.put("VALUE", "水环境");
        serviceType.add(serviceType0);
        apply.put("serviceType", serviceType);
        // 大区信息
        JSONObject reportingUnit = new JSONObject();
        reportingUnit.put("name", "四川综合业务区");
        reportingUnit.put("value", "0001D210000000000GAQ");
        apply.put("reportingUnit", reportingUnit);
        // 创建人
        apply.put("create_name", "唐春江");
        apply.put("create_by", "0001N610000000001DRM");
        // 基层法务
        JSONObject grassrootsLegalStaff = new JSONObject();
        grassrootsLegalStaff.put("name", "欧阳强鹏");
        grassrootsLegalStaff.put("value", "0001N6100000000HEF2N");
        apply.put("grassrootsLegalStaff", grassrootsLegalStaff);
        // directPerson
        JSONObject directPerson = new JSONObject();
        directPerson.put("name", "徐东升");
        directPerson.put("value", "0001N610000000049ANS");
        apply.put("directPerson", directPerson);
        // investmentPerson
        JSONObject investmentPerson = new JSONObject();
        investmentPerson.put("name", "徐东升");
        investmentPerson.put("value", "0001N610000000049ANS");
        // investmentModel
        apply.put("investmentPerson", investmentPerson);
        apply.put("investmentModel", true);
        json.put("apply", apply);
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
        // 项目单位负责人
        JSONObject companyHeader = new JSONObject();
        companyHeader.put("name", "唐春江");
        companyHeader.put("value", "0001N610000000001DRM");
        apply.put("companyHeader", companyHeader);
        // 一级业务类型
        List<JSONObject> serviceType = new ArrayList();
        JSONObject serviceType0 = new JSONObject();
        serviceType0.put("KEY", "1402");
        serviceType0.put("VALUE", "水环境");
        serviceType.add(serviceType0);
        apply.put("serviceType", serviceType);
        // 大区信息
        JSONObject reportingUnit = new JSONObject();
        reportingUnit.put("name", "四川综合业务区");
        reportingUnit.put("value", "0001D210000000000GAQ");
        apply.put("reportingUnit", reportingUnit);
        // 创建人
        apply.put("create_name", "唐春江");
        apply.put("create_by", "0001N610000000001DRM");
        // 基层法务
        JSONObject grassrootsLegalStaff = new JSONObject();
        grassrootsLegalStaff.put("name", "欧阳强鹏");
        grassrootsLegalStaff.put("value", "0001N6100000000HEF2N");
        apply.put("grassrootsLegalStaff", grassrootsLegalStaff);
        // directPerson
        JSONObject directPerson = new JSONObject();
        directPerson.put("name", "徐东升");
        directPerson.put("value", "0001N610000000049ANS");
        apply.put("directPerson", directPerson);
        // investmentPerson
        JSONObject investmentPerson = new JSONObject();
        investmentPerson.put("name", "徐东升");
        investmentPerson.put("value", "0001N610000000049ANS");
        // investmentModel
        apply.put("investmentPerson", investmentPerson);
        apply.put("investmentModel", true);
        json.put("apply", apply);
        String preResult = projectServiceForTz.createPre(JSON.toJSONString(json));
        System.out.println(preResult);
    }
}
