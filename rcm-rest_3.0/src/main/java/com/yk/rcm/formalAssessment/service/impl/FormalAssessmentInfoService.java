package com.yk.rcm.formalAssessment.service.impl;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.impl.util.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import report.Path;
import util.DbUtil;
import util.PropertiesUtil;
import util.ThreadLocalUtil;
import util.Util;
import ws.client.TzAfterNoticeClient;
import ws.service.DownloadFileForTZ;

import com.daxt.service.IDaxtService;
import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.flow.service.IBpmnAuditService;
import com.yk.flow.util.JsonUtil;
import com.yk.power.service.IOrgService;
import com.yk.power.service.IRoleService;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentInfoMapper;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.newFormalAssessment.service.IFormalAssessmentInfoCreateService;

import common.Constants;
import common.PageAssistant;
import common.Result;


/**
 * @author yaphet
 * @param <V>
 */
@Service
@Transactional
public class FormalAssessmentInfoService<V> implements IFormalAssessmentInfoService{
	@Resource
	private IFormalAssessmentInfoMapper formalAssessmentInfoMapper;
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IOrgService orgService;
	@Resource
	private IRoleService roleService;
	@Resource
	private IBpmnAuditService bpmnAuditService;
	@Resource
	private IDaxtService daxtService;
	@Resource
	private IFormalAssessmentInfoCreateService formalAssessmentInfoCreateService;
	
	public DownloadFileForTZ downloadFileForTZ=new DownloadFileForTZ();
	
	private Logger log = LoggerFactory.getLogger(FormalAssessmentInfoService.class);
	
	private final String RCM_FORMALASSESSMENT_INFO=Constants.RCM_FORMALASSESSMENT_INFO;	
	
	@Override
	public List<Map<String, Object>> getAllOldFormal() {
		return this.formalAssessmentInfoMapper.getAllOldFormal();
	}

	
	@Override
	public void save(Map<String,Object> param) {
		this.formalAssessmentInfoMapper.insert(param);
	}

	@Override
	public void update(Map<String,String> param) {
		this.formalAssessmentInfoMapper.update(param);
	}
	
	@Override
	public Map<String, Object> getFormalAssessmentByID(String id) {
		HashMap<String, Object> result = new HashMap<String,Object>();
		Map<String, Object> queryMongoById = this.baseMongo.queryById(id, RCM_FORMALASSESSMENT_INFO);
		result.put("formalAssessmentMongo", queryMongoById);
		
		Map<String, Object> queryOracleById = this.formalAssessmentInfoMapper.getFormalAssessmentById(id);
		result.put("formalAssessmentOracle", queryOracleById);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryMongoById.get("attachment");
		List<Map<String, Object>> attach = new ArrayList<Map<String, Object>>();
		if(Util.isNotEmpty(attachmentList)){
			for (Map<String, Object> attachment : attachmentList) {
				if(Util.isNotEmpty(attachment)){
					Map<String, Object> document = new HashMap<String, Object>();
					document.put("ITEM_NAME", attachment.get("ITEM_NAME").toString());
					document.put("UUID", attachment.get("UUID").toString());
					attach.add(document);
				}
			}
		}
		result.put("attach", attach);
		return result;
	}

