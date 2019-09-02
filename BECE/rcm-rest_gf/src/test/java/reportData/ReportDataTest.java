package reportData;


import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.yk.reportData.service.impl.ReportDataServiceImpl;

public class ReportDataTest {
	
	@Test
	public void testPfr() throws Exception{
		ReportDataServiceImpl reportData = new ReportDataServiceImpl();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("projectType", "pfr");
		/*params.put("createDate", "2017-02-28");*/
		String JsonMap = JSON.toJSONString(params);
		reportData.saveOrUpdateReportData(JsonMap);
	}
	
}
