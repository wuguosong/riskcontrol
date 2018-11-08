/**
 * 
 */
package com.yk.rcm.noticeofdecision.service.impl;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.DbUtil;
import util.ThreadLocalUtil;
import util.Util;

import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentInfoMapper;
import com.yk.rcm.noticeofdecision.dao.INoticeDecisionInfoMapper;
import com.yk.rcm.noticeofdecision.dao.INoticeDecisionRoleMapper;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionInfoService;

import common.Constants;
import common.PageAssistant;

/**
 * @author 80845530
 *
 */
@Service
@Transactional
public class NoticeDecisionInfoService  implements INoticeDecisionInfoService {
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private INoticeDecisionRoleMapper roleMapper;//need update
	@Resource
	private INoticeDecisionInfoMapper noticeDecisionInfoMapper;
	@Resource
	private IFormalAssessmentInfoMapper formalAssessmentInfoMapper;
	
	private final String DocumentName=Constants.RCM_NOTICEDECISION_INFO;
	
	@Override
	public PageAssistant queryStartByPage(PageAssistant page) {
//		Map<String, Object> map=page.getParamMap();
//		String uid=map.get("userId").toString();
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("user_id", uid);
//		paramMap.put("code", Constants.ROLE_SYSTEM_ADMIN);
//		String o = this.roleMapper.hasRole(paramMap);
//		if("1".equals(o)){
//			map.put("userId", null);
//			page.setParamMap(map);
//		}
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("page", page);
//		if(page.getParamMap()!=null){
//			params.putAll(page.getParamMap());
//		}
		
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
			orderBy = " ta.create_date desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String,Object>> list = this.noticeDecisionInfoMapper.queryStartByPage(params);
		page.setList(list);
		return page;
	}
	
	
	@Override
	public PageAssistant queryOverByPage(PageAssistant page) {
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
			orderBy = " ta.create_date desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String,Object>> list = this.noticeDecisionInfoMapper.queryOverByPage(params);
		page.setList(list);
		return page;
	}


	@Override
	public Document querySaveDefaultInfo(String userId) {
		BasicDBObject query =new BasicDBObject();
		query.put("_id", new ObjectId(userId));
		Document doc =DbUtil.getColl(Constants.RCM_FORMALASSESSMENT_INFO).find(query).first();
		doc.put("_id", doc.get("_id").toString());
		return doc;
	}


	@Override
	public String update(String json) {
		Document doc = Document.parse(json);
		//打包oracle数据
		Map<String, Object> paramsForOracle = this.packageNoticeDecisionInfoForOracle(doc);
		//修改oracle
		this.noticeDecisionInfoMapper.update(paramsForOracle);
		
		BasicDBObject queryAndWhere =new BasicDBObject();
		String id=doc.getString("_id");//赋值id
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		doc.put("_id",new ObjectId(id));
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		DbUtil.getColl(Constants.RCM_NOTICEDECISION_INFO).updateOne(queryAndWhere,updateSetValue);
		return id;
	}


	@Override
	public String delete(String projectId) {
		Map<String, Object> oracleParams = new HashMap<String ,Object>();
		oracleParams.put("businessId", projectId);
		//删除oracle
		this.noticeDecisionInfoMapper.delete(oracleParams);
		//删除mongo
		this.baseMongo.deleteById(projectId, Constants.RCM_NOTICEDECISION_INFO);
		return null;
	}


