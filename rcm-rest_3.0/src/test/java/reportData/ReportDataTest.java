package reportData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yk.common.IBaseMongo;
import com.yk.historyData.dao.IHistoryDataMapper;

import common.Constants;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")

public class ReportDataTest {

	@Resource
	private IHistoryDataMapper historyDataMapper;
	@Resource
	private IBaseMongo baseMongo;

	@Test
	public void testPfr() {
		List<Map<String, Object>> list = historyDataMapper.getNewData();

		for (Map<String, Object> obj : list) {
			Document doc = new Document();
			ObjectId businessid = new ObjectId();

			Map<String, Object> dataForOracle = new HashMap<String, Object>();
			dataForOracle.put("ID", Integer.valueOf(obj.get("id").toString()));
			dataForOracle.put("BUSINESS_ID", businessid.toString());
			dataForOracle.put("PROJECT_NAME", obj.get("name"));
			dataForOracle.put("PROJECT_TYPE", "pfr");
			dataForOracle.put("CUSTOM_FIELD1", "2011".toString());
			this.historyDataMapper.insert(dataForOracle);

			doc.put("_id", businessid);
			doc.put("projectName", obj.get("name").toString());
			doc.put("id", businessid.toString());
			this.baseMongo.save(doc, Constants.RCM_HISTORYDATA_INFO);

			System.out.println(obj.get("name"));
		}
	}

	@Test
	public void replaceData() {
		List<Map<String, Object>> list = historyDataMapper.getNewData();
		for (Map<String, Object> obj : list) {
			Map<String, Object> queryMongoById = this.baseMongo.queryById(obj.get("BUSINESS_ID").toString(), Constants.RCM_HISTORYDATA_INFO);
			System.out.println(obj.get("PROJECT_NAME"));
			queryMongoById.put("projectName", obj.get("PROJECT_NAME").toString());
			queryMongoById.put("id", obj.get("BUSINESS_ID").toString());
			this.baseMongo.save((Document) queryMongoById, "data_bak");
		}

	}

}
