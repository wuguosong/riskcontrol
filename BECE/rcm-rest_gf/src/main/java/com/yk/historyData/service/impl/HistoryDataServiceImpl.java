package com.yk.historyData.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.yk.common.IBaseMongo;
import com.yk.historyData.dao.IHistoryDataMapper;
import com.yk.historyData.service.IHistoryDataService;
import com.yk.power.dao.IOrgMapper;
import com.yk.power.service.IOrgService;

import common.Constants;
import common.PageAssistant;
import common.commonMethod;
import util.Util;

@Service
@Transactional
public class HistoryDataServiceImpl implements IHistoryDataService {

	@Resource
	private IHistoryDataMapper historyDataMapper;
	@Resource
	private IOrgMapper orgMapper;
	@Resource
	private IOrgService orgService;
	@Resource
	private IBaseMongo baseMongo;

	private Logger logger = LoggerFactory.getLogger(HistoryDataServiceImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public void saveOrUpdateHistoryData(String Json) throws Exception {
		HashMap<String, Object> jsonMap = JSON.parseObject(Json, HashMap.class);
		if (Util.isNotEmpty(jsonMap) && Util.isNotEmpty(jsonMap.get("BUSINESS_ID"))) {
			String businessId = jsonMap.get("BUSINESS_ID").toString();
			this.update1(jsonMap, businessId);
		} else {
            List<Map<String, Object>> list = this.historyDataMapper.getHistoryAlterList(jsonMap);
			
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> historyAlterInfo = list.get(i);
				Map<String, Object> historyInfo = this.historyDataMapper.getHistoryById(historyAlterInfo.get("ID").toString());
				if (Util.isNotEmpty(historyInfo)) {
					this.update(historyAlterInfo, historyInfo.get("BUSINESS_ID").toString());
				} else {
					this.save(historyAlterInfo);
				}
			}
		}
		
	}
	
	public void save(Map<String, Object> historyAlterInfo) {
		Document doc = new Document();
		ObjectId businessid = new ObjectId();
		
		Map<String, Object> dataForOracle = this.packageDataForOracle(historyAlterInfo, businessid.toString());
		this.historyDataMapper.insert(dataForOracle);
		
		doc.put("_id", businessid);
		doc.put("projectName", historyAlterInfo.get("PROJECT_NAME"));
		this.baseMongo.save(doc, Constants.RCM_HISTORYDATA_INFO);
	}
	
    public void update(Map<String, Object> historyAlterInfo, String businessId) {
    	Map<String, Object> queryMongoById = this.baseMongo.queryById(businessId, Constants.RCM_HISTORYDATA_INFO);
    	if (Util.isNotEmpty(queryMongoById)) {
    		queryMongoById.put("serviceType", historyAlterInfo.get("serviceType"));
    		queryMongoById.put("projectModel", historyAlterInfo.get("projectModel"));
    		baseMongo.updateSetByObjectId(businessId, queryMongoById, Constants.RCM_HISTORYDATA_INFO);
    	}
    	
    	Map<String, Object> dataForOracle = this.packageDataForOracle(historyAlterInfo, businessId);
		this.historyDataMapper.update(dataForOracle);
	}
    
    public void update1(Map<String, Object> historyAlterInfo, String businessId) {
    	Map<String, Object> queryMongoById = this.baseMongo.queryById(businessId, Constants.RCM_HISTORYDATA_INFO);
    	if (Util.isNotEmpty(queryMongoById)) {
    		queryMongoById.put("projectName", historyAlterInfo.get("PROJECT_NAME"));
    		queryMongoById.put("serviceType", historyAlterInfo.get("serviceType"));
    		queryMongoById.put("projectModel", historyAlterInfo.get("projectModel"));
    		baseMongo.updateSetByObjectId(businessId, queryMongoById, Constants.RCM_HISTORYDATA_INFO);
    	}
    	
    	Map<String, Object> dataForOracle = this.packageDataForOracle1(historyAlterInfo, businessId);
		this.historyDataMapper.update(dataForOracle);
	}
    