	@Override
	public String create(String json) {
		//添加信息
			Document doc = Document.parse(json);
			Map<String, Object> paramsForOracle = this.packageNoticeDecisionInfoForOracle(doc);
			//处理mongo的_id
			ObjectId objectId = new ObjectId((String) paramsForOracle.get("businessId"));
			doc.put("_id",objectId);
			//梳理时间戳
//			Date object = (Date) paramsForOracle.get("create_date");
			Format format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			doc.append("currentTimeStamp", format.format(new Date()));
			//处理创建时间
			doc.put("create_date",paramsForOracle.get("create_date"));
			
			//将前台传过来的数据添加到oracle rcm_noticeDecision_info
			this.noticeDecisionInfoMapper.save(paramsForOracle);
			
			//修改oracle的stage状态
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("stage", "7");
			map.put("businessId", doc.getString("projectFormalId").toString());
			this.noticeDecisionInfoMapper.updateFormalAssessmentStage(map);
			
			//添加mongo数据库rcm_noticeDecision_info
			doc.remove("oracle");
			
			baseMongo.save(doc, Constants.RCM_NOTICEDECISION_INFO); 
			return null;
	}
	
	
	/**
	 * 将doc数据封装到map中
	 * @param doc
	 * @return map 
	 */
	public Map<String , Object> packageNoticeDecisionInfoForOracle (Document doc){
		String now = Util.getTime();
		Map<String , Object> paramsForOracle = new HashMap<String , Object>();
		
		if(null==doc.getString("_id") || "".equals(doc.getString("_id"))){
			ObjectId _id = new ObjectId();
			//业务ID
			paramsForOracle.put("businessId", _id.toString());
		}else{
			paramsForOracle.put("businessId", doc.getString("_id"));
		}
		
		
		//合同规模 
		String contractScale =  doc.getString("contractScale");
		paramsForOracle.put("contractScale", contractScale);
		//评审规模
		String evaluationScale =  doc.getString("evaluationScale");
		paramsForOracle.put("evaluationScale", evaluationScale);
		//评审总投资 
		String reviewOfTotalInvestment =  doc.getString("reviewOfTotalInvestment");
		paramsForOracle.put("reviewOfTotalInvestment", reviewOfTotalInvestment);
		//投资决策会议期次 
		String decisionStage =  doc.getString("decisionStage");
		paramsForOracle.put("decisionStage", decisionStage);
		//责任单位 ID
		Document responsibilityUnit = doc.get("responsibilityUnit", Document.class);
		String responsibilityUnitValue=responsibilityUnit.getString("value");
		paramsForOracle.put("responsibilityUnitValue", responsibilityUnitValue);
		
		//创建人id
		Document createBy = doc.get("createBy", Document.class);
		String createById=createBy.getString("value");
		paramsForOracle.put("createBy", createById);
		
		//通知书状态
		String wf_State = "0";
		paramsForOracle.put("wf_State", wf_State);
		//申请时间
		String appyly_date = now;
		paramsForOracle.put("appyly_date", appyly_date);
		//创建时间
		String create_date = now;
		paramsForOracle.put("create_date", create_date);
		//最近修改时间
		String last_update_date = now;
		paramsForOracle.put("last_update_date", last_update_date);
		//项目id
		String projectFormalid =  doc.getString("projectFormalId");
		paramsForOracle.put("projectFormalid", projectFormalid);
		//申报项目ID
		Document reportingUnit = doc.get("reportingUnit", Document.class);
		String reportingUnitId=reportingUnit.getString("value");
		paramsForOracle.put("reportingUnitId", reportingUnitId);
		//会议时间
		String dateOfMeeting =  doc.getString("dateOfMeeting");
		paramsForOracle.put("dateOfMeeting", dateOfMeeting);
		//项目投资决策意见
		String consentToInvestment =  doc.getString("consentToInvestment");
		paramsForOracle.put("consentToInvestment", consentToInvestment);
		
		return paramsForOracle;
	}


	@Override
	public Document queryUpdateInitInfo(String projectId) {
		
		BasicDBObject queryAndWhere =new BasicDBObject();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(projectId));
		Document doc =DbUtil.getColl(Constants.RCM_NOTICEDECISION_INFO).find(queryAndWhere).first();
		doc.put("_id", doc.get("_id").toString());
		return doc;
	}

	@Override
	public void updateAuditStatusByBusinessId(String businessId, String status) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		map.put("wf_state", status);
		this.noticeDecisionInfoMapper.updateAuditStatusByBusinessId(map);
	}


	@Override
	public Map<String, Object> getNoticeDecstionByID(String id) {
		BasicDBObject queryAndWhere =new BasicDBObject();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
//		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		Map<String, Object> doc = baseMongo.queryById(id, DocumentName);
		Map<String,Object> oracle = noticeDecisionInfoMapper.queryById(id);
		doc.put("_id", doc.get("_id").toString());
		doc.put("oracle", oracle);
		return doc;
	}


	@Override
	public void updateAfterStartflow(String businessId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		map.put("apply_date", Util.now());
		map.put("wf_state", "1");
		this.noticeDecisionInfoMapper.updateAfterStartflow(map);
	}
	
	@Override
	public void updateOracleById(Map<String, Object> params) {
		noticeDecisionInfoMapper.update(params);
	}
	@Override
	public void insert(Map<String, Object> params) {
		noticeDecisionInfoMapper.save(params);
	}
	@Override
	public void deleteOracle(String projectId) {
		Map<String, Object> oracleParams = new HashMap<String ,Object>();
		oracleParams.put("businessId", projectId);
		//删除oracle
		this.noticeDecisionInfoMapper.delete(oracleParams);
	}


	@Override
	public Map<String, Object> getNoticeDecstionByBusinessId(String businessId) {
		BasicDBObject queryAndWhere =new BasicDBObject();
		queryAndWhere.put("projectFormalId", businessId);
		List<Map<String, Object>> docs = baseMongo.queryByCondition(queryAndWhere, Constants.RCM_NOTICEDECISION_INFO);
		if(Util.isEmpty(docs)){
			return null;
		}
		return docs.get(0);
	}


	@Override
	public void updateDecisionByBusinessId(String businessId,int decisionResult) {
		BasicDBObject filter = new BasicDBObject();
		filter.put("projectFormalId", businessId);
		Map<String,Object> updateToMongDb = new HashMap<String, Object>();
		updateToMongDb.put("consentToInvestment",String.valueOf(decisionResult));
		baseMongo.updateSetByFilter(filter, updateToMongDb, Constants.RCM_NOTICEDECISION_INFO);
		
		noticeDecisionInfoMapper.updateDecisionByProjectformalId(businessId,decisionResult);
	}
	
}