	@Override
	public PageAssistant queryByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		
		if(Util.isNotEmpty(page.getParamMap())){
			String needCreateBy = (String) page.getParamMap().get("needCreateBy");
			if(!ThreadLocalUtil.getIsAdmin()){
				//管理员能看所有的
				if(!"0".equals(needCreateBy)){
					params.put("createBy", ThreadLocalUtil.getUserId());
				}
			}
		}
		
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		
//		String orderBy = page.getOrderBy();
//		if(orderBy == null){
//			orderBy = " ta.create_date desc ";
//		}
//		params.put("orderBy", orderBy);
		List<Map<String,Object>> list = this.formalAssessmentInfoMapper.queryByPage(params);
		//循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, "rcm_formalAssessment_info");
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
		return page;
	}
	@Override
	public PageAssistant queryPageForExport(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
			Map<String, Object> date = page.getParamMap();
			if(Util.isEmpty(date.get("year"))){
				Date now = Util.now();
		        //获取当前年
				int year=now.getYear() +1900;
				params.put("year", year+"");
			}
		}
		List<Map<String,Object>> list = this.formalAssessmentInfoMapper.queryPageForExport(params);
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, "rcm_formalAssessment_info");
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
		return page;
	}


	@Override
	public void create(String json) {
		Document doc = Document.parse(json);
		ObjectId businessid = new ObjectId();
		doc.put("_id", businessid);
		Map<String, Object> dataForOracle = this.packageDataForOracle(doc);
		this.formalAssessmentInfoMapper.insert(dataForOracle);
		this.baseMongo.save(doc, RCM_FORMALASSESSMENT_INFO);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> packageDataForOracle(Document doc){
		HashMap<String, Object> params = new HashMap<String,Object>();
		
		params.put("businessid", doc.get("_id").toString());
		Document apply = (Document) doc.get("apply");
		params.put("projectName", apply.getString("projectName"));
		params.put("projectNum", apply.getString("projectNo"));
		
		// reportingUnit
		Document reportingUnit = (Document) apply.get("reportingUnit");
		params.put("reportingUnit_id", reportingUnit.getString("KEY"));
		
		// serviceType
		Document serviceType = (Document) apply.get("serviceType");
		params.put("serviceType_id", serviceType.getString("KEY"));
		
		// investment_model
		params.put("investment_model", apply.get("investmentModel").toString());
		
		// project_model_ids
		List<Document> projectModel = (List<Document>) apply.get("projectModel");
		if(Util.isNotEmpty(projectModel) && projectModel.size()>0){
			String projectModel_ids="";
			for (Document pm : projectModel) {
				projectModel_ids+=","+pm.getString("KEY");
			}
			projectModel_ids = projectModel_ids.substring(1);
			params.put("project_model_ids", projectModel_ids);
		}
		
		params.put("need_meeting", null);
		
		params.put("meeting_date", null);
		
		// IS_SUPPLEMENT_REVIEW
		params.put("is_supplement_review", doc.get("is_supplement_review"));
		
		params.put("emergencyLevel", null);
		
		params.put("isUrgent", null);
		
		// pertainArea
		Document pertainArea = (Document) apply.get("pertainArea");
		if(pertainArea != null){
			params.put("pertainAreaId", pertainArea.getString("KEY"));
		}
		
		params.put("isTZ", doc.getString("istz"));
		
		params.put("wf_state", "0");
		
		params.put("oldData", "0");
		
		//基层法务评审人ID
		Document grassrootsLegalStaff = (Document) apply.get("grassrootsLegalStaff");
		if(grassrootsLegalStaff != null){
			params.put("grassrootslegalpersonId", grassrootsLegalStaff.getString("VALUE"));
		}
		// 大区companyHeader
		Map<String, Object> companyHeader = (Map<String, Object>) apply.get("companyHeader");
		String companyHeaderValue = (String)companyHeader.get("VALUE");
		params.put("largeAreaPersonId", companyHeaderValue);
		
		//区域负责人——投资中心/水环境（疑问）
		Map<String, Object> investmentPerson = (Map<String, Object>) apply.get("investmentPerson");
		if(investmentPerson != null){
			String serviceTypeValue = (String) investmentPerson.get("VALUE");
			params.put("serviceTypePersonId",serviceTypeValue);
		}
		
		Document createby = (Document) apply.get("investmentManager");
		params.put("createBy", createby.getString("VALUE"));
		
		//创建时间与mongDB 一致
		params.put("create_date", doc.get("create_date"));
		return params;
	}
	
	@Override
	public Result saveOrUpdate(Document oldDoc) {
		oldDoc = formatData(oldDoc);
		oldDoc.put("istz", "1");
		boolean isSuccess = true;
		String  date= Util.getTime();
		String id=null;
		Map<String, Object> map = new HashMap<String, Object>();
		Document apply=(Document) oldDoc.get("apply");
		String businessId = oldDoc.getString("businessid");
		
		//处理serviceType
		Document doc =  updateServiceType(oldDoc);
		//修改附件路径\\为/
		//无论新增与修执行此方法
		 updateAttachmentPath(doc);
		//存储投资系统的附件
		doc.put("oldAttachment", oldDoc.get("attachment"));
		
		Document newApply=(Document) doc.get("apply");
		Document reportingUnit = (Document) newApply.get("reportingUnit");
		//处理大区pertainArea
		String orgpkvalue = reportingUnit.getString("KEY");
		Document pertainArea =   getPertainArea(orgpkvalue);
		newApply.put("pertainArea", pertainArea);
		
		//生成文件路径地址
		String filepan = Path.formalAttachmentPath()+apply.get("projectNum")+"/";
		//判断新增活修改
		if("".equals(businessId) || null == businessId){
			//数据不存在，可以新建数据
			doc.append("create_date", date);
			Format format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			doc.append("currentTimeStamp", format.format(new Date()));
			doc.append("state", "1");
			//为附件生成UUID
			 generateAttachmentUUID(doc);
			//新增业务没有businessid
			String oid=  new ObjectId().toString();
			id = oid;
			//oracle新增
			this.saveReviewBaseInfo2Oracle(oid, Constants.RCM_FORMALASSESSMENT_INFO, doc);
			//mongo新增
			doc.put("_id", new ObjectId(oid));
//			DbUtil.getColl(Constants.RCM_PROJECTFORMALREVIEW_INFO).insertOne(doc);
			
			this.baseMongo.save(doc, Constants.RCM_FORMALASSESSMENT_INFO);
			
			map.put("result_wf_state", "0");
			//文件处理
			try {
				 downloadFileForTZ.downloadFile("pfr", oid, filepan);
			} catch (Exception e) {
//				StringBuffer sb = new StringBuffer();
//				log.error(sb.append("***********************[")
//					.append(Util.getTime()).append("]:").append("touzi system call out interface createPfr, create formal")
//					.append(System.getProperty("line.separator")).toString());
//				String error = Util.parseException(e);
//				log.error(error);
			}
		}else{
			//修改	判断流程状态	根据wf_state
			Map<String, Object> proInfo = formalAssessmentInfoMapper.getFormalAssessmentById(businessId);
			String wf_state = proInfo.get("WF_STATE").toString();
			id = businessId;
			if("0".equals(wf_state) || "3".equals(wf_state)){
				//可以修改
				BasicDBObject queryAndWhere=new BasicDBObject();
				queryAndWhere.put("_id", new ObjectId(businessId));
				Document docOld =DbUtil.getColl(Constants.RCM_FORMALASSESSMENT_INFO).find(queryAndWhere).first();
				docOld.put("_id",new ObjectId(docOld.get("_id").toString()));
				docOld.put("apply", doc.get("apply"));
				//为附件生成UUID
				generateAttachmentUUID(doc);
				docOld.put("attachment", doc.get("attachment"));
				doc.append("update_date", date);
				BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
				//修改oracle
				this.saveReviewBaseInfo2Oracle(businessId, Constants.FORMAL_ASSESSMENT, docOld);
				//修改mongo
//				DbUtil.getColl(Constants.RCM_PROJECTFORMALREVIEW_INFO).updateOne(queryAndWhere,updateSetValue);
				baseMongo.updateSetByFilter(queryAndWhere, updateSetValue, Constants.RCM_PROJECTFORMALREVIEW_INFO);
				map.put("result_wf_state", "0");
				try {
					downloadFileForTZ.downloadFile("pfr", id, filepan);
				} catch (Exception e) {
					StringBuffer sb = new StringBuffer();
					log.error(sb.append("***********************[")
						.append(Util.getTime()).append("]:").append("touzi system call out interface createPfr, create formal")
						.append(System.getProperty("line.separator")).toString());
					String error = Util.parseException(e);
					log.error(error);
				}
			}else{
				//流程已提交不可修改
				//数据已存在，并且流程已开始审批，不可以修改数据
				Map<String, Object> docs = this.formalAssessmentInfoMapper.getFormalAssessmentById(businessId);
				String WF_STATE=docs.get("WF_STATE").toString();
				
				BasicDBObject queryAndWhere=new BasicDBObject();
				queryAndWhere.put("apply.projectNum", apply.get("projectNum").toString());
				//查询正式评审报告，返回报告路径
//				Map<String, Object> docOld = this.baseMongo.queryByCondition(queryAndWhere, Constants.RCM_PROJECTFORMALREVIEW_INFO).get(0);
//				
//				map.put("projectReportURL", docOld.get("filePath"));
				map.put("result_wf_state", WF_STATE);
				isSuccess = false;
			}
		}
		
		map.put("result_status", isSuccess);
		map.put("id", id);
		String prefix = PropertiesUtil.getProperty("domain.allow");
		map.put("URL", prefix+"/html/index.html#/FormalAssessmentDetailView/"+id);
		Result result = new Result();
		result.setResult_data(map);
		return result;
	}

	@SuppressWarnings("unchecked")
	public void updateAttachmentPath(Document doc){
		if(!doc.containsKey("attachment")){
			return;
		}
		List<Document> attachmentList =(List<Document>) doc.get("attachment");
		if(attachmentList == null || attachmentList.isEmpty()){
			return;
		}
		for(int i = 0; i < attachmentList.size(); i++){
			Document attachment = attachmentList.get(i);
			if(attachment != null && attachment.containsKey("files") && attachment.get("files")!=null){
				List<Document> files = (List<Document>) attachment.get("files");
				if(files.isEmpty()){
					continue;
				}
				for(int m = 0; m < files.size(); m++){
					Document file = files.get(m);
					if(file==null || !file.containsKey("filePath") || file.getString("filePath")==null){
						continue;
					}
					String filePath = file.getString("filePath");
					file.put("filePath", filePath.replaceAll("\\\\", "/"));
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	public void generateAttachmentUUID(Document doc){

		if(!doc.containsKey("attachment")){
			return;
		}
		List<Document> attachmentList =(List<Document>) doc.get("attachment");
		if(attachmentList == null || attachmentList.isEmpty()){
			return;
		}
		for(int i = 0; i < attachmentList.size(); i++){
			Document attachment = attachmentList.get(i);
			if(attachment != null && attachment.containsKey("UUID")){
				attachment.put("UUID", Util.getUUID());
			}
		}
	}
	public Document getPertainArea(String orgpkvalue) {
		Document pertainArea = new Document();
		Map<String, Object> queryPertainArea = this.orgService.queryPertainAreaByPkvalue(orgpkvalue);
		if(Util.isNotEmpty(queryPertainArea) ){
			pertainArea.put("KEY", queryPertainArea.get("ORGPKVALUE"));
			pertainArea.put("VALUE", queryPertainArea.get("NAME"));
		}
		return pertainArea;
	}
	@SuppressWarnings("unchecked")
	public Document updateServiceType(Document doc) {
		
		
		String docJson = JsonUtil.toJson(doc);
		Document copyDoc = Document.parse(docJson);
		
		Document apply=(Document) copyDoc.get("apply");
		
		//存旧的类型到mongo
		apply.put("oldServiceType", apply.get("serviceType"));
		List<Document> serviceTypeList = (List<Document>) apply.get("serviceType");
		List<Document> NewServiceTypeList = new ArrayList<Document>();
		/**
		 * 1--1401
		 * 2--1402
		 * 4--1401
		 * 5--1404
		 * 6--1403
		 * 7--1405
		 */
		HashSet<String> serviceTypeIdSet = new HashSet<String>();
		for(int i = 0; i < serviceTypeList.size(); i++){
			Document serviceType = serviceTypeList.get(i);
			if(!Util.isEmpty(serviceType)){
				String serviceTypeKey = serviceType.getString("KEY");
				if(!Util.isEmpty(serviceTypeKey)){
					if(serviceTypeKey.equals("1") || serviceTypeKey.equals("4")){
						serviceTypeKey = "1401";
					}
					if(serviceTypeKey.equals("2")){
						serviceTypeKey = "1402";
					}
					if(serviceTypeKey.equals("5")){
						serviceTypeKey = "1404";
					}
					if(serviceTypeKey.equals("6")){
						serviceTypeKey = "1403";
					}
					if(serviceTypeKey.equals("7")){
						serviceTypeKey = "1405";
					}
					serviceTypeIdSet.add(serviceTypeKey);//利用set去重
				}
			}
		}
		for (String key : serviceTypeIdSet) {
			String value = "";
			if(key.equals("1401")){
				value  = "传统水务";
			}
			if(key.equals("1402")){
				value  = "水环境";
			}
			if(key.equals("1403")){
				value  = "固废";
			}
			if(key.equals("1404")){
				value  = "环卫";
			}
			if(key.equals("1405")){
				value  = "其他";
			}
			Document serviceType = new Document();
			serviceType.put("KEY", key);
			serviceType.put("VALUE", value);
			NewServiceTypeList.add(serviceType);
		}
		apply.put("serviceType",NewServiceTypeList);
		return copyDoc;
	}
	@SuppressWarnings("unchecked")
	public void saveReviewBaseInfo2Oracle(String objectId, String processType,Document doc){
		Document apply = doc.get("apply", Document.class);
		//业务ID
		String businessId = objectId;
		
		//申报单位
		Document reportingUnit = apply.get("reportingUnit", Document.class);
		String reportingUnitId = reportingUnit.getString("KEY");
		//项目名称
		String projectName = apply.getString("projectName");
		//项目编码
		String projectNum = apply.getString("projectNo");
		//业务类型
		List<Document> serviceType = apply.get("serviceType", ArrayList.class);
		String serviceTypeId = getStringFromListDoc(serviceType, "KEY");
		//大区
		Document pertainArea = (Document) apply.get("pertainArea");
		String pertainAreaId = pertainArea.getString("KEY");
		//createby
		Document createBy = (Document) apply.get("createBy");
		
		//投资模式
		Boolean investmentModel = apply.getBoolean("investmentModel");//PPP和非PPP
		List<Document> projectModelList = apply.get("projectModel", ArrayList.class);
		String projectModelIds = getStringFromListDoc(projectModelList, "KEY");
		String is_supplement_review ="0";
		if(null!=doc.get("is_supplement_review") && !"".equals(doc.get("is_supplement_review"))){
			is_supplement_review=doc.get("is_supplement_review").toString();
		}
		//执行保存操作
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessid", businessId);
		params.put("projectName", projectName);
		params.put("projectNum", projectNum);
		params.put("reportingUnit_id", reportingUnitId);
		params.put("serviceType_id", serviceTypeId);
		params.put("investment_model", apply.get("investmentModel").toString());
		params.put("project_model_ids", projectModelIds);
		params.put("need_meeting", null);
		params.put("meeting_date", null);
		params.put("is_supplement_review", is_supplement_review);
		params.put("emergencyLevel", null);
		params.put("isUrgent", null);
		params.put("pertainAreaId", pertainAreaId);
		params.put("isTZ", doc.getString("istz"));
		params.put("wf_state", "0");
		params.put("apply_date", null);
		params.put("complete_date", null);
		params.put("report_create_date", null);
		params.put("projectSize", null);
		params.put("investMoney", null);
		params.put("rateOfReturn", null);
		params.put("auditStage", "1");
		params.put("oldData", "0");
		String now = Util.format(Util.now());
		params.put("last_update_date", now);
		params.put("createBy", createBy.getString("VALUE"));
		
		Map<String, Object> oldFormalAssessment = this.formalAssessmentInfoMapper.getFormalAssessmentById(businessId);
		if(Util.isNotEmpty(oldFormalAssessment)){
			params.put("wf_state", oldFormalAssessment.get("WF_STATE"));
		}
		this.formalAssessmentInfoMapper.deleteByBusinessId(businessId);
		this.formalAssessmentInfoMapper.insert(params);
	}

	//获取List<Document>中的
	private String getStringFromListDoc(List<Document> listDoc, String key){
		String str = "";
		if(Util.isNotEmpty(listDoc)){
			for(Document doc : listDoc){
				str += doc.getString(key)+",";
			}
		}
		str = StringUtils.removeEnd(str, ",");
		return str;
	}
	
	//处理投资系统推送的数据的结构
	@SuppressWarnings("unchecked")
	private Document formatData(Document olddoc){
		String json = JsonUtil.toJson(olddoc);
		Document doc = Document.parse(json);
		Document oldApply = (Document) olddoc.get("apply");
		Document newApply = (Document) doc.get("apply");
		
		//investmentManager
		Document investmentManager = (Document) oldApply.get("investmentManager");
		if(investmentManager != null){
			Document newInvestmentManager = new Document();
			newInvestmentManager.put("NAME", investmentManager.getString("name"));
			newInvestmentManager.put("VALUE", investmentManager.getString("value"));
			newApply.put("investmentManager", newInvestmentManager);
		}
		//处理大区reportingUnit
		Document reportingUnit = (Document) oldApply.get("reportingUnit");
		if(reportingUnit != null){
			Document newReportingUnit = new Document();
			newReportingUnit.put("VALUE", reportingUnit.getString("name"));
			newReportingUnit.put("KEY", reportingUnit.getString("value"));
			newApply.put("reportingUnit", newReportingUnit);
		}
		
		//companyHeader
		Document companyHeader = (Document) oldApply.get("companyHeader");
		if(companyHeader != null){
			Document newCompanyHeader = new Document();
			newCompanyHeader.put("VALUE", companyHeader.getString("value"));
			newCompanyHeader.put("NAME", companyHeader.getString("name"));
			newApply.put("companyHeader", newCompanyHeader);
		}
		
		//grassrootsLegalStaff
		Document grassrootsLegalStaff = (Document) oldApply.get("grassrootsLegalStaff");
		if(grassrootsLegalStaff != null){
			Document newGrassrootsLegalStaff = new Document();
			newGrassrootsLegalStaff.put("VALUE", grassrootsLegalStaff.getString("value"));
			newGrassrootsLegalStaff.put("NAME", grassrootsLegalStaff.getString("name"));
			newApply.put("grassrootsLegalStaff", newGrassrootsLegalStaff);
		}
		
		//directPerson
		Document directPerson = (Document) oldApply.get("directPerson");
		if(directPerson != null){
			Document newDirectPerson = new Document();
			newDirectPerson.put("VALUE", directPerson.getString("value"));
			newDirectPerson.put("NAME", directPerson.getString("name"));
			newApply.put("grassrootsLegalStaff", newDirectPerson);
		}
		
		//investmentPerson
		Document investmentPerson = (Document) oldApply.get("investmentPerson");
		if(investmentPerson != null){
			Document newInvestmentPerson = new Document();
			newInvestmentPerson.put("VALUE", investmentPerson.getString("value"));
			newInvestmentPerson.put("NAME", investmentPerson.getString("name"));
			newApply.put("investmentPerson", newInvestmentPerson);
		}
		
		//createBy
		Document createBy = new Document();
		createBy.put("VALUE", oldApply.getString("create_by"));
		createBy.put("NAME", oldApply.getString("create_name"));
		newApply.put("createBy", createBy);
		//projectNum
		newApply.put("projectNum", olddoc.getString("projectNo"));
		
		newApply.remove("create_by");
		newApply.remove("create_name");
		
		//处理附件中的人
		ArrayList<Document> attachment = (ArrayList<Document>) doc.get("attachment");
		for (Document f : attachment) {
			ArrayList<Document> files = (ArrayList<Document>) f.get("files");
			if(Util.isNotEmpty(files)){
				for (Document ff : files) {
					Document programmed = (Document) ff.get("programmed");
					if(Util.isNotEmpty(programmed)){
						programmed.put("NAME", programmed.getString("name"));
						programmed.put("VALUE", programmed.getString("value"));
						programmed.remove("name");
						programmed.remove("value");
						
					}
					
					Document approved = (Document) ff.get("approved");
					if(Util.isNotEmpty(approved)){
						approved.put("NAME", approved.getString("name"));
						approved.put("VALUE", approved.getString("value"));
						approved.remove("name");
						approved.remove("value");
					}
				}
			}
			
		}
		
		return doc;
	}


	@Override
	public void updateAfterStartflow(String businessId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		map.put("apply_date", Util.now());
		map.put("wf_state", "1");
		this.formalAssessmentInfoMapper.updateAfterStartflow(map);
	}


	@Override
	public void updateServiceTypeOpinionByBusinessId(String json,
			String businessId) {
		Document currentTaskVar = Document.parse(json);
		Document data = new Document();
		data.put("tzProtocolOpinion", currentTaskVar.getString("tzProtocolOpinion"));
		data.put("cesuanFileOpinion", currentTaskVar.getString("cesuanFileOpinion"));
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void saveLegalReviewInfo(String json,String businessId) {
		Document doc = Document.parse(json);
		List<Document> commentsList = (List<Document>) doc.get("commentsList");
		//获取需要反馈时间
		String feedbackTime = (String)doc.get("feedbackTime");
		if(Util.isNotEmpty(feedbackTime)){
			//处理反馈时间
			if(Util.isNotEmpty(commentsList)){
				for (Document comments : commentsList) {
					if("1".equals((String)comments.get("isLegalEdit"))){
						comments.put("commentDate", feedbackTime);
					}
				}
			}
		}
		
		//附件列表操作
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		Map<String, Object> docc = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		//获取所有文件信息
		
		List<Document> attachmentOldList =(List<Document>) docc.get("attachment");
				
		List<Document> attachmentNewList = (List<Document>) doc.get("attachmentNew");
		if(Util.isNotEmpty(attachmentNewList)){
			for (Document attachmentNew : attachmentNewList) {
				Document attachmentUList = (Document) attachmentNew.get("attachmentUList");
				Object attachment_new1 = attachmentNew.get("attachment_new");
				if(Util.isNotEmpty(attachmentUList) && Util.isNotEmpty(attachment_new1)){
					Document attachment_new = (Document)attachment_new1;
					//新文件类型的UUID
					String newUuid = (String) attachmentUList.get("UUID");
					String newVersion = (String) attachment_new.get("version");
					//遍历旧文件，匹配旧文件类型与版本号
					
					for (Document attachmentOld : attachmentOldList) {
						if(Util.isNotEmpty(attachmentOld) && attachmentOld.get("UUID").equals(newUuid)){
							//旧附件中是否存在同类型的新的附件，根据版本号
							boolean hasFile = false;
							List<Document> files = (List<Document>) attachmentOld.get("files");
							if(Util.isNotEmpty(files)){
								for (int i =0 ;i<files.size() ; i++){
									String oldVersion = (String) files.get(i).get("version");
									if(oldVersion.equals(newVersion)){
										files.set(i, attachment_new);
										hasFile = true;
									}
								}
							}
							//不存在新的附件
							if(!hasFile){
								files.add(attachment_new);
							}
						}
					}
				}
			}
		}
		Document newDocument = new Document();
		newDocument.put("approveLegalAttachment", doc);
		String json2 = JsonUtil.toJson(attachmentOldList);
		List<Document> attachmentOldList2 = JsonUtil.fromJson(json2, List.class);
		newDocument.put("attachment", attachmentOldList2);
		this.baseMongo.updateSetByObjectId(businessId, newDocument, Constants.RCM_FORMALASSESSMENT_INFO);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void saveReviewInfo(String json,String businessId) {
		Document doc = Document.parse(json);
		List<Document> commentsList = (List<Document>) doc.get("commentsList");
		//获取需要反馈时间
		String feedbackTime = (String)doc.get("feedbackTime");
		if(Util.isNotEmpty(feedbackTime)){
			//处理反馈时间
			if(Util.isNotEmpty(commentsList)){
				for (Document comments : commentsList) {
					if("1".equals((String)comments.get("isReviewLeaderEdit"))){
						comments.put("commentDate", feedbackTime);
					}
				}
			}
		}
		Map<String, Object> docc = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		//获取所有文件信息
		List<Document> attachmentOldList =(List<Document>) docc.get("attachment");
		
		//获取初步评审意见上传的文件
		List<Document> attachmentNewList = (List<Document>) doc.get("attachmentNew");
		if(Util.isNotEmpty(attachmentNewList)){
			for (Document attachmentNew : attachmentNewList) {
				//新文件类型的UUID
				Document attachmentUList= (Document) attachmentNew.get("attachmentUList");
				Object attachment_new1 = attachmentNew.get("attachment_new");
				if(Util.isNotEmpty(attachmentUList) && Util.isNotEmpty(attachment_new1)){
					Document attachment_new = (Document)attachment_new1;
					//新文件类型的UUID
					String newUuid = (String) attachmentUList.get("UUID");
					String newVersion = (String) attachment_new.get("version");
					//遍历旧文件，匹配旧文件类型与版本号
					
					for (Document attachmentOld : attachmentOldList) {
						
						if(Util.isNotEmpty(attachmentOld) && attachmentOld.get("UUID").equals(newUuid)){
							//旧附件中是否存在同类型的新的附件，根据版本号
							boolean hasFile = false;
							
							List<Document> files = (List<Document>) attachmentOld.get("files");
							if(Util.isNotEmpty(files)){
								for (int i =0 ;i<files.size() ; i++){
									String oldVersion = (String) files.get(i).get("version");
									if(oldVersion.equals(newVersion)){
										files.set(i, attachment_new);
										hasFile = true;
									}
								}
							}
							//不存在新的附件
							if(!hasFile){
								files.add(attachment_new);
							}
						}
					}
				}
			}
		}
		
		Document newDocument = new Document();
		newDocument.put("approveAttachment", doc);
		String json2 = JsonUtil.toJson(attachmentOldList);
		List<Document> attachmentOldList2 = JsonUtil.fromJson(json2, List.class);
		newDocument.put("attachment", attachmentOldList2);
		this.baseMongo.updateSetByObjectId(businessId, newDocument, Constants.RCM_FORMALASSESSMENT_INFO);
	}


	@SuppressWarnings("unchecked")
	@Override
	public void saveFixGroupOption(String json) {
		
		Document doc = Document.parse(json);
		
		String opinion = doc.get("opinion").toString();
		
		String businessId = doc.get("businessId").toString();
		
		Document user = (Document) doc.get("user");
		
		String userUuid = user.get("UUID").toString();
		
		String userName = user.get("userName").toString();
		
		
		Map<String, Object> formalAssessment = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		
		List<Map<String, Object>> fixGroupOption = (List<Map<String, Object>>) formalAssessment.get("fixGroupOption");
		
		if(Util.isNotEmpty(fixGroupOption)){

			int flag = 0;
			
			for (Map<String, Object> o : fixGroupOption) {
				//mongo中存在当前人的意见，替换掉
				if(o.get("VALUE").toString().equals(userUuid)){
					o.put("opinion", opinion);
					flag = 1;
				}
			}
			
			//mongo中不存在当前人意见，新增
			if(flag == 0){
				Map<String, Object> optionItem = new HashMap<String,Object>();
				optionItem.put("NAME", userName);
				optionItem.put("VALUE", userUuid);
				optionItem.put("opinion", opinion);
				fixGroupOption.add(optionItem);
			}
			
		}else{
			
			fixGroupOption = new ArrayList<Map<String,Object>>();
			
			Map<String, Object> optionItem = new HashMap<String,Object>();
			
			optionItem.put("NAME", userName);
			
			optionItem.put("VALUE", userUuid);
			
			optionItem.put("opinion", opinion);
			
			fixGroupOption.add(optionItem);
		}
		
		
		Map<String, Object> data = new HashMap<String,Object>();
		
		data.put("fixGroupOption", fixGroupOption);

		this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
	}
	@Override
	public void deleteByBusinessId (String businessId){
		this.formalAssessmentInfoMapper.deleteByBusinessId(businessId);
	}
	
	@Override
	public Map<String, Object> queryRelationByTypeId (String businessId,String relationTypeCode){
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("businessId", businessId);
		data.put("relationTypeId", relationTypeCode);
		return this.formalAssessmentInfoMapper.queryRelationByTypeId(data);
	}


	@Override
	public List<Map<String, Object>> getOldProjectRelationByBusinessId(String businessId) {
		Map<String, Object> params = new HashMap<String,Object>();
		
		params.put("businessId", businessId);
		
		return this.formalAssessmentInfoMapper.getOldProjectRelationByBusinessId(params);
	}


	@Override
	public Map<String, Object> queryWaitingByConditions(String userId,
			String businessId) {
		String sql = this.bpmnAuditService.queryWaitdealSql(Constants.FORMAL_ASSESSMENT, userId).getData();
		Map<String,Object> conditions = new HashMap<String,Object>();
		conditions.put("userId", userId);
		conditions.put("businessId", businessId);
		conditions.put("waitSql", sql);
		return this.formalAssessmentInfoMapper.queryWaitingByConditions(conditions);
	}


	@Override
	public Map<String, Object> queryAuditedByConditions(String userId,
			String businessId) {
		Map<String,Object> conditions = new HashMap<String,Object>();
		conditions.put("userId", userId);
		conditions.put("businessId", businessId);
		return this.formalAssessmentInfoMapper.queryAuditedByConditions(conditions);
	}


	@Override
	public PageAssistant querySubmitedByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
//		if(orderBy == null){
//			orderBy = " ta.create_date desc ";
//		}
//		params.put("orderBy", orderBy);
		List<Map<String,Object>> list = this.formalAssessmentInfoMapper.querySubmitedByPage(params);
		//循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, "rcm_formalAssessment_info");
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
		return page;
	}


	@Override
	public void updatePersonById(Map<String, Object> params) {
		this.formalAssessmentInfoMapper.updatePersonById(params);
	}

	@Override
	public List<Map<String, Object>> queryFixGroupOption(String businessId) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("businessId", businessId);
		return this.formalAssessmentInfoMapper.queryFixGroupOption(params);
	}

	@Override
	public void updateCompleteDateById(String businessId, Date now) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("businessId", businessId);
		params.put("complete_date", now);
		formalAssessmentInfoMapper.updateCompleteDateById(params);
	}


	@Override
	public void queryInformationList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("userId", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " apply_date desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.formalAssessmentInfoMapper.queryInformationList(params);
		page.setList(list);
	}


	@Override
	public void queryInformationListed(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " create_date desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.formalAssessmentInfoMapper.queryInformationListed(params);
		page.setList(list);
	}


	@SuppressWarnings("unchecked")
	@Override
	public void addConferenceInformation(String json) {
		Document pfr = Document.parse(json);
		Document meetingInfo = new Document();
		
		//修改oracle的stage状态
		/*Map<String, Object> map = new HashMap<String, Object>();
		//map.put("stage", "3.5");
		// 数据所在目录修改 状态随之修改
		map.put("stage", "3.9");
		map.put("need_meeting", "1");
		map.put("metting_commit_time", Util.getTime());
		map.put("businessId", pfr.getString("formalId"));
		this.formalAssessmentInfoMapper.updateStage(map);*/
		BasicDBObject query = new BasicDBObject();
		query.put("formalId",pfr.getString("formalId"));
		List<Map<String, Object>> queryByConditions = baseMongo.queryByCondition(query,Constants.FORMAL_MEETING_INFO);
		Map<String, Object> queryByCondition = new HashMap<String, Object>();
		if (Util.isNotEmpty(queryByConditions)){
			queryByCondition = queryByConditions.get(0);
		}
		//其他会议信息保存到metting表中
		Document apply = (Document) pfr.get("apply");
		String projectName = apply.getString("projectName");
		meetingInfo.put("formalId", pfr.getString("formalId"));
		meetingInfo.put("projectName", projectName);
		meetingInfo.put("user_id", ThreadLocalUtil.getUserId());
		meetingInfo.put("serviceType", apply.get("serviceType"));
		meetingInfo.put("isUrgent", pfr.get("isUrgent"));
		meetingInfo.put("projectRating", pfr.get("projectRating"));
		meetingInfo.put("projectType1", pfr.get("projectType1"));
		meetingInfo.put("projectType2", pfr.get("projectType2"));
		meetingInfo.put("projectType3", pfr.get("projectType3"));
		meetingInfo.put("ratingReason", pfr.get("ratingReason"));
		meetingInfo.put("participantMode", pfr.get("participantMode"));
		meetingInfo.put("division", pfr.get("division"));
		meetingInfo.put("investment", pfr.get("investment"));
		meetingInfo.put("agenda", pfr.get("agenda"));
		meetingInfo.put("contacts", pfr.get("contacts"));
		meetingInfo.put("telephone", pfr.get("telephone"));
		
		meetingInfo.put("create_date", Util.getTime());
		meetingInfo.put("state", "1");
		
		if(Util.isEmpty(queryByCondition)){
			this.baseMongo.save(meetingInfo, Constants.FORMAL_MEETING_INFO);
		} else {
			String id = queryByCondition.get("_id").toString();
			this.baseMongo.updateSetByObjectId(id, meetingInfo, Constants.FORMAL_MEETING_INFO);
		}
		
	}

	
	@Override
	public void updateStageById(String businessId, String stage,String need_meeting,String projectType) {
		Map<String, Object> map = new HashMap<String, Object>();
		String[] businessIdArr = businessId.split(",");
		for (String id : businessIdArr) {
			map.put("businessId", id);
			map.put("stage", "9");
			map.put("need_meeting", "0");
			map.put("metting_commit_time", Util.getTime());
			map.put("projectType", projectType);
			
			if ("pfr".equals(projectType)){
				map.put("table", Constants.RCM_FORMALASSESSMENT_INFO);
			} else if ("pre".equals(projectType)) {
				map.put("table", Constants.RCM_PRE_INFO);
			} else {
				map.put("table", Constants.RCM_BULLETIN_INFO);
			}
			
			this.formalAssessmentInfoMapper.updateStageByProjectType(map);
			
			
			this.daxtService.prfStart(id);
			//给投资推项目信息数据
			TzAfterNoticeClient tzAfterPreReviewClient = new TzAfterNoticeClient(id, "2",null);
			Thread tt = new Thread(tzAfterPreReviewClient);
			tt.start();
		}
	}


	@Override
	public Map<String, Object> getOracleByBusinessId(String businessId) {
		return formalAssessmentInfoMapper.getFormalAssessmentById(businessId);
	}

	@Override
	public Result saveOrUpdateForTz(Document doc,Result result) {
		String businessid = null;
		Map<String, Object> map = new HashMap<String, Object>();
		//判断新增或修改
		doc.append("create_date", Util.format(Util.now()));
		doc.append("currentTimeStamp", Util.format(Util.now(), "yyyyMMddHHmmssSSS"));
		if(!doc.containsKey("businessid") || Util.isEmpty(doc.get("businessid"))){
			//组织数据
			doc.append("state", "1");
			doc.put("_id", new ObjectId());
			Map<String, Object> dataForOracle = packageDataForOracle(doc);
			//插入oracle
			formalAssessmentInfoMapper.createForTz(dataForOracle);
			String is_supplement_review = (String) doc.get("is_supplement_review");
			if("1".equals(is_supplement_review)){
				String projectNum = (String) dataForOracle.get("projectNum");
				List<Map<String, Object>> decisionList = formalAssessmentInfoMapper.getDecisionByProjectNum(projectNum);
				if(Util.isNotEmpty(decisionList)){
					Map<String, Object> decision = decisionList.get(0);
					String dbusinessid = (String) decision.get("BUSINESSID");
					Map<String, Object> decisionMongo = this.baseMongo.queryById(dbusinessid, Constants.RCM_NOTICEDECISION_INFO);
					//执行要求
					String implementationRequirements = (String) decisionMongo.get("implementationRequirements");
					//项目投资前须落实事项	
					String implementationMatters = (String) decisionMongo.get("implementationMatters");
					
					Map<String, Object> apply = (Map<String, Object>) doc.get("apply");
					Map<String, Object> supplement = (Map<String, Object>) apply.get("supplement");
					if(Util.isEmpty(supplement)){
						supplement = new HashMap<String, Object>();
						supplement.put("preDecisionDate", Util.format((Date)decision.get("DATEOFMEETING")));
						supplement.put("preDecisionResult", (String)decision.get("CONSENTTOINVESTMENT"));
						supplement.put("executiveRequirements", implementationRequirements);
						supplement.put("implementationItems", implementationMatters);
						apply.put("supplement", supplement);
					}else{
						supplement.put("preDecisionDate", Util.format((Date)decision.get("DATEOFMEETING")));
						supplement.put("preDecisionResult", (String)decision.get("CONSENTTOINVESTMENT"));
						supplement.put("executiveRequirements", implementationRequirements);
						supplement.put("implementationItems", implementationMatters);
					}
				}
			}
			
			//插入mongdb
			baseMongo.save(doc, Constants.RCM_FORMALASSESSMENT_INFO);
			businessid = doc.get("_id").toString();
		}else{
			//删除oracle
			businessid = doc.getString("businessid");
			formalAssessmentInfoMapper.deleteByBusinessId(businessid);
			//组织数据
			doc.append("update_date", Util.format(Util.now()));
			doc.put("_id", businessid);
			Map<String, Object> dataForOracle = packageDataForOracle(doc);
			//插入oracle
			formalAssessmentInfoMapper.createForTz(dataForOracle);
			//修改mongo
			doc.remove("_id");
			this.baseMongo.updateSetByObjectId(businessid, doc, Constants.RCM_FORMALASSESSMENT_INFO);
		}
		this.updateStageById(businessid, "1");
		map.put("result_wf_state", "0");
		map.put("result_status", "true");
		map.put("id", businessid);
		String prefix = PropertiesUtil.getProperty("domain.allow");
		map.put("URL", prefix+"/html/index.html#/FormalAssessmentDetailView/"+businessid+"/"+Util.encodeUrl("#/FormalAssessmentInfoList/0"));
		String json = JsonUtil.toJson(map);
		result.setResult_data(json);
		return result;
	}
	
	@Override
	public Result saveOrUpdateForTz_V01(Document doc,Result result) {
		String businessid = null;
		Map<String, Object> map = new HashMap<String, Object>();
		//判断新增或修改
		doc.append("create_date", Util.format(Util.now()));
		doc.append("currentTimeStamp", Util.format(Util.now(), "yyyyMMddHHmmssSSS"));
		if(!doc.containsKey("businessid") || Util.isEmpty(doc.get("businessid"))){
			//组织数据
			doc.append("state", "1");
			doc.put("_id", new ObjectId());
			Map<String, Object> dataForOracle = packageDataForOracle(doc);
			//插入oracle
			formalAssessmentInfoMapper.createForTz(dataForOracle);
			String is_supplement_review = (String) doc.get("is_supplement_review");
			if("1".equals(is_supplement_review)){
				String projectNum = (String) dataForOracle.get("projectNum");
				List<Map<String, Object>> decisionList = formalAssessmentInfoMapper.getDecisionByProjectNum(projectNum);
				if(Util.isNotEmpty(decisionList)){
					Map<String, Object> decision = decisionList.get(0);
					String dbusinessid = (String) decision.get("BUSINESSID");
					Map<String, Object> decisionMongo = this.baseMongo.queryById(dbusinessid, Constants.RCM_NOTICEDECISION_INFO);
					//执行要求
					String implementationRequirements = (String) decisionMongo.get("implementationRequirements");
					//项目投资前须落实事项	
					String implementationMatters = (String) decisionMongo.get("implementationMatters");
					
					Map<String, Object> apply = (Map<String, Object>) doc.get("apply");
					Map<String, Object> supplement = (Map<String, Object>) apply.get("supplement");
					if(Util.isEmpty(supplement)){
						supplement = new HashMap<String, Object>();
						supplement.put("preDecisionDate", Util.format((Date)decision.get("DATEOFMEETING")));
						supplement.put("preDecisionResult", (String)decision.get("CONSENTTOINVESTMENT"));
						supplement.put("executiveRequirements", implementationRequirements);
						supplement.put("implementationItems", implementationMatters);
						apply.put("supplement", supplement);
					}else{
						supplement.put("preDecisionDate", Util.format((Date)decision.get("DATEOFMEETING")));
						supplement.put("preDecisionResult", (String)decision.get("CONSENTTOINVESTMENT"));
						supplement.put("executiveRequirements", implementationRequirements);
						supplement.put("implementationItems", implementationMatters);
					}
				}
			}
			
			//插入mongdb
			baseMongo.save(doc, Constants.RCM_FORMALASSESSMENT_INFO);
			businessid = doc.get("_id").toString();
		}else{
			//删除oracle
			businessid = doc.getString("businessid");
			formalAssessmentInfoMapper.deleteByBusinessId(businessid);
			//组织数据
			doc.append("update_date", Util.format(Util.now()));
			doc.put("_id", businessid);
			Map<String, Object> dataForOracle = packageDataForOracle(doc);
			//插入oracle
			formalAssessmentInfoMapper.createForTz(dataForOracle);
			//修改mongo
			doc.remove("_id");
			this.baseMongo.updateSetByObjectId(businessid, doc, Constants.RCM_FORMALASSESSMENT_INFO);
		}
		this.formalAssessmentInfoCreateService.saveDefaultProRoleForTz(doc);
		this.updateStageById(businessid, "1");
		map.put("result_wf_state", "0");
		map.put("result_status", "true");
		map.put("id", businessid);
		String prefix = PropertiesUtil.getProperty("domain.allow");
		map.put("URL", prefix+"/html/index.html#/formalAssessmentInfo/"+businessid+"/2");
		map.put("URL_PRO", prefix+"/html/index.html#/projectInfoAllBoardView/"+businessid+"/#"+Util.encodeUrl("/JTI1MjMlMkY="));
		String json = JsonUtil.toJson(map);
		result.setResult_data(json);
		return result;
	}
	
	@Override
	public PageAssistant queryAllInfoByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String,Object>> list = formalAssessmentInfoMapper.queryAllInfoByPage(params);
		page.setList(list);
		return page;
	}

	@Override
	public int countAll() {
		return formalAssessmentInfoMapper.countAll();
	}


	@Override
	public void updateFixGroupIds(String businessId, String fixedGroupIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("fixedGroupIds", fixedGroupIds);
		formalAssessmentInfoMapper.updateFixGroupIds(params);
	}
	

	@Override
	public List<Map<String, Object>> getProjectReport0706() {
		return formalAssessmentInfoMapper.getProjectReport0706();
	}
	
	@Override
	public List<Map<String, Object>> getProjectReport0710() {
		return formalAssessmentInfoMapper.getProjectReport0710();
	}

	@Override
	public PageAssistant getProjectReportDetails0706(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String,Object>> list = formalAssessmentInfoMapper.getProjectReportDetails0706(params);
		page.setList(list);
		return page;
	}


	@Override
	public PageAssistant getProjectReportDetails0710(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String,Object>> list = formalAssessmentInfoMapper.getProjectReportDetails0710(params);
		page.setList(list);
		return page;
	}


	@Override
	public void deleteOracleAndMongdbByBusinessId(String businessid) {
		formalAssessmentInfoMapper.deleteByBusinessId(businessid);	
		baseMongo.deleteById(businessid, Constants.RCM_FORMALASSESSMENT_INFO);
	}

	@Override
	public void queryStartByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("userId", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " apply_date desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.formalAssessmentInfoMapper.queryStartByPage(params);
		page.setList(list);
		
	}


	@Override
	public void queryOverByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " create_date desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.formalAssessmentInfoMapper.queryOverByPage(params);
		page.setList(list);
	}


	@Override
	public void updateStageById(String businessId, String stage) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("stage", stage);
		this.formalAssessmentInfoMapper.updateStageById(params);
	}


	@Override
	public void saveMajMemberInfo(String json, String businessId, Map<String, Object> user) {
		//因为是数组类型的Json，所以先转为jsonarray，然后循环转为document
		JSONArray jsonArray = new JSONArray(json);
		ArrayList<Document> documentList = new ArrayList<Document>();
		for (int i = 0; i < jsonArray.length(); i++) {
			String string = jsonArray.get(i).toString();
			Document document = Document.parse(string);
			documentList.add(document);
		}
		//如果是当前人添加的数据，手动增加当前日期
		for (Document document : documentList) {
			String userId = (String)user.get("UUID");
			Document majorApply = (Document)document.get("majorApply");
			String majorApplyID = (String)majorApply.get("VALUE");
			if(userId.equals(majorApplyID)){
				String time = Util.getTime();
				document.put("date", time);
			}
		}
		Document doc = new Document();
		doc.put("approveMajorCommonts", documentList);
		baseMongo.updateSetByObjectId(businessId, doc, Constants.RCM_FORMALASSESSMENT_INFO);
		
	}


	@Override
	public void saveMajorMemberOption(String json, String businessId) {
//		Map<String, Object> formalAssessmentMongo = baseMongo.queryById(businessId, RCM_FORMALASSESSMENT_INFO);
//		Map<String, Object> taskallocation= (Map<String, Object>) formalAssessmentMongo.get("taskallocation");
//		JSONArray jsonArray = new JSONArray(json);
//		ArrayList<Document> documentList = new ArrayList<Document>();
//		for (int i = 0; i < jsonArray.length(); i++) {
//			String string = jsonArray.get(i).toString();
//			Document document = Document.parse(string);
//			documentList.add(document);
//		}
////		Map<String, Object> taskallocation = new HashMap<String,Object>();
//		for (Document document : documentList) {
//			document.remove("$$hashKey");
//		}		
//		taskallocation.put("professionalReviewers", documentList);
//		//查mongo根据id查formalAssessment
//		
//		formalAssessmentMongo.put("taskallocation", taskallocation);
//		baseMongo.updateSetByObjectId(businessId, formalAssessmentMongo, RCM_FORMALASSESSMENT_INFO);
	}


	@Override
	public List<Map<String, Object>> queryPertainAreaIsNull() {
		return formalAssessmentInfoMapper.queryPertainAreaIsNull();
	}


	@Override
	public void updatePertainAreaId(String id, String pertainAreaId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("pertainAreaId", pertainAreaId);
		formalAssessmentInfoMapper.updatePertainAreaId(params);
	}


	@Override
	public void startPigeonholeByBusinessId(String businessId,Date pigeonholeTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("pigeonholeStatus", "1");
		params.put("pigeonholeTime", pigeonholeTime);
		formalAssessmentInfoMapper.startPigeonholeByBusinessId(params);
	}
	
	@Override
	public void updatePigeStatByBusiId(String businessId,String status) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("pigeonholeStatus",status);
		formalAssessmentInfoMapper.updatePigeStatByBusiId(params);
	}
	
	@Override
	public void cancelPigeonholeByBusinessId(String businessId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("pigeonholeStatus", "");
		params.put("pigeonholeTime",null);
		formalAssessmentInfoMapper.cancelPigeonholeByBusinessId(params);
	}


	@Override
	public List<Map<String, Object>> queryFormalPertainArea() {
		
		List<Map<String, Object>> list = this.formalAssessmentInfoMapper.queryAllFormal();
		
		List<String> areaList = new ArrayList<String>();
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		for (Map<String, Object> map : list) {
			
			String pertainAreaId = (String) map.get("PERTAINAREAID");
			
			if(!areaList.contains(pertainAreaId)){
				
				areaList.add(pertainAreaId);
				
			}
			
		}
		
		for (String pertainAreaId : areaList) {
			
			Map<String, Object> org = this.orgService.queryByPkvalue(pertainAreaId);
			
			result.add(org);
		
		}
			
		return result;
	}


	@Override
	public List<Map<String, Object>> queryFormalCount(String wf_state,String stage,String result, String pertainAreaId, String serviceTypeId,String year) {
		Map<String, Object> map = new HashMap<String,Object>();
		if(Util.isNotEmpty(wf_state)){
			map.put("wf_state", wf_state.split(","));
		}
		if(Util.isNotEmpty(stage)){
			map.put("stage", stage.split(","));
		}
		if(Util.isNotEmpty(pertainAreaId)){
			//处理大区id
			map.put("pertainAreaId", pertainAreaId.split(","));
		}
		if(Util.isNotEmpty(serviceTypeId)){
			//处理serviceTypeId
			map.put("serviceTypeId", serviceTypeId.split(","));
		}
		if(Util.isNotEmpty(result)){
			//处理serviceTypeId
			map.put("result", result.split(","));
		}
		
		if(Util.isEmpty(year)){
			int y = Util.now().getYear()+1900;
			map.put("year", y);
		}else{
			map.put("year", year);
		}
		return this.formalAssessmentInfoMapper.queryFormalCount(map);
	}


	@Override
	public List<Map<String, Object>> queryByStageAndstate(String stage,String state) {
		
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("wf_state", state);
		map.put("stage", stage);
		return this.formalAssessmentInfoMapper.queryByStageAndstate(map);
	}


	@Override
	public List<Map<String, Object>> queryAllLargePersonIsNull() {
		return this.formalAssessmentInfoMapper.queryAllLargePersonIsNull();
	}


	@Override
	public List<Map<String, Object>> queryContractByBusinessId(String businessId) {
		return formalAssessmentInfoMapper.queryContractByBusinessId(businessId);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void updateAttachment(String json) {
		
		Document doc = Document.parse(json);
		
		String itemUUID = (String) doc.get("itemUUID");
		String itemName = (String) doc.get("itemName");
		String businessId = (String) doc.get("businessId");
		String fileName = (String) doc.get("fileName");
		String filePath = (String) doc.get("filePath");
		String previewPath = (String) doc.get("previewPath");
		String downLoadPath = (String) doc.get("downLoadPath");
		Boolean newItem = (Boolean) doc.get("newItem");
		Map<String, Object> fomralOracle = this.getOracleByBusinessId(businessId);
		String stage = (String) fomralOracle.get("STAGE");
		
		String createby = (String) fomralOracle.get("CREATEBY");
		String reviewLeaderId = (String) fomralOracle.get("REVIEWPERSONID");
		String legalLeaderId = (String) fomralOracle.get("LEGALREVIEWPERSONID");
		String grassRootId = (String) fomralOracle.get("GRASSROOTSLEGALPERSONID");
		
		//当前人
		String userId = ThreadLocalUtil.getUserId();
		
		Map<String, Object> formalMongo = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) formalMongo.get("attachment");
		
		ObjectId objectId = new ObjectId();
		
		if(Util.isNotEmpty(newItem) && newItem){
			//新增类型与附件
			Map<String, Object> newAttachment = new HashMap<String, Object>();
			newAttachment.put("ITEM_NAME", itemName);
			newAttachment.put("UUID", itemUUID);
			List<Map<String, Object>> files = new ArrayList<Map<String,Object>>();
			
			Map<String, Object> file = new HashMap<String,Object>();
			file.put("fileName", fileName);
			file.put("filePath", filePath);
			file.put("upload_date", Util.format(Util.now()));
			file.put("version", "1");
			
			
			file.put("UUID", objectId.toHexString());
			Map<String, Object> owner = new HashMap<String,Object>();
			Map<String, Object> user = ThreadLocalUtil.getUser();
			
			owner.put("NAME", (String)user.get("NAME"));
			owner.put("VALUE", (String)user.get("UUID"));
			
			file.put("owner", owner);
			file.put("approved", owner);
			file.put("programmed", owner);
			file.put("previewPath", previewPath);
			file.put("downLoadPath", downLoadPath);
			files.add(file);
			newAttachment.put("files", files);
			
			this.saveFileLogs(businessId, (String)user.get("UUID"), (String)user.get("NAME"), objectId.toHexString(), fileName, filePath, "新增投资部门附件类型与附件");
			attachmentList.add(newAttachment);
			
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("attachment", attachmentList);
			this.baseMongo.updateSetByObjectId(businessId, map , Constants.RCM_FORMALASSESSMENT_INFO);
		}else{
			for (Map<String, Object> attachment : attachmentList) {
				String uuid = (String) attachment.get("UUID");
				if(uuid.equals(itemUUID)){
					List<Map<String, Object>> files;
					if(Util.isNotEmpty(attachment.get("files"))){
						files = (List<Map<String, Object>>) attachment.get("files");
					}else{
						files = new ArrayList<Map<String, Object>>();
					}		
					Map<String, Object> newFile = new HashMap<String,Object>();
					newFile.put("fileName", fileName);
					newFile.put("filePath", filePath);
					newFile.put("upload_date", Util.format(Util.now()));
					
					if("1".equals(stage) || "2".equals(stage)){
						newFile.put("version", "1");
					}else{
						newFile.put("version", getMaxVersion(files));
					}
					
					newFile.put("UUID", objectId.toHexString());
					Map<String, Object> owner = new HashMap<String,Object>();
					Map<String, Object> user = ThreadLocalUtil.getUser();
					
					owner.put("NAME", (String)user.get("NAME"));
					owner.put("VALUE", (String)user.get("UUID"));
					
					newFile.put("owner", owner);
					newFile.put("approved", owner);
					newFile.put("programmed", owner);
					newFile.put("previewPath", previewPath);
					newFile.put("downLoadPath", downLoadPath);
					this.saveFileLogs(businessId, (String)user.get("UUID"), (String)user.get("NAME"), objectId.toHexString(), fileName, filePath, "新增投资部门附件");
					files.add(newFile);
					attachment.put("files", files);
					Map<String, Object> map = new HashMap<String,Object>();
					map.put("attachment", attachmentList);
					this.baseMongo.updateSetByObjectId(businessId, map , Constants.RCM_FORMALASSESSMENT_INFO);
				}
			}
		}
		//存未读标记
		Map<String, Object> fileRemark = (Map<String, Object>) formalMongo.get("fileRemark");
		if(Util.isEmpty(fileRemark)){
			fileRemark = new HashMap<String,Object>();
		}
		//评审负责人未读标记
		List<String> reviewLeader = (List<String>) fileRemark.get("reviewLeader");
		if(Util.isEmpty(reviewLeader)){
			reviewLeader = new ArrayList<String>();
		}
		//投资经理未读标记
		List<String> investmentManager = (List<String>) fileRemark.get("investmentManager");
		if(Util.isEmpty(investmentManager)){
			investmentManager = new ArrayList<String>();
		}
		//法律负责人未读标记
		List<String> legalLeader = (List<String>) fileRemark.get("legalLeader");
		if(Util.isEmpty(legalLeader)){
			legalLeader = new ArrayList<String>();
		}
		//基层法务未读标记
		List<String> grassRoot = (List<String>) fileRemark.get("grassRoot");
		if(Util.isEmpty(grassRoot)){
			grassRoot = new ArrayList<String>();
		}
		
		if(createby.equals(userId)){
			//当前人是投资经理
			reviewLeader.add(objectId.toHexString());
			legalLeader.add(objectId.toHexString());
			grassRoot.add(objectId.toHexString());
		}else if (Util.isNotEmpty(reviewLeaderId) && reviewLeaderId.equals(userId)){
			//当前人是评审负责人
			legalLeader.add(objectId.toHexString());
			grassRoot.add(objectId.toHexString());
			investmentManager.add(objectId.toHexString());
		}else if (Util.isNotEmpty(grassRootId) && grassRootId.equals(userId)){
			//当前人是基层法务
			legalLeader.add(objectId.toHexString());
			reviewLeader.add(objectId.toHexString());
			investmentManager.add(objectId.toHexString());
		}else if (Util.isNotEmpty(legalLeaderId) && legalLeaderId.equals(userId)){
			//当前人是法律负责人
			grassRoot.add(objectId.toHexString());
			reviewLeader.add(objectId.toHexString());
			investmentManager.add(objectId.toHexString());
		}
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("fileRemark", fileRemark);
		this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
	}
	private String getMaxVersion(List<Map<String,Object>> files){
		int v = 1;
		for (Map<String, Object> file : files) {
			String version = (String) file.get("version");
			if(version.contains(".")){
				version = version.substring(0,version.indexOf("."));
			}
			int v2 = Integer.parseInt(version);
			if(v < v2) v = v2;
		}
		return ++v+"";
	}
	
	@Override
	public Result updateRiskAttachment(String json) {
		Result result = new Result();
		
		Document doc = Document.parse(json);
		String businessId = (String) doc.get("businessId");
		String fileName = (String) doc.get("fileName");
		String filePath = (String) doc.get("filePath");
		String previewPath = (String) doc.get("previewPath");
		String downLoadPath = (String) doc.get("downLoadPath");
		Boolean newFile = (Boolean) doc.get("newFile");
		String UUID = (String) doc.get("UUID");
		
		Map<String, Object> file = new HashMap<String,Object>();
		file.put("UUID", UUID);
		file.put("fileName", fileName);
		file.put("filePath", filePath);
		file.put("previewPath", previewPath);
		file.put("downLoadPath", downLoadPath);
		Map<String, Object> owner = new HashMap<String,Object>();
		
		Map<String, Object> user = ThreadLocalUtil.getUser();
		owner.put("NAME", (String)user.get("NAME"));
		owner.put("VALUE", (String)user.get("UUID"));
		file.put("owner", owner);
		file.put("upload_date", Util.format(Util.now()));
		
		Map<String, Object> formalMongo = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		
		List<Map<String, Object>> riskAttachment = (List<Map<String, Object>>) formalMongo.get("riskAttachment");
		if(Util.isNotEmpty(newFile) && newFile){
			//新增附件
			if(Util.isEmpty(riskAttachment)){
				riskAttachment = new ArrayList<Map<String, Object>>();
			}
			riskAttachment.add(file);
			this.saveFileLogs(businessId, (String)user.get("UUID"), (String)user.get("NAME"), UUID, fileName, filePath, "新增风控附件");
		}else{
			//替换附件
			for (Map<String, Object> atta : riskAttachment) {
				if(atta.get("UUID").equals(UUID)){
					atta.put("fileName", fileName);
					atta.put("filePath", filePath);
					atta.put("previewPath", previewPath);
					atta.put("downLoadPath", downLoadPath);
					atta.put("owner", owner);
					atta.put("upload_date", Util.format(Util.now()));
					this.deleteMeetingFile(businessId, UUID);
					this.saveFileLogs(businessId, (String)user.get("UUID"), (String)user.get("NAME"), UUID, fileName, filePath, "替换风控附件");
				}
			}
		}
		
		HashMap<String, Object> data = new HashMap<String,Object>();
		data.put("riskAttachment", riskAttachment);
		this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result deleteAttachment(String json) {
		Result result = new Result();
		Document doc = Document.parse(json);
		String businessId = doc.getString("businessId");
		String itemUUID = doc.getString("itemUUID");
		String fileUUID = doc.getString("fileUUID");
		
		Map<String, Object> user = ThreadLocalUtil.getUser();
		Map<String, Object> formalMongo = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		List<Map<String, Object>> attachment = (List<Map<String, Object>>) formalMongo.get("attachment");
		for (Map<String, Object> file : attachment) {
			String UUID = (String) file.get("UUID");
			if(UUID.equals(itemUUID)){
				List<Map<String, Object>> files = (List<Map<String, Object>>) file.get("files");
				for (Map<String, Object> map : files) {
					String fUUID = (String) map.get("UUID");
					if(fUUID.equals(fileUUID)){
						String version = (String) map.get("version");
						Map<String, Object> owner = (Map<String, Object>) map.get("owner");
						String mVersion = Integer.parseInt(this.getMaxVersion(files))-1+"";
						
						if(!owner.get("VALUE").equals(user.get("UUID"))){
							result.setSuccess(false).setResult_name("您不是此文件的上传人，无法删除此文件！");
							break;
						}else if(!mVersion.equals(version)){
							result.setSuccess(false).setResult_name("此文件不是最高版本，无法删除此文件！");
							break;
						}else{
							files.remove(map);
							//删除会议附件
							this.deleteMeetingFile(businessId, fileUUID);
							String fileName = (String) map.get("fileName");
							String filePath = (String) map.get("filePath");
							this.saveFileLogs(businessId, (String)user.get("UUID"), (String)user.get("NAME"), fileUUID, fileName, filePath, "删除投资部门附件");
							
							Map<String, Object> data = new HashMap<String,Object>();
							data.put("attachment", attachment);
							
							//处理未读标记
							Map<String, Object> fileRemark = (Map<String, Object>) formalMongo.get("fileRemark");
							List<String> grassRoot =  (List<String>) fileRemark.get("grassRoot");
							List<String> legalLeader =  (List<String>) fileRemark.get("legalLeader");
							List<String> investmentManager =  (List<String>) fileRemark.get("investmentManager");
							List<String> reviewLeader =  (List<String>) fileRemark.get("reviewLeader");
							if(Util.isNotEmpty(grassRoot)){
								grassRoot.remove(fileUUID);
							}
							if(Util.isNotEmpty(legalLeader)){
								legalLeader.remove(fileUUID);
							}
							if(Util.isNotEmpty(investmentManager)){
								investmentManager.remove(fileUUID);
							}
							if(Util.isNotEmpty(reviewLeader)){
								reviewLeader.remove(fileUUID);
							}
							data.put("fileRemark", fileRemark);
							this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
							break;
						}
					}
				}
			}
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result deleteRiskAttachment(String json) {
		Result result = new Result();
		Document doc = Document.parse(json);
		String businessId = doc.getString("businessId");
		String uuid = doc.getString("uuid");
		Map<String, Object> user = ThreadLocalUtil.getUser();
		
		Map<String, Object> formalMongo = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		List<Map<String, Object>> riskAttachment = (List<Map<String, Object>>) formalMongo.get("riskAttachment");
		for (Map<String, Object> file : riskAttachment) {
			String fUUID = (String) file.get("UUID");
			if(fUUID.equals(uuid)){
				Map<String, Object> owner = (Map<String, Object>) file.get("owner");
				if(!owner.get("VALUE").equals(user.get("UUID"))){
					result.setSuccess(false).setResult_name("您不是此文件的上传人，无法删除此文件！");
					break;
				}else{
					riskAttachment.remove(file);
					//删除上会附件
					this.deleteMeetingFile(businessId, fUUID);
					String fileName = (String) file.get("fileName");
					String filePath = (String) file.get("filePath");
					this.saveFileLogs(businessId, (String)user.get("UUID"), (String)user.get("NAME"), uuid, fileName, filePath, "删除风控附件");
					Map<String, Object> map = new HashMap<String,Object>();
					map.put("riskAttachment", riskAttachment);
					this.baseMongo.updateSetByObjectId(businessId, map, Constants.RCM_FORMALASSESSMENT_INFO);
					break;
				}
			}
		}
		return result;
	}

	@Override
	public void deleteSign(String json) {
		Document doc = Document.parse(json);
		String businessId = doc.getString("businessId");
		String uuid = doc.getString("uuid");
		String type = doc.getString("type");
		
		Map<String, Object> formalMongo = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		
		Map<String, Object> fileRemark = (Map<String, Object>) formalMongo.get("fileRemark");
		
		if(Util.isEmpty(fileRemark)) return;
		
		if("investmentManager".equals(type)){
			
			List<String> investmentManager = (List<String>) fileRemark.get("investmentManager");
			
			if(Util.isEmpty(investmentManager)) return;
			
			investmentManager.remove(uuid);
			
		}else if("reviewLeader".equals(type)){
			
			List<String> reviewLeader = (List<String>) fileRemark.get("reviewLeader");
			
			if(Util.isEmpty(reviewLeader)) return;
			
			reviewLeader.remove(uuid);
			
		}else if("grassRoot".equals(type)){
			
			List<String> grassRoot = (List<String>) fileRemark.get("grassRoot");
			
			if(Util.isEmpty(grassRoot)) return;
			
			grassRoot.remove(uuid);
			
		}else if("legalLeader".equals(type)){
			
			List<String> legalLeader = (List<String>) fileRemark.get("legalLeader");
			
			if(Util.isEmpty(legalLeader)) return;
			
			legalLeader.remove(uuid);
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		data.put("fileRemark", fileRemark);
		
		this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
		
	}
	
	public void deleteMeetingFile(String businessId,String uuid){
		Map<String, Object> formalMongo = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		List<Map<String, Object>> meetingFiles = (List<Map<String, Object>>) formalMongo.get("meetingFiles");
		
		if(Util.isNotEmpty(meetingFiles)){
			for (Map<String, Object> file : meetingFiles) {
				if(file.get("UUID").toString().equals(uuid)){
					meetingFiles.remove(file);
					break;
				}
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("meetingFiles", meetingFiles);
			this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
		}
		
	}
	@Override
	public void changeAttachment(String json) {
		
		Document doc = Document.parse(json);
		
		String itemUUID = (String) doc.get("itemUUID");
		String businessId = (String) doc.get("businessId");
		String fileName = (String) doc.get("fileName");
		String filePath = (String) doc.get("filePath");
		String previewPath = (String) doc.get("previewPath");
		String downLoadPath = (String) doc.get("downLoadPath");
		String UUID = (String) doc.get("UUID");
		Map<String, Object> fomralOracle = this.getOracleByBusinessId(businessId);
		String stage = (String) fomralOracle.get("STAGE");
		
		String createby = (String) fomralOracle.get("CREATEBY");
		String reviewLeaderId = (String) fomralOracle.get("REVIEWPERSONID");
		String legalLeaderId = (String) fomralOracle.get("LEGALREVIEWPERSONID");
		String grassRootId = (String) fomralOracle.get("GRASSROOTSLEGALPERSONID");
		
		//当前人
		String userId = ThreadLocalUtil.getUserId();
		
		Map<String, Object> formalMongo = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) formalMongo.get("attachment");
		
		Map<String, Object> owner = new HashMap<String,Object>();
		Map<String, Object> user = ThreadLocalUtil.getUser();
		owner.put("NAME", (String)user.get("NAME"));
		owner.put("VALUE", (String)user.get("UUID"));
		ObjectId objectId = new ObjectId();
		for (Map<String, Object> attachment : attachmentList) {
			String uuid = (String) attachment.get("UUID");
			if(uuid.equals(itemUUID)){
				List<Map<String, Object>> files= (List<Map<String, Object>>) attachment.get("files");
				
				if("1".equals(stage) || "2".equals(stage)){
					for (Map<String, Object> file : files) {
						String fileUuid = (String) file.get("UUID");
						if(fileUuid.equals(UUID)){
							//删除上会附件
							this.deleteMeetingFile(businessId, fileUuid);
							
							//删除未读标记
							Map<String, Object> fileRemark = (Map<String, Object>) formalMongo.get("fileRemark");
							if(Util.isNotEmpty(fileRemark)){
								
								List<String> grassRoot =  (List<String>) fileRemark.get("grassRoot");
								List<String> legalLeader =  (List<String>) fileRemark.get("legalLeader");
								List<String> investmentManager =  (List<String>) fileRemark.get("investmentManager");
								List<String> reviewLeader =  (List<String>) fileRemark.get("reviewLeader");
								if(Util.isNotEmpty(grassRoot)){
									grassRoot.remove(fileUuid);
								}
								if(Util.isNotEmpty(legalLeader)){
									legalLeader.remove(fileUuid);
								}
								if(Util.isNotEmpty(investmentManager)){
									investmentManager.remove(fileUuid);
								}
								if(Util.isNotEmpty(reviewLeader)){
									reviewLeader.remove(fileUuid);
								}
								Map<String, Object> data = new HashMap<String,Object>();
								data.put("fileRemark", fileRemark);
								this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
							}
							
							
							file.put("UUID", objectId.toHexString());
							file.put("fileName", fileName);
							file.put("filePath", filePath);
							file.put("previewPath", previewPath);
							file.put("downLoadPath", downLoadPath);
							file.put("upload_date", Util.format(Util.now()));
							file.put("owner", owner);
							file.put("approved", owner);
							file.put("programmed", owner);
							this.saveFileLogs(businessId, (String)user.get("UUID"), (String)user.get("NAME"), UUID, fileName, filePath, "投资部门附件替换，替换源文件");
						}
					}
				}else{
					Map<String, Object> newFile = new HashMap<String,Object>();
					newFile.put("fileName", fileName);
					newFile.put("filePath", filePath);
					newFile.put("upload_date", Util.format(Util.now()));
					newFile.put("version", getMaxVersion(files));
					newFile.put("UUID", objectId.toHexString());
					newFile.put("fileName", fileName);
					newFile.put("filePath", filePath);
					newFile.put("upload_date", Util.format(Util.now()));
					newFile.put("owner", owner);
					newFile.put("approved", owner);
					newFile.put("programmed", owner);
					newFile.put("previewPath", previewPath);
					newFile.put("downLoadPath", downLoadPath);
					files.add(newFile);
					this.saveFileLogs(businessId, (String)user.get("UUID"), (String)user.get("NAME"), UUID, fileName, filePath, "投资部门附件替换，新增版本号");
				}
				
				attachment.put("files", files);
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("attachment", attachmentList);
				this.baseMongo.updateSetByObjectId(businessId, map , Constants.RCM_FORMALASSESSMENT_INFO);
			}
		}
		
		//存未读标记
		Map<String, Object> fileRemark = (Map<String, Object>) formalMongo.get("fileRemark");
		if(Util.isEmpty(fileRemark)){	
			fileRemark = new HashMap<String,Object>();
		}
		//评审负责人温度标记
		List<String> reviewLeader = (List<String>) fileRemark.get("reviewLeader");
		if(Util.isEmpty(reviewLeader)){
			reviewLeader = new ArrayList<String>();
		}
		//投资经理未读标记
		List<String> investmentManager = (List<String>) fileRemark.get("investmentManager");
		if(Util.isEmpty(investmentManager)){
			investmentManager = new ArrayList<String>();
		}
		//法律负责人未读标记
		List<String> legalLeader = (List<String>) fileRemark.get("legalLeader");
		if(Util.isEmpty(legalLeader)){
			legalLeader = new ArrayList<String>();
		}
		//疾风法务未读标记
		List<String> grassRoot = (List<String>) fileRemark.get("grassRoot");
		if(Util.isEmpty(grassRoot)){
			grassRoot = new ArrayList<String>();
		}
		
		if(createby.equals(userId)){
			//当前人是投资经理
			grassRoot.add(objectId.toHexString());
			legalLeader.add(objectId.toHexString());
			reviewLeader.add(objectId.toHexString());
		}else if (Util.isNotEmpty(reviewLeaderId) && reviewLeaderId.equals(userId)){
			//当前人是评审负责人
			grassRoot.add(objectId.toHexString());
			legalLeader.add(objectId.toHexString());
			investmentManager.add(objectId.toHexString());
		}else if (Util.isNotEmpty(grassRootId) && grassRootId.equals(userId)){
			//当前人是基层法务
			legalLeader.add(objectId.toHexString());
			reviewLeader.add(objectId.toHexString());
			investmentManager.add(objectId.toHexString());
		}else if (Util.isNotEmpty(legalLeaderId) && legalLeaderId.equals(userId)){
			//当前人是法律负责人
			grassRoot.add(objectId.toHexString());
			reviewLeader.add(objectId.toHexString());
			investmentManager.add(objectId.toHexString());
		}
		Map<String, Object> data = new HashMap<String,Object>();
		fileRemark.put("investmentManager", investmentManager);
		fileRemark.put("legalLeader", legalLeader);
		fileRemark.put("reviewLeader", reviewLeader);
		fileRemark.put("grassRoot", grassRoot);
		data.put("fileRemark", fileRemark);
		this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
	}
	
	@Override
	public void saveMeetingFiles(String json) {
		Document doc = Document.parse(json);
		String businessId = (String) doc.get("businessId");
		List<Map<String,Object>> meetingFiles = (List<Map<String,Object>>) doc.get("meetingFiles");
		
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("meetingFiles", meetingFiles);
		this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
	}
	
	public void saveFileLogs(String businessId,String userId,String userName,String fileUuid,String fileName,String filePath,String desc){
		Map<String, Object> formalMongo = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		List<Map<String,Object>> fileLogs = (List<Map<String, Object>>) formalMongo.get("fileLogs");
		if(Util.isEmpty(fileLogs)){
			fileLogs = new ArrayList<Map<String,Object>>();
		}
		Map<String, Object> user = new HashMap<String,Object>();
		user.put("NAME", userName);
		user.put("VALUE", userId);
		
		Map<String, Object> log = new HashMap<String,Object>();
		log.put("user", user);
		log.put("UUID", fileUuid);
		log.put("fileName", fileName);
		log.put("filePath", filePath);
		log.put("desc", desc);
		log.put("date", Util.format(Util.now()));
		fileLogs.add(log);
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("fileLogs", fileLogs);
		this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
	}


	@Override
	public void signRead(String json) {
		Document doc = Document.parse(json);
		String businessId = (String) doc.get("businessId");
		List<String> uuids = (List<String>) doc.get("uuids");
		
		Map<String, Object> formalMongo = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		Map<String, Object> fomralOracle = this.getOracleByBusinessId(businessId);
		
		String createby = (String) fomralOracle.get("CREATEBY");
		String reviewLeaderId = (String) fomralOracle.get("REVIEWPERSONID");
		String legalLeaderId = (String) fomralOracle.get("LEGALREVIEWPERSONID");
		String grassRootId = (String) fomralOracle.get("GRASSROOTSLEGALPERSONID");
		String userId = ThreadLocalUtil.getUserId();
		Map<String, Object> fileRemark = (Map<String, Object>) formalMongo.get("fileRemark");
		if(Util.isNotEmpty(fileRemark)){
			if(createby.equals(userId)){
				//当前人是投资经理
				List<String> investmentManager = (List<String>) fileRemark.get("investmentManager");
				if(Util.isNotEmpty(investmentManager)){
					for (String uuid : uuids) {
						investmentManager.remove(uuid);
					}
				}
			}else if (Util.isNotEmpty(reviewLeaderId) && reviewLeaderId.equals(userId)){
				//当前人是评审负责人
				List<String> reviewLeader = (List<String>) fileRemark.get("reviewLeader");
				if(Util.isNotEmpty(reviewLeader)){
					for (String uuid : uuids) {
						reviewLeader.remove(uuid);
					}
				}
			}else if (Util.isNotEmpty(grassRootId) && grassRootId.equals(userId)){
				//当前人是基层法务
				List<String> grassRoot = (List<String>) fileRemark.get("grassRoot");
				if(Util.isNotEmpty(grassRoot)){
					for (String uuid : uuids) {
						grassRoot.remove(uuid);
					}
				}
			}else if (Util.isNotEmpty(legalLeaderId) && legalLeaderId.equals(userId)){
				//当前人是法律负责人
				List<String> legalLeader = (List<String>) fileRemark.get("legalLeader");
				if(Util.isNotEmpty(legalLeader)){
					for (String uuid : uuids) {
						legalLeader.remove(uuid);
					}
				}
			}
		}
		
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("fileRemark", fileRemark);
		this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
	}
	
	@Override
	public List<Map<String, Object>> queryAllByDaxt() {
		return formalAssessmentInfoMapper.queryAllByDaxt();
	}


	@Override
	public PageAssistant queryEnvirByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		
		if(Util.isNotEmpty(page.getParamMap())){
			String needCreateBy = (String) page.getParamMap().get("needCreateBy");
			if(!ThreadLocalUtil.getIsAdmin()){
				//管理员能看所有的
				if(!"0".equals(needCreateBy)){
					params.put("createBy", ThreadLocalUtil.getUserId());
				}
			}
		}
		
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String sql = "'1404' , '1406'";
		params.put("serviceTypeId", sql);
		List<Map<String,Object>> list = this.formalAssessmentInfoMapper.queryEnvirByPage(params);
		//循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, "rcm_formalAssessment_info");
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
		return page;
		
	}


	@Override
	public PageAssistant queryEnvirSubmitedByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String sql = "'1404' , '1406'";
		params.put("serviceTypeId", sql);
		String orderBy = page.getOrderBy();
//		if(orderBy == null){
//			orderBy = " ta.create_date desc ";
//		}
//		params.put("orderBy", orderBy);
		List<Map<String,Object>> list = this.formalAssessmentInfoMapper.queryEnvirSubmitedByPage(params);
		//循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, "rcm_formalAssessment_info");
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
		return page;
	}
}