    /**
     * 数据导入用
     * */
    public Map<String, Object> packageDataForOracle (Map<String, Object> info, String businessId) {
    	Map<String, Object> dataForOracle = new HashMap<String, Object>();
    	
    	dataForOracle.put("ID", info.get("ID"));
    	dataForOracle.put("BUSINESS_ID", businessId);
    	dataForOracle.put("PROJECT_NUM", info.get("PROJECT_NUM"));
    	dataForOracle.put("PROJECT_NAME", info.get("PROJECT_NAME"));
    	Object projectType = info.get("PROJECT_TYPE");
    	if ("投标项目".equals(projectType)){
    		projectType = "pre";
    	} else {
    		projectType = "pfr";
    	}
    	dataForOracle.put("PROJECT_TYPE", projectType);
    	Object isWinBid = info.get("IS_WIN_BID");
    	if (isWinBid == "是") {
    		isWinBid = "1";
    	} else if (isWinBid == "否") {
    		isWinBid = "2";
    	} else if (isWinBid == "投标中") {
    		isWinBid = "0";
    	}
    	dataForOracle.put("IS_WIN_BID", isWinBid);
    	dataForOracle.put("SERVICE_TYPE_ID", info.get("SERVICE_TYPE_ID"));
    	dataForOracle.put("SERVICE_TYPE", info.get("SERVICE_TYPE"));
    	dataForOracle.put("BUSINESS_MODEL_IDS", info.get("BUSINESS_MODEL_IDS"));
    	dataForOracle.put("BUSINESS_MODEL", info.get("BUSINESS_MODEL"));
    	dataForOracle.put("FRANCHISE_PERIOD", info.get("FRANCHISE_PERIOD"));
    	dataForOracle.put("REVIEW_SCALE", info.get("REVIEW_SCALE"));
    	dataForOracle.put("INVESTMENT_TOTAL", info.get("INVESTMENT_TOTAL"));
    	dataForOracle.put("GROUP_SHAREHOLDING_RATIO", info.get("GROUP_SHAREHOLDING_RATIO"));
    	dataForOracle.put("SELF_FUNDS_RATIO", info.get("SELF_FUNDS_RATIO"));
    	dataForOracle.put("REVIEW_PRICE", info.get("REVIEW_PRICE"));
    	dataForOracle.put("EXPECTED_RATE_OF_RETURN", info.get("EXPECTED_RATE_OF_RETURN"));
    	
    	// 根据组织中文名查询组织ORGPKVALUE
    	Map<String, Object> params1 = new HashMap<String, Object>();
    	params1.put("ORGNAME", info.get("INVESTMENT_DEPT"));
    	List<Map<String, Object>> orgList = orgMapper.queryAllOrg(params1);
    	if (Util.isNotEmpty(orgList) && orgList.size() == 1) {
    		Map<String, Object> org = orgList.get(0);
    		dataForOracle.put("INVESTMENT_DEPT_ID", org.get("ORGPKVALUE"));
    	} 
    	dataForOracle.put("INVESTMENT_DEPT", info.get("INVESTMENT_DEPT"));
    	
    	
    	
    	// 投资经理、评审人员以及法律人员因为系统中有多个重名，有些不好做区分，因此目前不做处理
    	dataForOracle.put("CREATE_BY", info.get("CREATE_BY"));
    	dataForOracle.put("REVIEW_PERSON", info.get("REVIEW_PERSON"));
    	dataForOracle.put("LEGAL_REVIEW_PERSON", info.get("LEGAL_REVIEW_PERSON"));
    	String REVIEW_TIME = "";
    	if (Util.isNotEmpty(info.get("REVIEW_TIME"))) {
    		REVIEW_TIME = info.get("REVIEW_TIME").toString().split(" ")[0];
    	} else {
    		REVIEW_TIME = null;
    	}
    	dataForOracle.put("REVIEW_TIME", REVIEW_TIME);
    	dataForOracle.put("FOLLOW_SIGN_AGREEMENT_NAME", info.get("FOLLOW_SIGN_AGREEMENT_NAME"));
    	
    	// 根据组织中文名查询组织ORGPKVALUE
    	params1.put("ORGNAME", info.get("REGIONAL_COMPANY"));
    	List<Map<String, Object>> orgList1 = orgMapper.queryAllOrg(params1);
    	if (Util.isNotEmpty(orgList1) && orgList1.size() == 1) {
    		Map<String, Object> org1 = orgList1.get(0);
    		dataForOracle.put("REGIONAL_COMPANY_ID", org1.get("ORGPKVALUE"));
    	} 
    	dataForOracle.put("REGIONAL_COMPANY", info.get("REGIONAL_COMPANY"));
    	
    	commonMethod common = new commonMethod();
    	if (Util.isNotEmpty(info.get("PROVINCE"))){
    		List<Map> areaList = common.getAreaInfoByEnumName(info.get("PROVINCE").toString());
        	if (Util.isNotEmpty(areaList)) {
        		dataForOracle.put("PROVINCE_ID", areaList.get(0).get("PK_ENUM"));
        	}
    	}
    	dataForOracle.put("PROVINCE", info.get("PROVINCE"));
    	if (Util.isNotEmpty(info.get("CITY"))){
    		List<Map> areaList1 = common.getAreaInfoByEnumName(info.get("CITY").toString());
        	if (Util.isNotEmpty(areaList1)) {
        		dataForOracle.put("CITY_ID", areaList1.get(0).get("PK_ENUM"));
        	}
    	}
    	dataForOracle.put("CITY", info.get("CITY"));
    	String CREATE_TIME = "";
    	if (Util.isNotEmpty(info.get("CREATE_TIME"))) {
    		CREATE_TIME = info.get("CREATE_TIME").toString().split(" ")[0];
    	} else {
    		CREATE_TIME = null;
    	}
    	dataForOracle.put("CREATE_TIME", CREATE_TIME);
    	dataForOracle.put("CONTRACT_TITLE", info.get("CONTRACT_TITLE"));
    	String SIGNING_TIME = "";
    	if (Util.isNotEmpty(info.get("SIGNING_TIME"))) {
    		SIGNING_TIME = info.get("SIGNING_TIME").toString().split(" ")[0];
    	} else {
    		SIGNING_TIME = null;
    	}
    	dataForOracle.put("SIGNING_TIME", SIGNING_TIME);
    	// 根据组织中文名查询组织ORGPKVALUE
    	params1.put("ORGNAME", info.get("OUR_CONTRACTING_ENTITY"));
    	List<Map<String, Object>> orgList2 = orgMapper.queryAllOrg(params1);
    	if (Util.isNotEmpty(orgList2)) {
    		Map<String, Object> org2 = orgList2.get(0);
    		dataForOracle.put("OUR_CONTRACTING_ENTITY_ID", org2.get("ORGPKVALUE"));
    	} 
    	dataForOracle.put("OUR_CONTRACTING_ENTITY", info.get("OUR_CONTRACTING_ENTITY"));
    	dataForOracle.put("CONTRACT_AMOUNT", info.get("CONTRACT_AMOUNT"));
    	dataForOracle.put("CONTRACT_SCALE", info.get("CONTRACT_SCALE"));
    	dataForOracle.put("REMARKS", info.get("REMARKS"));
    	dataForOracle.put("CUSTOM_FIELD1", info.get("CUSTOM_FIELD1"));
    	dataForOracle.put("CUSTOM_FIELD2", info.get("CUSTOM_FIELD2"));
    	dataForOracle.put("CUSTOM_FIELD3", info.get("CUSTOM_FIELD3"));
    	dataForOracle.put("CUSTOM_FIELD4", info.get("CUSTOM_FIELD4"));
    	dataForOracle.put("CUSTOM_FIELD5", info.get("CUSTOM_FIELD5"));
    	
    	return dataForOracle;
    }
    
