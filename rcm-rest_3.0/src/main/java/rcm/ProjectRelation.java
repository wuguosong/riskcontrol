/**
 * 
 */
package rcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.bson.Document;

import common.BusinessException;
import common.Constants;
import common.commonMethod;
import formalAssessment.NoticeOfDecision;
import formalAssessment.ProjectFormalReview;
import projectPreReview.ProjectPreReview;
import util.DbUtil;
import util.Util;

/**
 * @Description: 保存项目流程人员关系表
 * @Author zhangkewei
 * @Date 2016年10月17日 下午6:56:41  
 */
public class ProjectRelation {
	//人员项目关系字典
	private enum dicRelationType{
		
		investmentManager("0701","investmentManager","投资经理"),
		
		companyHeader("0702","companyHeader","单位负责人"),
		
		grassrootsLegalStaff("0703","grassrootsLegalStaff","基层法务人员"),
		
		investmentPerson("0704","investmentPerson","区域团队负责人"),
		
		directPerson("0705","directPerson","直接负责人"),
		
		reviewLeader("0706","reviewLeader","评审负责人"),
		
		fixedGroup("0707","fixedGroup","评审小组固定成员"),
		
		groupLeader("0708","groupLeader","评审组长"),
		
		decisionMakingCommitteeStaff("0709","decisionMakingCommitteeStaff","投资决策委员会委员"),
		
		legalReviewLeader("0710", "legalReviewLeader", "法律评审负责人"),
		
		legalGroupLeader("0711", "legalGroupLeader", "法律组组长"),
		
		taskAssigneeManager("0712", "taskAssigneeManager", "任务分配人"),
		
		personLiable("0713","personLiable","决策通知书责任人"),
		
		presidentAssistant("0714","presidentAssistant","总裁办"),
		
		fkLeader("0715","fkLeader","风控负责签发领导");
		
		
		private String code;
		private String columnName;
		private String typeName;
		private dicRelationType(String code, String columnName, String typeName){
			this.code = code;
			this.columnName = columnName;
			this.typeName = typeName;
		}
	}
	
	public void insert(Map<String, Object> map){
		SqlSession session = DbUtil.openSession();
		map.put("id", Util.getUUID());
		session.insert("projectRelation.insert", map);
		DbUtil.close();
	}
	
	public void batchInser(List<Map<String, Object>> list){
		if(Util.isEmpty(list)) return;
		//插入前先执行删除操作
		SqlSession session = DbUtil.openSession(false);
		session.delete("projectRelation.delete", list.get(0));
		for(Map<String, Object> map : list){
			map.put("id", Util.getUUID());
			session.insert("projectRelation.insert", map);
		}
		DbUtil.close();
	}

	private String getString(Document doc , String key){
		return doc.containsKey(key) ? doc.getString(key) : null;
	}
	
	/**
	 * 根据业务ID删除rcm_project_relation中的一条记录
	 * @param businessId
	 */
	public void deleteProjectInfoByBusinessId(String businessId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		SqlSession session = DbUtil.openSession();
		session.delete("projectRelation.delete", map);
		DbUtil.close();
	}
	
	/**
	 * @param businessId
	 * @param string
	 * @param processInstanceId
	 */
	public void insertWhenStart(String businessId, String processDefId) {
	 	//查询数据字段获取
		Map<String, Object> templateMap = this.getTemplateMap(businessId);
		Document doc = null;
		if(processDefId.startsWith(Constants.PRE_ASSESSMENT)){
			templateMap.put("type", Constants.PRE_ASSESSMENT);
			//根据businessId查询预评审
			ProjectPreReview p = new ProjectPreReview();
			doc = p.getProjectPreReviewByID(businessId);
		}else if(processDefId.startsWith(Constants.FORMAL_ASSESSMENT)){
			templateMap.put("type", Constants.FORMAL_ASSESSMENT);
			ProjectFormalReview formal = new ProjectFormalReview();
			doc = formal.getProjectFormalReviewByID(businessId);
		}
		if(doc == null) return;
		Document apply = doc.get("apply", Document.class);
		String projectNo = this.getString(apply, "projectNo");
		templateMap.put("projectNo", projectNo);
		
		//apply.investmentManager投资经理
		Document investmentManager = apply.get(dicRelationType.investmentManager.columnName, Document.class);
		List<Map<String, Object>> toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.investmentManager.code, investmentManager);
		this.batchInser(toInsertList);
		
