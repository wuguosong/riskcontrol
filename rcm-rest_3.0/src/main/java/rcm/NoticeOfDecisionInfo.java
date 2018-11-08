package rcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import common.BaseService;
import common.Constants;
import common.PageAssistant;
import formalAssessment.NoticeOfDecision;
import util.DbUtil;
import util.Util;

public class NoticeOfDecisionInfo extends BaseService {
	
	private final String DocumentNameMeeting=Constants.FORMAL_MEETING_INFO;	
	
	public PageAssistant getAll(String json) {
		PageAssistant assistant = new PageAssistant(json);
		Map<String, Object> map=assistant.getParamMap();
		String uid=map.get("userId").toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_id", uid);
		paramMap.put("code", Constants.ROLE_SYSTEM_ADMIN);
		String o=DbUtil.openSession().selectOne("role.hasRole",paramMap);
		if("1".equals(o)){
			map.put("userId", null);
			assistant.setParamMap(map);
		}
		this.selectByPage(assistant, "noticeOfDecisionInfo.selectAll");
		
		///////////////////////////////
		List<Map<String, Object>> list = (List<Map<String, Object>>) assistant.getList();
		
		MongoCollection<Document> userCollection = DbUtil.getColl(DocumentNameMeeting);
		
		for (Map<String, Object> result : list) {
			String bussinessId = (String) result.get("PROJECTFORMALID");
			if(bussinessId!=null&&bussinessId!=""){
				BasicDBObject queryAndWhere =new BasicDBObject();
				queryAndWhere.put("formalId", bussinessId);
				Document doc = userCollection.find(queryAndWhere).first();
				if(null != doc && null != doc.get("isUrgent")){
					result.put("isUrgent", doc.get("isUrgent").toString());
				}
			}
		}
		
		assistant.setList(list);
		
		
		return assistant;
	}
	
	/**
	 * 保存决策通知书基本信息到oracle中
	 * @param objectId
	 */
	public void saveNoticeOfDecisionBaseInfo2Oracle(String objectId){
		NoticeOfDecision noticeOfDecision = new NoticeOfDecision();
		Document doc = noticeOfDecision.getNoticeOfDecstionByID(objectId);
		//业务ID
		String businessId = doc.get("_id").toString();
		//项目名称
		String projectName = doc.getString("projectName");
		//申请项目名称ID
		String projectFormalId = doc.getString("projectFormalId");
		//申报单位
		String reportingUnit = doc.getString("reportingUnit");
		//合同规模  
		String contractScale = doc.getString("contractScale");
		//评审规模 
		String evaluationScale = doc.getString("evaluationScale");
		//评审总投资 
		String reviewOfTotalInvestment = doc.getString("reviewOfTotalInvestment");
		//投资决策会议期次 
		String decisionStage = doc.getString("decisionStage");
		//会议日期 
		String dateOfMeeting = doc.getString("dateOfMeeting");
		//项目投资决策意见 
		String consentToInvestment = doc.getString("consentToInvestment");
		//注册资本金 
		String registeredCapital = doc.getString("registeredCapital");
		//责任单位 
		Document responsibilityUnit = doc.get("responsibilityUnit", Document.class);
		String responsibilityUnitName=responsibilityUnit.getString("name");
		String responsibilityUnitValue=responsibilityUnit.getString("value");
		//责任人
		List<Document> personLiableList = doc.get("personLiable", ArrayList.class);
		String personLiableIds = getStringFromListDoc(personLiableList, "value");
		String personLiableName = getStringFromListDoc(personLiableList, "name");
		//执行保存操作
		Map<String, Object> insertMap = new HashMap<String, Object>();
		insertMap.put("id", Util.getUUID());
		insertMap.put("businessId", businessId);
		insertMap.put("projectName", projectName);
		insertMap.put("projectFormalId", projectFormalId);
		insertMap.put("reportingUnit", reportingUnit);
		insertMap.put("contractScale", contractScale);
		insertMap.put("evaluationScale", evaluationScale);
		insertMap.put("reviewOfTotalInvestment", reviewOfTotalInvestment);
		insertMap.put("decisionStage", decisionStage);
		insertMap.put("dateOfMeeting", dateOfMeeting);
		insertMap.put("consentToInvestment", consentToInvestment);
		insertMap.put("registeredCapital", registeredCapital);
		insertMap.put("responsibilityUnitName", responsibilityUnitName);
		insertMap.put("responsibilityUnitValue", responsibilityUnitValue);
		insertMap.put("approveState", "");
		insertMap.put("personLiableIds", personLiableIds);
		insertMap.put("personLiableName", personLiableName);
		SqlSession session = DbUtil.openSession(false);
		Map<String, Object> obj = this.selectByBusinessId(businessId);
		String wfState = (String) (obj==null||obj.get("WF_STATE")==null?"0":obj.get("WF_STATE"));
		insertMap.put("wfState", wfState);
		session.delete("noticeOfDecisionInfo.delete", insertMap);
		session.insert("noticeOfDecisionInfo.insert", insertMap);
		DbUtil.close();
	}
	
	/**
	 * 根据业务ID删除RCM_NOTICEOFDECISION_INFO中的一条记录
	 * @param businessId
	 */
	public void deleteByBusinessId(String businessId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		SqlSession session = DbUtil.openSession();
		session.delete("noticeOfDecisionInfo.delete", map);
		DbUtil.close();
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
	
	/**
	 * 更新决策通知书状态 wfState 0起草，1审批中，2审批结束，3终止
	 * or
	 * 更新项目审批结束时间 completeDate
	 * or
	 * 更新项目申请时间 applyDate
	 * or
	 * 评审报告更新时间 reportCreateDate
	 * @param businessId
	 * @param state
	 */
	public void updateNoticeDecisionInfo(String businessId, Map<String, Object> params){
		if(StringUtils.isEmpty(businessId)) return;
		params.put("businessId", businessId);
		SqlSession session = DbUtil.openSession();
		session.update("noticeOfDecisionInfo.updateNoticeOfDecisionInfo", params);
		DbUtil.close();
	}
	
	/*
	 * 根据businessId查询一条记录
	 */
	public Map<String, Object> selectByBusinessId(String businessId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		SqlSession session = DbUtil.openSession();
		List<Map<String, Object>> list = session.selectList("noticeOfDecisionInfo.selectByBusinessId", params);
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(Util.isNotEmpty(list)){
			retMap = list.get(0);
		}
		DbUtil.close();
		return retMap;
	}
}