    /**
     * 页面存储用
     * */
    public Map<String, Object> packageDataForOracle1 (Map<String, Object> info, String businessId) {
    	Map<String, Object> dataForOracle = new HashMap<String, Object>();
    	
    	dataForOracle.put("ID", info.get("ID"));
    	dataForOracle.put("BUSINESS_ID", businessId);
    	dataForOracle.put("PROJECT_NUM", info.get("PROJECT_NUM"));
    	dataForOracle.put("PROJECT_NAME", info.get("PROJECT_NAME"));
    	dataForOracle.put("PROJECT_TYPE", info.get("PROJECT_TYPE"));
    	dataForOracle.put("IS_WIN_BID", info.get("IS_WIN_BID"));
    	List<Map<String, Object>> serviceType = (List<Map<String, Object>>) info.get("serviceType");
		if (Util.isNotEmpty(serviceType) && serviceType.size() > 0) {
			String serviceType_id = "";
			for (Map<String, Object> st : serviceType) {
				serviceType_id += "," + st.get("KEY").toString();
			}
			serviceType_id = serviceType_id.substring(1);
			dataForOracle.put("SERVICE_TYPE_ID", serviceType_id);
		}
    	dataForOracle.put("SERVICE_TYPE", info.get("SERVICE_TYPE"));
    	
    	List<Map<String, Object>> projectModel = (List<Map<String, Object>>) info.get("projectModel");
		if (Util.isNotEmpty(projectModel) && projectModel.size() > 0) {
			String projectModel_ids = "";
			for (Map<String, Object> pm : projectModel) {
				projectModel_ids += "," + pm.get("KEY").toString();
			}
			projectModel_ids = projectModel_ids.substring(1);
			dataForOracle.put("BUSINESS_MODEL_IDS", projectModel_ids);
		}
    	dataForOracle.put("BUSINESS_MODEL", info.get("BUSINESS_MODEL"));
    	dataForOracle.put("FRANCHISE_PERIOD", info.get("FRANCHISE_PERIOD"));
    	dataForOracle.put("REVIEW_SCALE", info.get("REVIEW_SCALE"));
    	dataForOracle.put("INVESTMENT_TOTAL", info.get("INVESTMENT_TOTAL"));
    	dataForOracle.put("GROUP_SHAREHOLDING_RATIO", info.get("GROUP_SHAREHOLDING_RATIO"));
    	dataForOracle.put("SELF_FUNDS_RATIO", info.get("SELF_FUNDS_RATIO"));
    	dataForOracle.put("REVIEW_PRICE", info.get("REVIEW_PRICE"));
    	dataForOracle.put("EXPECTED_RATE_OF_RETURN", info.get("EXPECTED_RATE_OF_RETURN"));
    	
    	dataForOracle.put("INVESTMENT_DEPT_ID", info.get("INVESTMENT_DEPT_ID"));
    	dataForOracle.put("INVESTMENT_DEPT", info.get("INVESTMENT_DEPT"));
    	
    	
    	dataForOracle.put("CREATE_BY_ID", info.get("CREATE_BY_ID"));
    	dataForOracle.put("CREATE_BY", info.get("CREATE_BY"));
    	dataForOracle.put("REVIEW_PERSON_ID", info.get("REVIEW_PERSON_ID"));
    	dataForOracle.put("REVIEW_PERSON", info.get("REVIEW_PERSON"));
    	dataForOracle.put("LEGAL_REVIEW_PERSON_ID", info.get("LEGAL_REVIEW_PERSON_ID"));
    	dataForOracle.put("LEGAL_REVIEW_PERSON", info.get("LEGAL_REVIEW_PERSON"));
    	String REVIEW_TIME = "";
    	if (Util.isNotEmpty(info.get("REVIEW_TIME"))) {
    		REVIEW_TIME = info.get("REVIEW_TIME").toString().split(" ")[0];
    	} else {
    		REVIEW_TIME = null;
    	}
    	dataForOracle.put("REVIEW_TIME", REVIEW_TIME);
    	dataForOracle.put("FOLLOW_SIGN_AGREEMENT_NAME", info.get("FOLLOW_SIGN_AGREEMENT_NAME"));

    	dataForOracle.put("REGIONAL_COMPANY_ID", info.get("REGIONAL_COMPANY_ID"));
    	dataForOracle.put("REGIONAL_COMPANY", info.get("REGIONAL_COMPANY"));
    	
    	commonMethod common = new commonMethod();
    	if (Util.isNotEmpty(info.get("PROVINCE"))){
    		List<Map> areaList = common.getAreaInfoByEnumName(info.get("PROVINCE").toString());
        	if (Util.isNotEmpty(areaList)) {
        		dataForOracle.put("PROVINCE_ID", areaList.get(0).get("PK_ENUM"));
        	}
    	}
    	dataForOracle.put("PROVINCE", info.get("PROVINCE"));
    	if (Util.isNotEmpty(info.get("CITY"))){
    		List<Map> areaList1 = common.getAreaInfoByEnumName(info.get("CITY").toString());
        	if (Util.isNotEmpty(areaList1)) {
        		dataForOracle.put("CITY_ID", areaList1.get(0).get("PK_ENUM"));
        	}
    	}
    	dataForOracle.put("CITY", info.get("CITY"));
    	String CREATE_TIME = "";
    	if (Util.isNotEmpty(info.get("CREATE_TIME"))) {
    		CREATE_TIME = info.get("CREATE_TIME").toString().split(" ")[0];
    	} else {
    		CREATE_TIME = null;
    	}
    	dataForOracle.put("CREATE_TIME", CREATE_TIME);
    	dataForOracle.put("CONTRACT_TITLE", info.get("CONTRACT_TITLE"));
    	String SIGNING_TIME = "";
    	if (Util.isNotEmpty(info.get("SIGNING_TIME"))) {
    		SIGNING_TIME = info.get("SIGNING_TIME").toString().split(" ")[0];
    	} else {
    		SIGNING_TIME = null;
    	}
    	dataForOracle.put("SIGNING_TIME", SIGNING_TIME);
    	dataForOracle.put("OUR_CONTRACTING_ENTITY_ID", info.get("OUR_CONTRACTING_ENTITY_ID"));
    	dataForOracle.put("OUR_CONTRACTING_ENTITY", info.get("OUR_CONTRACTING_ENTITY"));
    	dataForOracle.put("CONTRACT_AMOUNT", info.get("CONTRACT_AMOUNT"));
    	dataForOracle.put("CONTRACT_SCALE", info.get("CONTRACT_SCALE"));
    	dataForOracle.put("REMARKS", info.get("REMARKS"));
    	dataForOracle.put("CUSTOM_FIELD1", info.get("CUSTOM_FIELD1"));
    	dataForOracle.put("CUSTOM_FIELD2", info.get("CUSTOM_FIELD2"));
    	dataForOracle.put("CUSTOM_FIELD3", info.get("CUSTOM_FIELD3"));
    	dataForOracle.put("CUSTOM_FIELD4", info.get("CUSTOM_FIELD4"));
    	dataForOracle.put("CUSTOM_FIELD5", info.get("CUSTOM_FIELD5"));
    	
    	return dataForOracle;
    }

