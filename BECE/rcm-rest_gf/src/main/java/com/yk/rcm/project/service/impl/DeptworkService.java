/**
 * 
 */
package com.yk.rcm.project.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.mongodb.BasicDBObject;
import com.yk.power.service.IOrgService;
import com.yk.rcm.noticeofdecision.service.INoticeOfDecisionService;
import com.yk.rcm.pre.service.IPreAuditReportService;
import com.yk.rcm.pre.service.IPreInfoService;
import com.yk.rcm.project.dao.IDeptworkMapper;
import com.yk.rcm.project.dao.IPersonnelWorkMapper;
import com.yk.rcm.project.service.IDeptworkService;
import com.yk.rcm.project.service.IFormalAssesmentService;
import com.yk.rcm.project.service.IPreAssementService;

import common.Constants;
import common.PageAssistant;

/**
 * @author Administrator
 */
@Service
@Transactional
public class DeptworkService implements IDeptworkService {
	@Resource
	private IFormalAssesmentService formalAssesmentService; 
	@Resource
	private IPreInfoService preInfoService;
	@Resource
	private IPreAssementService preAssesmentService; 
	@Resource
	private INoticeOfDecisionService noticeOfDecisionService; 
	@Resource
	private IPersonnelWorkMapper personnelWorkMapper; 
	@Resource
	private IDeptworkMapper deptworkMapper; 
	@Resource
	private IPreAuditReportService preAuditReportService;
	@Resource
	private IOrgService orgService;
	
	/* (non-Javadoc)
	 * @see com.yk.rcm.service.IDeptworkService#queryFormalAllViewById(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> queryFormalAllViewById(String businessId) {
		Map<String, Object> map = new  HashMap<String, Object>();
		Map<String, Object> mongoData = this.formalAssesmentService.queryMongoById(businessId);
		Map<String, Object> oracleData = this.formalAssesmentService.queryOracleById(businessId);
		map.put("oracleData", oracleData);
		
		//1、查正式评审基本信息
		map.put("projectInfo", mongoData);
		//2、一级业务单位意见
		String cesuanFileOpinion = (String) mongoData.get("cesuanFileOpinion");
		String tzProtocolOpinion = (String) mongoData.get("tzProtocolOpinion");
		if(cesuanFileOpinion!=null && !"".equals(cesuanFileOpinion) &&
				tzProtocolOpinion!=null && !"".equals(tzProtocolOpinion)){
			Map<String, Object> firstLevelOpinion = new HashMap<String, Object>();
			firstLevelOpinion.put("cesuanFileOpinion", cesuanFileOpinion);
			firstLevelOpinion.put("tzProtocolOpinion", tzProtocolOpinion);
			map.put("firstLevelOpinion", firstLevelOpinion);
		}else{
			map.put("firstLevelOpinion", null);
		}
		//3、风控意见
		//负责人意见
		Map<String, Object> approveAttachment = (Map<String, Object>) mongoData.get("approveAttachment");
		//法律意见
		Map<String, Object> approveLegalAttachment = (Map<String, Object>) mongoData.get("approveLegalAttachment");
		if(approveAttachment!=null && !"".equals(approveAttachment) ||
				approveLegalAttachment!=null && !"".equals(approveLegalAttachment)){
			Map<String, Object> fengkongOpinion = new HashMap<String, Object>();
			fengkongOpinion.put("approveAttachment", approveAttachment);
			fengkongOpinion.put("approveLegalAttachment", approveLegalAttachment);
			map.put("fengkongOpinion", fengkongOpinion);
		}else{
			map.put("fengkongOpinion", null);
		}
		//4、投资评审报告
		Map<String, Object> report = this.formalAssesmentService.queryReportById(businessId);
		// 修改报告判断
		map.put("report", report);
		/*if(report!=null && !"".equals(report)){
			if(report.get("filePath")!=null && !"".equals(report.get("filePath"))){
				map.put("report", report);
			}else{
				map.put("report", null);
			}
		}else{
			map.put("report", null);
		}*/
		//5、投资决策通知书
		Map<String, Object> noticeMongoData = this.noticeOfDecisionService.queryMongoByFormalId(businessId);
		map.put("noticeOfDecisionInfo", noticeMongoData);
		//6、项目经验总结
		Map<String, Object> projectExperience = this.formalAssesmentService.queryProjectExperienceById(businessId);
		map.put("projectExperience", projectExperience);
		