		//apply.companyHeader单位负责人
		Document companyHeader = apply.get(dicRelationType.companyHeader.columnName, Document.class);
		List<Map<String, Object>> toInsertcompanyHeaderList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.companyHeader.code, companyHeader);
		this.batchInser(toInsertcompanyHeaderList);
		/*List<Document> companyHeaderList = apply.get(dicRelationType.companyHeader.columnName, ArrayList.class);
		if(Util.isNotEmpty(companyHeaderList)){
			List<Map<String, Object>> chl = new ArrayList<Map<String, Object>>();
			for(Document ch : companyHeaderList){
				toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.companyHeader.code, ch);
				chl.addAll(toInsertList);
			}
			this.batchInser(chl);
		}*/
		
		//apply.grassrootsLegalStaff基层法务人员
		Document grassrootsLegalStaff = apply.get(dicRelationType.grassrootsLegalStaff.columnName, Document.class);
		toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.grassrootsLegalStaff.code, grassrootsLegalStaff);
		this.batchInser(toInsertList);
		
		//apply.investmentPerson区域团队负责人
		Document investmentPerson = apply.get(dicRelationType.investmentPerson.columnName, Document.class);
		toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.investmentPerson.code, investmentPerson);
		this.batchInser(toInsertList);
		
		//apply.directPerson直接负责人
		Document directPerson = apply.get(dicRelationType.directPerson.columnName, Document.class);
		toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.directPerson.code, directPerson);
		this.batchInser(toInsertList);
		
		//任务分配人
		commonMethod c = new commonMethod();
		List<Map<String, Object>> assigneeManagerList = c.getRoleuserByCode(Constants.TASK_ASSIGNEE_MANAGER);
		if(Util.isNotEmpty(assigneeManagerList)){
			Map<String, Object> am = assigneeManagerList.get(0);
			Document assigneeManager = new Document();
			assigneeManager.put("value", am.get("VALUE"));
			assigneeManager.put("name", am.get("NAME"));
			toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.taskAssigneeManager.code, assigneeManager);
			this.batchInser(toInsertList);
		}
	}

	/**
	 * 保存决策通知书人员关系
	 * */
	public void insertWhenStartNotice(String businessId, String processDefId) {
	 	//查询数据字段获取
		Map<String, Object> templateMap = this.getTemplateMap(businessId);
		Document doc = null;
		
		templateMap.put("type", Constants.FORMAL_NOTICEOFDECSTION);
		//根据businessId查询预评审
		NoticeOfDecision p = new NoticeOfDecision();
		doc = p.getNoticeOfDecstionByID(businessId);
		
		if(doc == null) return;
		String projectName = doc.get("projectName").toString();
		templateMap.put("projectNo", projectName);
		
		List<Map<String, Object>> toInsertList=null;
		//起草人-评审负责人
		Document createBy = doc.get("createBy", Document.class);
		toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.reviewLeader.code, createBy);
		this.batchInser(toInsertList);
		//personLiable决策通知书责任人
		List<Document> companyHeaderList = doc.get(dicRelationType.personLiable.columnName, ArrayList.class);
		if(Util.isNotEmpty(companyHeaderList)){
			List<Map<String, Object>> chl = new ArrayList<Map<String, Object>>();
			for(Document ch : companyHeaderList){
				toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.personLiable.code, ch);
				chl.addAll(toInsertList);
			}
			this.batchInser(chl);
		}
		//风控分管领导-任务分配人（角色5）
		commonMethod c = new commonMethod();
		List<Map<String, Object>> assigneeManagerList = c.getRoleuserByCode(Constants.TASK_ASSIGNEE_MANAGER);
		if(Util.isNotEmpty(assigneeManagerList)){
			Map<String, Object> am = assigneeManagerList.get(0);
			Document assigneeManager = new Document();
			assigneeManager.put("value", am.get("VALUE"));
			assigneeManager.put("name", am.get("NAME"));
			toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.taskAssigneeManager.code, assigneeManager);
			this.batchInser(toInsertList);
		}
		//总裁办-会议管理员（角色3）
		List<Map<String, Object>> managerList = c.getRoleuserByCode(Constants.ROLE_MEETING_ADMIN);
		if(Util.isNotEmpty(managerList)){
			Map<String, Object> am = managerList.get(0);
			Document assigneeManager = new Document();
			assigneeManager.put("value", am.get("VALUE"));
			assigneeManager.put("name", am.get("NAME"));
			toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.presidentAssistant.code, assigneeManager);
			this.batchInser(toInsertList);
		}
		//风控负责签发领导（角色9）
		List<Map<String, Object>> fkLeaderList = c.getRoleuserByCode(Constants.ROLE_FKFGLEADER);
		if(Util.isNotEmpty(fkLeaderList)){
			Map<String, Object> am = fkLeaderList.get(0);
			Document leader = new Document();
			leader.put("value", am.get("VALUE"));
			leader.put("name", am.get("NAME"));
			toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.fkLeader.code, leader);
			this.batchInser(toInsertList);
		}
	}

	/**
	 * @param businessId
	 * @param processDefId
	 * @param processInstanceId
	 * @return
	 */
	private Map<String, Object> getTemplateMap(String businessId) {
		Map<String, Object> templateMap = new HashMap<String, Object>();
		templateMap.put("businessId", businessId);
		return templateMap;
	}

	/**
	 * @param templateMap 
	 * @param string
	 * @param investmentManager
	 * @return
	 */
	private List<Map<String, Object>> getToInsertMapWithTemplateMap(Map<String, Object> templateMap, String relationType, Document person) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(templateMap);
		map.put("relationType", relationType);
		String value = "", name = "";
		if(person != null){
			if(person.containsKey("value")){
				value = this.getString(person, "value");
			}else if(person.containsKey("VALUE")){
				value = this.getString(person, "VALUE");
			}
			
			if(person.containsKey("name")){
				name = this.getString(person, "name");
			}else if(person.containsKey("NAME")){
				name = this.getString(person, "NAME");
			}
		}
		map.put("userId", value);
		map.put("userName", name);
		/*Gson gson = new Gson();
		System.out.println(gson.toJson(map));*/
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(1);
		list.add(map);
		return list;
	}

	/**
	 * @param businessId
	 * @param processDefId
	 * @param processInstanceId
	 */
	public void insertWhenReviewLeaderComplete(String businessId, String processDefId) {
		Map<String, Object> templateMap = this.getTemplateMap(businessId);
		Document doc = null;
		if(processDefId!=null && processDefId.startsWith(Constants.PRE_ASSESSMENT)){
			templateMap.put("type", Constants.PRE_ASSESSMENT);
			//根据businessId查询预评审
			ProjectPreReview p = new ProjectPreReview();
			doc = p.getProjectPreReviewByID(businessId);
		}else if(processDefId!=null && processDefId.startsWith(Constants.FORMAL_ASSESSMENT)){
			templateMap.put("type", Constants.FORMAL_ASSESSMENT);
			ProjectFormalReview formal = new ProjectFormalReview();
			doc = formal.getProjectFormalReviewByID(businessId);
		}
		if(doc == null) return;
		Document apply = doc.get("apply", Document.class);
		String projectNo = this.getString(apply, "projectNo");
		templateMap.put("projectNo", projectNo);
		Document taskallocation = doc.get("taskallocation", Document.class);
		
		//taskallocation.reviewLeader 评审负责人
		Document reviewLeader = taskallocation.get(dicRelationType.reviewLeader.columnName, Document.class);
		List<Map<String, Object>> toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.reviewLeader.code, reviewLeader);
		this.batchInser(toInsertList);
		
		//taskallocation.fixedGroup 评审小组固定成员
		List<Document> fixedGroup = taskallocation.get(dicRelationType.fixedGroup.columnName, ArrayList.class);
		if(Util.isNotEmpty(fixedGroup)){
			List<Map<String, Object>> fgl = new ArrayList<Map<String, Object>>();
			for(Document fi : fixedGroup){
				toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.fixedGroup.code, fi);
				fgl.addAll(toInsertList);
			}
			this.batchInser(fgl);
		}
		
		//评审组长,该人员为评审负责人所属小组的组长
		String reviewLeaderId = reviewLeader.getString("VALUE");
		Pteam team = new Pteam();
		Map<String, String> retMap = team.selectTeamLeaderByMemberId(reviewLeaderId, "1");
		Document teamLeader = new Document();
		teamLeader.put("value", retMap.get("TEAM_LEADER_ID"));
		teamLeader.put("name", retMap.get("TEAM_LEADER"));
		toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.groupLeader.code, teamLeader);
		this.batchInser(toInsertList);
		
		//如果是正式评审
		if(processDefId.startsWith(Constants.FORMAL_ASSESSMENT)){
			//保存法律评审负责人
			Document legalReviewLeader = taskallocation.get(dicRelationType.legalReviewLeader.columnName, Document.class);
			toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.legalReviewLeader.code, legalReviewLeader);
			this.batchInser(toInsertList);
			//保存法律评审组组长
			Map<String, String> lgl = team.selectTeamLeaderByMemberId((String)toInsertList.get(0).get("userId"), "2");
			Document leagalGroupLeader = new Document();
			leagalGroupLeader.put("value", lgl.get("TEAM_LEADER_ID"));
			leagalGroupLeader.put("name", lgl.get("TEAM_LEADER"));
			toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.legalGroupLeader.code, leagalGroupLeader);
			this.batchInser(toInsertList);
		}
	}

	/**
	 * @param businessId
	 * @param processDefId
	 * @param processInstanceId
	 */
	public void insertWhenReportComplete(String businessId, String processDefId) {
		if(StringUtils.isBlank(processDefId)) return;
			Map<String, Object> templateMap = this.getTemplateMap(businessId);
			Document doc = null;
			if(processDefId.startsWith(Constants.PRE_ASSESSMENT)){
				templateMap.put("type", Constants.PRE_ASSESSMENT);
				//根据businessId查询预评审
				ProjectPreReview p = new ProjectPreReview();
				doc = p.getProjectPreReviewByID(businessId);
			}else if(processDefId.startsWith(Constants.FORMAL_ASSESSMENT)){
				templateMap.put("type", Constants.FORMAL_ASSESSMENT);
				ProjectFormalReview formal = new ProjectFormalReview();
				doc = formal.getProjectFormalReviewByID(businessId);
			}
			if(doc == null) return;
			Document apply = doc.get("apply", Document.class);
			String projectNo = this.getString(apply, "projectNo");
			templateMap.put("projectNo", projectNo);
			
			if(processDefId.startsWith(Constants.PRE_ASSESSMENT)){
				List<Document> decisionMakingCommitteeStaff = doc.get(dicRelationType.decisionMakingCommitteeStaff.columnName, ArrayList.class);
				if(Util.isNotEmpty(decisionMakingCommitteeStaff)){
					List<Map<String, Object>> dmList = new ArrayList<Map<String, Object>>();
					for(Document de : decisionMakingCommitteeStaff){
						List<Map<String, Object>> toInsertList = getToInsertMapWithTemplateMap(templateMap, dicRelationType.decisionMakingCommitteeStaff.code, de);
						dmList.addAll(toInsertList);
					}
					this.batchInser(dmList);
				}
			}else if(processDefId.startsWith(Constants.FORMAL_ASSESSMENT)){
				commonMethod com = new commonMethod();
				List<Map<String, Object>> list = com.getRoleuserByCode(Constants.DECISION_MAKING_STAFF);//全部决策委员会成员
				if(list != null){
					List<Map<String, Object>> dmList = new ArrayList<Map<String, Object>>();
					for(Map<String, Object> de : list){
						Map<String, Object> toAddMap = new HashMap<String, Object>();
						toAddMap.putAll(templateMap);
						toAddMap.put("relationType", dicRelationType.decisionMakingCommitteeStaff.code);
						toAddMap.put("userId", de.get("VALUE"));
						toAddMap.put("userName", de.get("NAME"));
						dmList.add(toAddMap);
					}
					this.batchInser(dmList);
				}
			}
	}
	
	//根据业务ID查询相关人员
	public List<Map<String, Object>> findRelationUserByBusinessId(String json){
		Map params = Util.parseJson2Map(json);
		SqlSession session = DbUtil.openSession();
		List<Map<String, Object>> list = session.selectList("projectRelation.selectProjectRelation", params);
		DbUtil.close();
		return list;
	}

}