	@Override
	public PageAssistant getHistoryList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);

		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}

		List<Map<String, Object>> list = this.historyDataMapper.getHistoryListByPage(params);
		
		page.setList(list);
		return page;
	}

	@Override
	public Map<String, Object> getHistoryById(String id) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		Document mongoData = null;
		Map<String, Object> queryMongoById = this.baseMongo.queryById(id, Constants.RCM_HISTORYDATA_INFO);
		mongoData = (Document) queryMongoById;
		mongoData.put("_id", mongoData.get("_id").toString());
		result.put("mongoData", mongoData);

		Map<String, Object> queryOracleById = this.historyDataMapper.getHistoryByBusinessId(id);
		result.put("historyInfo", queryOracleById);

		return result;
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public void addNewAttachment(String json) {

		Document doc = Document.parse(json);

		String businessId = (String) doc.get("businessId");
		String oldFileName = (String) doc.get("oldFileName");
		Document item = (Document) doc.get("item");
		String fileId = (String) item.get("fileId");
		Document type = (Document) item.get("type");
		String fileName = (String) item.get("fileName");

		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_HISTORYDATA_INFO);

		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachmentList");

		if (attachmentList == null) {
			attachmentList = new ArrayList<Map<String, Object>>();
			Map<String, Object> file = new HashMap<String, Object>();
			file.put("fileId", fileId);
			file.put("fileName", fileName);
			file.put("oldFileName", oldFileName);
			file.put("type", type);
			attachmentList.add(file);
		} else {
			Map<String, Object> file = new HashMap<String, Object>();
			file.put("fileId", fileId);
			file.put("fileName", fileName);
			file.put("oldFileName", oldFileName);
			file.put("type", type);
			attachmentList.add(file);
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("attachmentList", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_HISTORYDATA_INFO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteAttachment(String json) {
		Document doc = Document.parse(json);
		String businessId = (String) doc.get("businessId");
		String fileId = doc.get("fileId") + "";
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_HISTORYDATA_INFO);

		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachmentList");
		System.out.println("fileId ====" + fileId);
		for (int i = 0; i < attachmentList.size(); i++) {
			Document attachment = (Document) attachmentList.get(i);
			if (attachment.get("fileId").equals(fileId)) {
				attachmentList.remove(i);
				break;
			}
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("attachmentList", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_HISTORYDATA_INFO);
	}

}
