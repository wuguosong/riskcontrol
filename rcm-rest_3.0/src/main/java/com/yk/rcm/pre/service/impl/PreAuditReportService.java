package com.yk.rcm.pre.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.yk.common.IBaseMongo;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.fillMaterials.service.IFillMaterialsService;
import com.yk.rcm.pre.dao.IPreAuditReportMapper;
import com.yk.rcm.pre.service.IPreAuditReportService;
import com.yk.rcm.pre.service.IPreInfoService;

import common.Constants;
import common.PageAssistant;
import report.PreAssessmentReport;
import util.FileUtil;
import util.PropertiesUtil;
import util.ThreadLocalUtil;
import util.Util;
import ws.client.TzClient;

@Service
@Transactional
public class PreAuditReportService implements IPreAuditReportService {

	@Resource
	private IPreAuditReportMapper preAuditReportMapper;
	@Resource
	private IPreInfoService preInfoService;
	
	@Resource
	private IBaseMongo baseMongo;
	
	@Resource
	private IFillMaterialsService fillMaterialsService;

	@SuppressWarnings("unchecked")
	@Override
	public void queryUncommittedReportByPage(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
		params.put("page", page);
		params.put("projectName", retMap.get("projectName"));
		params.put("createBy", retMap.get("createBy"));
		if (!ThreadLocalUtil.getIsAdmin()) {
			params.put("psfzr", ThreadLocalUtil.getUserId());
		}
		params.put("pertainareaname", retMap.get("pertainareaname"));
		String orderBy = page.getOrderBy();
		if (orderBy == null) {
			orderBy = " create_date desc ";
		}
		params.put("orderBy", orderBy);

		List<Map<String, Object>> list = this.preAuditReportMapper.queryUncommittedReportByPage(params);
		page.setList(list);
	}

