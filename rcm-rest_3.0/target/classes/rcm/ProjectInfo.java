/**
 * 
 */
package rcm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.bson.Document;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import common.BaseService;
import common.Constants;
import common.PageAssistant;
import common.Result;
import formalAssessment.ProjectFormalReview;
import projectPreReview.ProjectPreReview;
import util.DbUtil;
import util.Util;

/**
 * @Description: TODO
 * @Author zhangkewei
 * @Date 2016年10月20日 下午1:32:37  
 */
public class ProjectInfo extends BaseService {

	/**
	 * 保存预评审基本信息到oracle中
	 * @param objectId
	 */
	public void saveReviewBaseInfo2Oracle(String objectId, String processType){
		if(StringUtils.isBlank(processType)) return;
		Document doc = null;
		if(processType.equals(Constants.PRE_ASSESSMENT)){
			ProjectPreReview pre = new ProjectPreReview();
			doc = pre.getProjectPreReviewByID(objectId);
		}else if(processType.equals(Constants.FORMAL_ASSESSMENT)){
			ProjectFormalReview formal = new ProjectFormalReview();
			doc = formal.getProjectFormalReviewByID(objectId);
		}
		
		Document apply = doc.get("apply", Document.class);
		//业务ID
		String businessId = doc.get("_id").toString();
		//申报单位
		Document reportingUnit = apply.get("reportingUnit", Document.class);
		String reportingUnitName = reportingUnit.getString("name");
		String reportingUnitId = reportingUnit.getString("value");
		//项目名称
		String projectName = apply.getString("projectName");
		//项目编码
		String projectNo = apply.getString("projectNo");
		//业务类型
		List<Document> serviceType = apply.get("serviceType", ArrayList.class);
		String serviceTypeId = getStringFromListDoc(serviceType, "KEY");
		String serviceTypeName = getStringFromListDoc(serviceType, "VALUE");
		//二级业务类型
		List<Document> projectTypeList = apply.get("projectType", ArrayList.class);
		String projectTypeIds = getStringFromListDoc(projectTypeList, "KEY");
		String projectTypNames = getStringFromListDoc(projectTypeList, "VALUE");
		//大区
		Document pertainArea = apply.get("pertainArea", Document.class);
		String pertainAreaKey = null;
		if(Util.isNotEmpty(pertainArea)){
			pertainAreaKey = pertainArea.getString("KEY");
		}
		//投资模式
		Boolean investmentModel = apply.getBoolean("investmentModel");//PPP和非PPP
		List<Document> projectModelList = apply.get("projectModel", ArrayList.class);
		String projectModelIds = getStringFromListDoc(projectModelList, "KEY");
		String projectModelNames = getStringFromListDoc(projectModelList, "VALUE");
		String is_supplement_review ="0";
		if(null!=doc.get("is_supplement_review") && !"".equals(doc.get("is_supplement_review"))){
			is_supplement_review=doc.get("is_supplement_review").toString();
		}
		String istz ="1";
		if(null!=doc.get("istz") && !"".equals(doc.get("istz"))){
			istz=doc.get("istz").toString();
		}
		//创建时间
		String applyDate = doc.getString("create_date");
		//执行保存操作
		Map<String, Object> insertMap = new HashMap<String, Object>();
		insertMap.put("id", Util.getUUID());
		insertMap.put("businessId", businessId);
		insertMap.put("projectName", projectName);
		insertMap.put("projectNo", projectNo);
		insertMap.put("reportingUnitName", reportingUnitName);
		insertMap.put("reportingUnitId", reportingUnitId);
		insertMap.put("serviceTypeId", serviceTypeId);
		insertMap.put("serviceTypeName", serviceTypeName);
		insertMap.put("projectTypeIds", projectTypeIds);
		insertMap.put("projectTypNames", projectTypNames);
		insertMap.put("investmentModel", investmentModel==null ? Boolean.FALSE:investmentModel);
		insertMap.put("projectModelIds", projectModelIds);
		insertMap.put("projectModelNames", projectModelNames);
		insertMap.put("approveState", "");
		insertMap.put("type", processType);
		insertMap.put("wfState", "0");
		insertMap.put("is_supplement_review", is_supplement_review);
		insertMap.put("istz", istz);
		
		//新增大区
		insertMap.put("pertainAreaId", pertainAreaKey);
		
		Map<String, Object> oldPrj = this.selectByBusinessId(businessId);
		if(Util.isNotEmpty(oldPrj)){
			insertMap.put("wfState", oldPrj.get("WF_STATE"));
		}
		SqlSession session = DbUtil.openSession(false);
		//先查询目前该项目审批状态
		session.delete("projectInfo.delete", insertMap);
		session.insert("projectInfo.insert", insertMap);
		DbUtil.close();
	}
	