		// ------------------------Sunny Qi 2019-03-21 Start------------------------------
		Map<String, Object> summary = this.formalAssesmentService.querySummaryById(businessId);
		map.put("summary", summary);
		// ------------------------Sunny Qi 2019-03-21 End------------------------------
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> queryPreAllViewById(String businessId) {
		Map<String, Object> map = new  HashMap<String, Object>();
		Map<String, Object> preinfo = preInfoService.getPreInfoByID(businessId);
		Map<String, Object> oracleData = (Map<String, Object>) preinfo.get("oracle");
		Map<String, Object> mongoData = (Map<String, Object>) preinfo.get("mongo");
		
		//1、查正式评审基本信息
		map.put("projectInfo", mongoData);
		map.put("oracleData", oracleData);
		//2、一级业务单位意见
		String cesuanFileOpinion = (String) mongoData.get("cesuanFileOpinion");
		String tzProtocolOpinion = (String) mongoData.get("tzProtocolOpinion");
		
		if(cesuanFileOpinion!=null && !"".equals(cesuanFileOpinion) && tzProtocolOpinion!=null && !"".equals(tzProtocolOpinion)){
			Map<String, Object> firstLevelOpinion = new HashMap<String, Object>();
			firstLevelOpinion.put("cesuanFileOpinion", cesuanFileOpinion);
			firstLevelOpinion.put("tzProtocolOpinion", tzProtocolOpinion);
			map.put("firstLevelOpinion", firstLevelOpinion);
		}else{
			map.put("firstLevelOpinion", null);
		}
		//3、风控意见
		//负责人意见
		Map<String, Object> approveAttachment = (Map<String, Object>) mongoData.get("approveAttachment");
		if(approveAttachment!=null && !"".equals(approveAttachment)){
			Map<String, Object> fengkongOpinion = new HashMap<String, Object>();
			fengkongOpinion.put("approveAttachment", approveAttachment);
			map.put("fengkongOpinion", fengkongOpinion);
		}else{
			map.put("fengkongOpinion", null);
		}
		//4、投资评审报告
		Map<String, Object> report = this.preAuditReportService.getById(businessId);
		if(Util.isNotEmpty(report.get("reviewReport"))){
		    Map<String, Object> reviewReport = (Map<String, Object>) report.get("reviewReport");
		    map.put("report", reviewReport);
		} else {
			map.put("report", null);
		}
		/*if(Util.isNotEmpty(report.get("reviewReport"))){
			Map<String, Object> reviewReport = (Map<String, Object>) report.get("reviewReport");
			if(reviewReport!=null && !"".equals(reviewReport)){
				if(reviewReport.get("filePath")!=null && !"".equals(reviewReport.get("filePath"))){
					map.put("report", reviewReport);
				}else{
					map.put("report", null);
				}
			}else{
				map.put("report", null);
			}
		}else{
			map.put("report", null);
		}*/
		return map;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryProjectAllViewById(String businessId) {
		Map<String, Object> map = new  HashMap<String, Object>();
		Map<String, Object> mongoData = this.preAssesmentService.queryMongoById(businessId);
		Map<String, Object> oracleData = this.preAssesmentService.queryOracleById(businessId);
		//1、查正式评审基本信息
		map.put("projectInfo", mongoData);
		//2、一级业务单位意见
		String cesuanFileOpinion = (String) mongoData.get("cesuanFileOpinion");
		String tzProtocolOpinion = (String) mongoData.get("tzProtocolOpinion");
		if(cesuanFileOpinion!=null && !"".equals(cesuanFileOpinion) &&
			tzProtocolOpinion!=null && !"".equals(tzProtocolOpinion)){
			Map<String, Object> firstLevelOpinion = new HashMap<String, Object>();
			firstLevelOpinion.put("cesuanFileOpinion", cesuanFileOpinion);
			firstLevelOpinion.put("tzProtocolOpinion", tzProtocolOpinion);
			map.put("firstLevelOpinion", firstLevelOpinion);
		}else{
			map.put("firstLevelOpinion", null);
			}
		//3、风控意见
		//负责人意见
		Map<String, Object> approveAttachment = (Map<String, Object>) mongoData.get("approveAttachment");
		map.put("approveAttachment", approveAttachment);
		return map;
	}
	
	@Override
	public void queryFormalGoingList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> formalGoingList = personnelWorkMapper.queryFormalGoingList(params);
		page.setList(formalGoingList);
	}