	@Override
	public void querySubmittedReportByPage(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> retMap = new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
		}.getType());
		params.put("page", page);
		params.put("projectName", retMap.get("projectName"));
		params.put("createBy", retMap.get("createBy"));
		if (!ThreadLocalUtil.getIsAdmin()) {
			params.put("psfzr", ThreadLocalUtil.getUserId());
		}
		params.put("pertainareaname", retMap.get("pertainareaname"));
		String orderBy = page.getOrderBy();
		if (orderBy == null) {
			orderBy = " create_date desc ";
		}
		params.put("orderBy", orderBy);

		List<Map<String, Object>> list = this.preAuditReportMapper.querySubmittedReportByPage(params);
		page.setList(list);
	}

	@Override
	public List<Map<String, Object>> queryNotNewlyPreAuditProject() {

		BasicDBObject query = new BasicDBObject();
		query.append("reviewReport", null);
		BasicDBObject fields = new BasicDBObject();
		fields.append("_id", 1);

		FindIterable<Document> findIterable = this.querySpecifiedColumn(query, fields, Constants.RCM_PRE_INFO);

		final List<String> list_id = new ArrayList<String>();
		findIterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				list_id.add(document.get("_id").toString());
			}
		});

		Map<String, Object> map = new HashMap<String, Object>();
		if (!ThreadLocalUtil.getIsAdmin()) {
			map.put("USERID", ThreadLocalUtil.getUserId());
		}
		map.put("LIST_BUSINESSID", list_id);

		List<Map<String, Object>> listMap = this.preAuditReportMapper.queryNotNewlyPreAuditProject(map);

		return listMap;
	}

	@Override
	public void batchDeletePreReport(String[] businessids) {
		this.preAuditReportMapper.batchDeletePreReport(businessids);

		for (String id : businessids) {
			Map<String, Object> map = this.baseMongo.queryById(id, Constants.RCM_PRE_INFO);
			if (map.containsKey("reviewReport") && map.get("reviewReport") != null) {
				map.remove("reviewReport");
			}
			if (map.containsKey("filePath") && map.get("filePath") != null) {
				map.remove("filePath");
			}

			this.baseMongo.deleteById(id, Constants.RCM_PRE_INFO);

			Document doc = (Document) map;
			this.baseMongo.save(doc, Constants.RCM_PRE_INFO);
		}

	}

	@Override
	public FindIterable<Document> querySpecifiedColumn(BasicDBObject query, BasicDBObject fields,
			String collectionName) {

		return this.baseMongo.getCollection(Constants.RCM_PRE_INFO).find(query).projection(fields);
	}

	@Override
	public Document getPreProjectFormalReviewByID(String id) {
		Map<String, Object> map = this.baseMongo.queryById(id, Constants.RCM_PRE_INFO);
		map.put("_id", id);
		return (Document) map;
	}

	@Override
	public String saveReviewReportById(String json) {
		Document bson = Document.parse(json);
		String objectId = bson.getString("_id");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", objectId);
		params.put("create_date", Util.now());
		params.put("controller_val", bson.getString("controllerVal"));
		this.preAuditReportMapper.savePreReport(params);
		
		
		Map<String, Object> statusMap = new HashMap<String, Object>();
		statusMap.put("table", "RCM_PRE_INFO");
		statusMap.put("filed", "IS_SUBMIT_REPORT");
		statusMap.put("status", "0");
		statusMap.put("BUSINESSID", objectId);
		this.fillMaterialsService.updateProjectStaus(statusMap);
		

		Document data = new Document();
		Document reviewReport = (Document) bson.get("reviewReport");
		data.put("reviewReport", reviewReport);
		if (null != reviewReport.get("models")) {
			String models = reviewReport.getString("models");
			if (models.equals("other")) {
				if (null != reviewReport.get("filePath")) {
					String filePath = reviewReport.getString("filePath");
					data.put("filePath", filePath);
				}
			}
		}
		this.baseMongo.updateSetByObjectId(objectId, data, Constants.RCM_PRE_INFO);
		return objectId;
	}

	@Override
	public String updateReviewReport(String json) {
		Document bson = Document.parse(json);
		String id = bson.get("_id").toString();

		/*
		 * Map<String, Object> params = new HashMap<String, Object>();
		 * params.put("businessId", id);
		 * this.preAuditReportMapper.updateReviewReport(params);
		 */

		bson.put("_id", new ObjectId(id));
		bson.remove("controllerVal");
		this.baseMongo.updateSetByObjectId(id, bson, Constants.RCM_PRE_INFO);

		return id;
	}

	@Override
	public void submitAndupdate(String businessid) {
		
		Map<String, Object> preOracle = this.preInfoService.getOracleByBusinessId(businessid);
		String needMeeting = (String) preOracle.get("NEED_MEETING");
		
		Map<String, Object> statusMap = new HashMap<String, Object>();
		statusMap.put("table", "RCM_PRE_INFO");
		statusMap.put("filed", "IS_SUBMIT_REPORT");
		statusMap.put("status", "1");
		statusMap.put("BUSINESSID", businessid);
		this.fillMaterialsService.updateProjectStaus(statusMap);
		
		Map<String, Object> mapReport = new HashMap<String, Object>();
		mapReport.put("businessId", businessid);
		mapReport.put("submit_date", Util.now());
		this.preAuditReportMapper.updatePreReport(mapReport);

		Map<String, Object> mapInfo = new HashMap<String, Object>();
		mapInfo.put("businessid", businessid);
		Map<String, Object> Object = this.fillMaterialsService.getRPIStatus(businessid);
		if(Util.isNotEmpty(Object)) {
			if (Util.isNotEmpty(Object.get("IS_SUBMIT_BIDDING"))) {
				if (Object.get("IS_SUBMIT_BIDDING").equals("1")) {
					mapInfo.put("stage", "4");
				}
			}
		}
//		mapInfo.put("stage", "3.7");
		/*this.preAuditReportMapper.changeState(mapInfo);*/

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("_id", new ObjectId(businessid));
		params.put("submit_date", Util.format(Util.now(), "yyyy-MM-dd"));
		this.baseMongo.updateSetByObjectId(businessid, params, Constants.RCM_PRE_INFO);
		if(Util.isNotEmpty(needMeeting) && "0".equals(needMeeting)){
			this.preInfoService.updateAuditStageByBusinessId(businessid, "9");
		}
		
		
		
		Document doc = (Document) this.baseMongo.queryById(businessid, Constants.RCM_PRE_INFO);
		String filepath = "", ext = "";
		if (doc != null) {
			filepath = doc.getString("filePath");
			if (filepath != null) {
				ext = filepath.substring(filepath.lastIndexOf("."));
			}
		}
		String serverPath = PropertiesUtil.getProperty("domain.allow") + PropertiesUtil.getProperty("contextPath");
		serverPath = serverPath + "/common/RcmFile/downLoad?filePath=" + filepath + "&fileName=preReport" + ext;

		//推送报告数据
		TzClient client = new TzClient();
		client.setBusinessId(businessid);
		client.setStatus("2");
		client.setLocation(serverPath);
		Thread t = new Thread(client);
		t.start();
	}

	@Override
	public Document getById(String id) {

		Map<String, Object> map = this.baseMongo.queryById(id, Constants.RCM_PRE_INFO);

		Document document = new Document();
		document.put("_id", id);
		if (map.containsKey("apply")) {
			document.put("apply", map.get("apply"));
		}
		if (map.containsKey("reviewReport")) {
			document.put("reviewReport", map.get("reviewReport"));
		}

		return document;
	}

	@Override
	public Map<String, String> getPreWordReport(String id) {
		PreAssessmentReport prReport = new PreAssessmentReport();
		String path=prReport.generateReport(id);
		
		Map<String,Object> doc = this.baseMongo.queryById(id, Constants.RCM_PRE_INFO);
		//判断之前的报告是不是存在，如果存在，删除
		String oldPath = null;
		if(doc.containsKey("filePath")&&doc.get("filePath")!=null){
			oldPath = doc.get("filePath").toString();	
		}
		try {
			if(oldPath != null && oldPath.length() > 0){
				FileUtil.removeFile(oldPath);
			}
		} catch (Exception e) {
		}
		this.baseMongo.updateSetById(id, doc, Constants.RCM_PRE_INFO);
		Map<String,String> map =new HashMap<String, String>();
		Document docs=(Document) doc.get("apply");
		map.put("filePath", path);
		map.put("fileName", docs.get("projectName").toString());
		return map;
	}
	
	@Override
	public boolean isPossible2Submit(String businessid) {
		boolean flag = false;
		Map<String, Object> map = this.preAuditReportMapper.isPossible2Submit(businessid);
		if ("2".equals(map.get("WF_STATE"))) {
			flag = true;
		}
		return flag;
	}

	@Override
	public void updateReportByBusinessId(Map<String, Object> params) {
		preAuditReportMapper.updateReportByBusinessId(params);
	}
	
	@Override
	public Map<String, Object> getOracleByBusinessId(String businessid) {
		return preAuditReportMapper.getByBusinessId(businessid);
	}
}