	/**
	 * 根据业务ID删除rcm_project_info中的一条记录
	 * @param businessId
	 */
	public void deleteProjectInfoByBusinessId(String businessId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		SqlSession session = DbUtil.openSession();
		session.delete("projectInfo.delete", map);
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
	 * 更新项目状态 wfState 0起草，1审批中，2审批结束，3终止
	 * or
	 * 更新项目审批结束时间 completeDate
	 * or
	 * 更新项目申请时间 applyDate
	 * or
	 * 评审报告更新时间 reportCreateDate
	 * @param businessId
	 * @param state
	 */
	public void updateProjectInfo(String businessId, Map<String, Object> params){
		if(StringUtils.isEmpty(businessId)) return;
		params.put("businessId", businessId);
		SqlSession session = DbUtil.openSession();
		session.update("projectInfo.updateProjectInfo", params);
		DbUtil.close();
	}
	
	public void updateProjectInfo(String json){
		Map<String, Object> params = Util.parseJson2Map(json);
		this.updateProjectInfo((String)params.get("businessId"), params);
	}
	
	//根据申请单ID将评审报告相关字段置空
	public void updateReportInfo2Blank(String businessId){
		SqlSession session = DbUtil.openSession();
		session.update("projectInfo.setReportInfo2Blank", businessId);
		DbUtil.close();
	}
	/*
	 * 根据businessId查询一条记录
	 */
	public Map<String, Object> selectByBusinessId(String businessId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		SqlSession session = DbUtil.openSession();
		List<Map<String, Object>> list = session.selectList("projectInfo.selectByBusinessId", params);
		DbUtil.close();
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(Util.isNotEmpty(list)){
			retMap = list.get(0);
		}
		return retMap;
	}
	
	public Map<String, Object> selectPrjReviewView(String json){
		Map<String, Object> params = Util.parseJson2Map(json);
		SqlSession session = DbUtil.openSession();
		List<Map<String, Object>> list = session.selectList("projectInfo.selectPrjReviewView", params);
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(Util.isNotEmpty(list)){
			retMap = list.get(0);
		}
		DbUtil.close();
		return retMap;
	}
	/**
	 * 根据业务ID绑定的流程实例数量， 校验是否已启动
	 * @param businessId
	 * @return
	 */
	public boolean isAllreadyStartWithBusiness(String businessId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		SqlSession session = DbUtil.openSession();
		Map<String, Object> retMap = session.selectOne("projectInfo.selectProcInstanceCountByBusinessId", params);
		BigDecimal count = (BigDecimal)retMap.get("COUNT");
		if(count.intValue()>0){
			return Boolean.TRUE;
		}
		DbUtil.close();
		return Boolean.FALSE;
	}
	
	/**
	 * 根据task_def_key_和申请单id查询该节点审批意见
	 * @param json {taskDefKey:'' ,businessId:''}
	 * @return
	 */
	public List<Map<String, Object>> getTaskOpinion(String json){
		Map<String, Object> params = Util.parseJson2Map(json);
		SqlSession session = DbUtil.openSession();
		List<Map<String, Object>> list = session.selectList("projectInfo.selectOpinionByTaskDefKeyAndBusinessId", params);
		DbUtil.close();
		return list;
	}
	/**
	 * 需要查询部门需求调用
	 * 根据task_def_key_和申请单id查询该节点审批意见
	 * @param json {taskDefKey:'' ,businessId:''}
	 * @return
	 */
	public Map<String, Object> getTaskOpinionTwo(String json){
		Map<String, Object> params = Util.parseJson2Map(json);
		SqlSession session = DbUtil.openSession();
		Map<String, Object> list = session.selectOne("projectInfo.selectOpinionByTaskDefKeyAndBusinessIdTwo", params);
		DbUtil.close();
		return list;
	}
	/**
	 * 首页汇总评审负责人项目信息
	 * @return
	 */
	public Map<String, Object> getProjectReportWithReviewLeader(){
		SqlSession session = DbUtil.openSession();
		List<Map<String, Object>> list_0710 = session.selectList("projectInfo.selectReviewleaderPrjInfo_0710");
		List<Map<String, Object>> list_0706 = session.selectList("projectInfo.selectReviewleaderPrjInfo_0706");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tzps_0710", list_0710);
		map.put("tzps_0706", list_0706);
		return map;
	}
	
	/*public PageAssistant getProjectReportWithReviewLeader(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "projectInfo.selectReviewleaderPrjInfo");
		return assistant;
	}*/
	
	
	
	/**
	 * 查询评审负责人评审项目列表
	 * @param json
	 * @return
	 */
	public PageAssistant getReviewleaderPrjByPage(String json){
		PageAssistant assistant = new PageAssistant(json);
		super.selectByPage(assistant, "projectInfo.selectReviewleaderPrjList");
		return assistant;
	}
	
	public Map<String, int[]> getMonthlyReports(){
		Map<String, int[]> map = new HashMap<String, int[]>();
		String preAssessment = "preAssessment";
		String formalAssessment ="formalAssessment";
		String bulletinMatters ="bulletinMatters";
		map.put(preAssessment, getMonthlyReportstwo(preAssessment));
		map.put(formalAssessment, getMonthlyReportstwo(formalAssessment));
		map.put(bulletinMatters, getBulletinMattersByMonth(bulletinMatters));
		return map;
	}
	
	private int[] getMonthlyReportstwo(String type){
		int[] monthlyReports = new int[12];
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("type", type);
		List<Map<String, Object>> queryResults = DbUtil.openSession().selectList("projectInfo.countReportsByMonth", paramMap);
		for(Map<String, Object> queryResult : queryResults){
			int month = Integer.valueOf((String)queryResult.get("MONTH")) - 1;
			BigDecimal num = (BigDecimal) queryResult.get("NUM");
			monthlyReports[month] = num.intValue();
		}
		return monthlyReports;
	}
	
	private int[] getBulletinMattersByMonth(String type){
		int[] monthlyReports = new int[12];
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("type", type);
		List<Map<String, Object>> queryResults = DbUtil.openSession().selectList("projectInfo.countBulletinMattersByMonth", paramMap);
		for(Map<String, Object> queryResult : queryResults){
			int month = Integer.valueOf((String)queryResult.get("MONTH")) - 1;
			BigDecimal num = (BigDecimal) queryResult.get("NUM");
			monthlyReports[month] = num.intValue();
		}
		return monthlyReports;
	}
}