	@Override
	public void queryFormalDealedList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> formalGoingList = personnelWorkMapper.queryFormalDealedList(params);
		page.setList(formalGoingList);
	}

	@Override
	public void queryPreGoingList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> formalGoingList = personnelWorkMapper.queryPreGoingList(params);
		page.setList(formalGoingList);
	}

	@Override
	public void queryPreDealedList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> formalGoingList = personnelWorkMapper.queryPreDealedList(params);
		page.setList(formalGoingList);
	}

	@Override
	public int countYPSAll() {
		return deptworkMapper.countYPSAll();
	}

	@Override
	public List<Map<String, Object>> queryPsfzrUsers() {
		return deptworkMapper.queryPsfzrUsers();
	}
	
	@Override
	public List<Map<String, Object>> queryFlfzrUsers() {
		return deptworkMapper.queryFlfzrUsers();
	}
	
	@Override
	public List<Map<String, Object>> countByStatus1() {
		return this.deptworkMapper.countByStatus1();
	}

	@Override
	public List<Map<String, Object>> countByStatus2() {
		return this.deptworkMapper.countByStatus2();
	}

	@Override
	public List<Map<String, Integer>> getPreWghGroupAreaReports1401() {
		List<Map<String, Integer>> getPreWghGroupAreaReports = this.deptworkMapper.getPreWghGroupAreaReports1401();
		return getPreWghGroupAreaReports;
	}
	
	@Override
	public List<Map<String, Integer>> getPreWghGroupAreaReports1402() {
		List<Map<String, Integer>> getPreWghGroupAreaReports = this.deptworkMapper.getPreWghGroupAreaReports1402();
		return getPreWghGroupAreaReports;
	}

	@Override
	public List<Map<String, Integer>> getFormalaWghGroupAreaReports1401() {
		List<Map<String, Integer>> getPreWghGroupAreaReports = this.deptworkMapper.getFormalaWghGroupAreaReports1401();
		return getPreWghGroupAreaReports;
	}
	
	@Override
	public List<Map<String, Integer>> getFormalaWghGroupAreaReports1402() {
		List<Map<String, Integer>> getPreWghGroupAreaReports = this.deptworkMapper.getFormalaWghGroupAreaReports1402();
		return getPreWghGroupAreaReports;
	}

	@Override
	public List<Map<String, Object>> getProjectReport0922ByYw() {
		return this.deptworkMapper.getProjectReport0922ByYw();
	}

	@Override
	public List<Map<String, Object>> getProjectReport0922ByFL() {
		return this.deptworkMapper.getProjectReport0922ByFl();
	}
	
	@Override
	public List<Map<String, Object>> getFzrByTeamId(String teamID) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("teamID", teamID);
		return this.deptworkMapper.getFzrByTeamId(params);
	}
	
	@Override
	public List<Map<String, Object>> getFlFzrByTeamId(String teamID) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("teamID", teamID);
		return this.deptworkMapper.getFlFzrByTeamId(params);
	}

	@Override
	public List<Map<String, Integer>> getFormalaAllGroupAreaReports() {
		return this.deptworkMapper.getFormalaAllGroupAreaReports();
	}

	@Override
	public List<Map<String, Integer>> getFormalaWghGroupAreaReports() {
		return this.deptworkMapper.getFormalaWghGroupAreaReports();
	}
	@Override
	public List<Map<String, Integer>> getPreaWghGroupAreaReports() {
		return this.deptworkMapper.getPreaWghGroupAreaReports();
	}

	@Override
	public List<Map<String, Integer>> getFormalaWghReportsByServiceType() {
		return this.deptworkMapper.getFormalaWghReportsByServiceType();
	}
	@Override
	public List<Map<String, Integer>> getPreaWghReportsByServiceType() {
		return this.deptworkMapper.getPreaWghReportsByServiceType();
	}

	/**
	 * 获取未过会项目规模，根据大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getFormalaWghReportsByAreaWithGm() {
		return this.deptworkMapper.getFormalaWghReportsByAreaWithGm();
	}
	/**
	 * 获取未过会投标评审项目规模，根据大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getPreaWghReportsByAreaWithGm() {
		return this.deptworkMapper.getPreaWghReportsByAreaWithGm();
	}

	/**
	 * 获取已过会项目数量，根据大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getFormalaYghGroupAreaReports() {
		return this.deptworkMapper.getFormalaYghGroupAreaReports();
	}
	/**
	 * 获取投标评审已过会项目数量，根据大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getPreaYghGroupAreaReports() {
		return this.deptworkMapper.getPreaYghGroupAreaReports();
	}

	/**
	 * 获取已过会项目规模，根据大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getFormalaYghReportsByAreaWithGm() {
		return this.deptworkMapper.getFormalaYghReportsByAreaWithGm();
	}
	/**
	 * 获取投标评审已过会项目规模，根据大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getPreaYghReportsByAreaWithGm() {
		return this.deptworkMapper.getPreaYghReportsByAreaWithGm();
	}

	/**
	 * 查询未过会的正式评审项目规模，根据业务类型分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getFormalaWghReportsByServiceWithGm() {
		return this.deptworkMapper.getFormalaWghReportsByServiceWithGm();
	}
	/**
	 * 查询未过会的投标评审项目规模，根据业务类型分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getPreaWghReportsByServiceWithGm() {
		return this.deptworkMapper.getPreaWghReportsByServiceWithGm();
	}



	/**
	 * 获取已过会项目数量，根据业务类型分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getFromYghGroupServiceWithNum() {
		return this.deptworkMapper.getFromYghGroupServiceWithNum();
	}
	/**
	 * 获取投标评审已过会项目数量，根据业务类型分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getPreFromYghGroupServiceWithNum() {
		return this.deptworkMapper.getPreFromYghGroupServiceWithNum();
	}

	/**
	 * 获取已过会项目规模，根据业务类型分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getFromYghGroupServiceWithGm() {
		return this.deptworkMapper.getFromYghGroupServiceWithGm();
	}
	/**
	 * 获取投标评审已过会项目规模，根据业务类型分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getPreFromYghGroupServiceWithGm() {
		return this.deptworkMapper.getPreFromYghGroupServiceWithGm();
	}
	
	/**
	 * 查询跟进中的数量 根据业务类型 和 大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getFromWghByServietypeAndAreaWithNum() {
		return this.deptworkMapper.getFromWghByServietypeAndAreaWithNum();
	}
	/**
	 * 查询投标评审跟进中的数量 根据业务类型 和 大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getPreFromWghByServietypeAndAreaWithNum() {
		return this.deptworkMapper.getPreFromWghByServietypeAndAreaWithNum();
	}
	
	/**
	 * 查询已过会的数量 根据业务类型 和 大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getFromYghByServietypeAndAreaWithNum() {
		return this.deptworkMapper.getFromYghByServietypeAndAreaWithNum();
	}
	/**
	 * 查询投标评审已过会的数量 根据业务类型 和 大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getPreFromYghByServietypeAndAreaWithNum() {
		return this.deptworkMapper.getPreFromYghByServietypeAndAreaWithNum();
	}

	/**
	 * 查询跟进中的规模 根据业务类型 和 大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getFromWghByServietypeAndAreaWithGm() {
		return this.deptworkMapper.getFromWghByServietypeAndAreaWithGm();
	}
	/**
	 * 查询投标评审跟进中的规模 根据业务类型 和 大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getPreFromWghByServietypeAndAreaWithGm() {
		return this.deptworkMapper.getPreFromWghByServietypeAndAreaWithGm();
	}

	
	/**
	 * 查询已过会的规模 根据业务类型 和 大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getFromYghByServietypeAndAreaWithGm() {
		return this.deptworkMapper.getFromYghByServietypeAndAreaWithGm();
	}
	/**
	 * 查询投标评审已过会的规模 根据业务类型 和 大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getPreFromYghByServietypeAndAreaWithGm() {
		return this.deptworkMapper.getPreFromYghByServietypeAndAreaWithGm();
	}

	@Override
	public List<Map<String, Object>> queryProjects(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		
		Map<String, Object> paramMap = page.getParamMap();
		String type = (String) paramMap.get("type");
		
		if("team".equals(type)){
			String userId = "";
			if("YW".equals((String)paramMap.get("lx"))){
				List<Map<String, Object>> ywFzrByTeamId = this.getFzrByTeamId((String) paramMap.get("id"));
				for (Map<String, Object> map : ywFzrByTeamId) {
					userId+=",'"+(String)map.get("USERID")+"'";
				}
			}else if("FL".equals((String)paramMap.get("lx"))){
				List<Map<String, Object>> flFzrByTeamId = this.getFlFzrByTeamId((String) paramMap.get("id"));
				for (Map<String, Object> map : flFzrByTeamId) {
					userId+=",'"+(String)map.get("USERID")+"'";
				}
			}
			userId = userId.substring(1);
			paramMap.put("id", userId);
		}else if("user".equals(type)){
			String userId = (String) paramMap.get("id");
			Map<String, Object> queryFLFZR = this.queryFLFZR(userId);
			Map<String, Object> queryYWFZR = this.queryYWFZR(userId);
//			if(Util.isNotEmpty(queryFLFZR)){
//				paramMap.put("lx", "FL");
//			}else if(Util.isNotEmpty(queryYWFZR)){
//				paramMap.put("lx", "YW");
//			}
			
				paramMap.put("lx", (String)paramMap.get("lx"));
			paramMap.put("id", "'"+userId+"'");
		}
		
		
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		return this.deptworkMapper.queryProjects(params);
	}
	
	public Map<String,Object> queryFLFZR(String userId){
		Map<java.lang.String, Object> data = new HashMap<String,Object>();
		data.put("userId", userId);
		return this.deptworkMapper.queryFLFZR(data);
	}
	
	public Map<String,Object> queryYWFZR(String userId){
		Map<java.lang.String, Object> data = new HashMap<String,Object>();
		data.put("userId", userId);
		return this.deptworkMapper.queryYWFZR(data);
	}

	@Override
	public int countTzjcAll() {
		return this.deptworkMapper.countTzjcAll();
	}

	@Override
	public int countTzjcWxsh() {
		return this.deptworkMapper.countTzjcWxsh();
	}

	@Override
	public int countTzjcXsh() {
		return this.deptworkMapper.countTzjcXsh();
	}

	@Override
	public List<Map<String, Object>> getBulletinTypeReport(String year) {
		Map<String, Object> map = new HashMap<String,Object>();
		if(Util.isEmpty(year)){
			int y = Util.now().getYear()+1900;
			map.put("year", y);
		}else{
			map.put("year", year);
		}
		return this.deptworkMapper.getBulletinTypeReport(map);
	}

	@Override
	public List<Map<String, Object>> getBulletinFromWghByServietypeAndAreaWithNum() {
		return deptworkMapper.getBulletinFromWghByServietypeAndAreaWithNum();
	}

	@Override
	public List<Map<String, Integer>> getBulletinWghGroupAreaReports() {
		return deptworkMapper.getBulletinWghGroupAreaReports();
	}

	@Override
	public List<Map<String, Object>> getBulletinFromYghByServietypeAndAreaWithNum() {
		return deptworkMapper.getBulletinFromYghByServietypeAndAreaWithNum();
	}

	@Override
	public List<Map<String, Integer>> getBulletinYghGroupAreaReports() {
		return deptworkMapper.getBulletinYghGroupAreaReports();
	}
	@Override
	public Map<String, Object> getNoticeTYCount() {
		Map<String,Object> resultData = new HashMap<String,Object>();
		List<Map<String,Object>> noticeTYCount = deptworkMapper.getNoticeTYCount();
		List<String> areaList = new ArrayList<String>();
		List<String> typeList = new ArrayList<String>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : noticeTYCount) {
			String areaName = map.get("PERTAINAREANAME").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String type = (String) map.get("TYPE");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();
			
			if(!areaList.contains(areaName)){
				areaList.add(areaName);
			}
			if(!typeList.contains(type)){
				typeList.add(type);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", type);
				everyOne.put("data", new HashMap<String,Object>());
				everyOne.put("type", "bar");
				items.put(itemCode, everyOne);
			}
			
			Map<String,Object> nums = (Map<String,Object>) items.get(itemCode).get("data");
			HashMap<String, Object> area = new HashMap<String,Object>();
			nums.put(areaName, value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", typeList);
		resultData.put("areaList", areaList);
		resultData.put("dataList", dataList);
		List<Map<String, Integer>> totalList = this.getNoticedTYGroupAreaReports();
		resultData.put("totalList", totalList);
		return resultData;
	}
	/**
	 * 获取同意投资项目数量，根据大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getNoticedTYGroupAreaReports() {
		return this.deptworkMapper.getNoticedTYGroupAreaReports();
	}
	
	
	@Override
	public Map<String, Object> getNoticeBTYCount() {
		Map<String,Object> resultData = new HashMap<String,Object>();
		List<Map<String,Object>> fromWghByServietypeAndAreaWithNum = deptworkMapper.getNoticeBTYCount();
		List<String> areaList = new ArrayList<String>();
		List<String> typeList = new ArrayList<String>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : fromWghByServietypeAndAreaWithNum) {
			String areaName = map.get("PERTAINAREANAME").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String type = (String) map.get("TYPE");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();
			
			if(!areaList.contains(areaName)){
				areaList.add(areaName);
			}
			if(!typeList.contains(type)){
				typeList.add(type);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", type);
				everyOne.put("data", new HashMap<String,Object>());
				everyOne.put("type", "bar");
				items.put(itemCode, everyOne);
			}
			
			Map<String,Object> nums = (Map<String,Object>) items.get(itemCode).get("data");
			HashMap<String, Object> area = new HashMap<String,Object>();
			nums.put(areaName, value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", typeList);
		resultData.put("areaList", areaList);
		resultData.put("dataList", dataList);
		List<Map<String, Integer>> totalList = this.getNoticedBTYGroupAreaReports();
		resultData.put("totalList", totalList);
		return resultData;
	}

	/**
	 * 获取不同意投资项目数量，根据大区分组
	 * @return
	 */
	@Override
	public List<Map<String, Integer>> getNoticedBTYGroupAreaReports() {
		return this.deptworkMapper.getNoticedBTYGroupAreaReports();
	}

	@Override
	public List<Map<String, Object>> queryNoticedPertainArea() {
		List<Map<String, Object>> list = this.deptworkMapper.queryAllNoticed();
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
	public List<Map<String, Object>> queryNoticedType() {
		return this.deptworkMapper.queryNoticedType();
	}

	@Override
	public List<Map<String, Object>> queryNoticedCount(String result,String pertainAreaId, String serviceTypeId,String year) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("result", result);
		if(Util.isEmpty(year)){
			int y = Util.now().getYear()+1900;
			map.put("year", y);
		}else{
			map.put("year", year);
		}
		
		if(Util.isNotEmpty(pertainAreaId)){
			//处理大区id
			String[] ids = pertainAreaId.split(",");
			pertainAreaId = "";
			for (String string : ids) {
				pertainAreaId +=",'"+string + "'";
			}
			pertainAreaId = pertainAreaId.substring(1);
		}
		map.put("pertainAreaId", pertainAreaId);
		//处理serviceTypeId
		if(Util.isNotEmpty(serviceTypeId)){
			String[] sids = serviceTypeId.split(",");
			serviceTypeId = "";
			for (String string : sids) {
				serviceTypeId +=",'"+string + "'";
			}
			serviceTypeId = serviceTypeId.substring(1);
		}
		map.put("serviceTypeId", serviceTypeId);
		return this.deptworkMapper.queryNoticedCount(map);
	}
	
}

