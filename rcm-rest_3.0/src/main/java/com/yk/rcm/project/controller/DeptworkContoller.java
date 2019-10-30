package com.yk.rcm.project.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import util.DbUtil;
import util.Util;

import com.yk.power.service.IOrgService;
import com.yk.power.service.impl.OrgService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import com.yk.rcm.decision.serevice.IDecisionReviewService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.pre.service.IPreInfoService;
import com.yk.rcm.project.service.IDeptworkService;

import common.PageAssistant;
import common.Result;

/**
 * @author 80845530
 *
 */
@Controller
@RequestMapping("/deptwork")
public class DeptworkContoller {

	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	@Resource
	private IPreInfoService preInfoService;
	@Resource
	private IDecisionReviewService decisionReviewService;

	@Resource
	private IDeptworkService deptworkService;
	@Resource
	private IOrgService orgService;

	@Resource
	private IBulletinInfoService bulletinInfoService;

	@RequestMapping("/getBulletinTypeReport")
	@ResponseBody
	public Result getBulletinTypeReport(HttpServletRequest request) {
		Result result = new Result();
		String year = request.getParameter("year");
		List<Map<String, Object>> list = this.deptworkService.getBulletinTypeReport(year);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		if (Util.isEmpty(year)) {
			int y = Util.now().getYear() + 1900;
			map.put("year", y);
		} else {
			map.put("year", year);
		}
		result.setResult_data(map);
		return result;
	}

	@RequestMapping("/queryFormalCount")
	@ResponseBody
	public Result queryFormalCount(HttpServletRequest request) {
		Result result = new Result();

		String wf_state = request.getParameter("wf_state");
		String stage = request.getParameter("stage");
		String noticeResult = request.getParameter("result");
		String pertainAreaId = request.getParameter("pertainAreaId");
		String serviceTypeId = request.getParameter("serviceTypeId");
		String year = request.getParameter("year");

		List<Map<String, Object>> list = this.formalAssessmentInfoService.queryFormalCount(wf_state, stage,
				noticeResult, pertainAreaId, serviceTypeId, year);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		if (Util.isEmpty(year)) {
			int y = Util.now().getYear() + 1900;
			map.put("year", y);
		} else {
			map.put("year", year);
		}
		result.setResult_data(map);
		return result;
	}

	@RequestMapping("/queryNoticeCount")
	@ResponseBody
	public Result queryNoticeCount(HttpServletRequest request) {
		Result result = new Result();

		String resultt = request.getParameter("result");
		String pertainAreaId = request.getParameter("pertainAreaId");
		String serviceTypeId = request.getParameter("serviceTypeId");
		String year = request.getParameter("year");

		List<Map<String, Object>> list = this.deptworkService.queryNoticedCount(resultt, pertainAreaId, serviceTypeId,
				year);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		if (Util.isEmpty(year)) {
			int y = Util.now().getYear() + 1900;
			map.put("year", y);
		} else {
			map.put("year", year);
		}
		result.setResult_data(map);
		return result;
	}

	@RequestMapping("/queryPreCount")
	@ResponseBody
	public Result queryPreCount(HttpServletRequest request) {
		Result result = new Result();

		String wf_state = request.getParameter("wf_state");
		String stage = request.getParameter("stage");
		String pertainAreaId = request.getParameter("pertainAreaId");
		String serviceTypeId = request.getParameter("serviceTypeId");
		String year = request.getParameter("year");

		List<Map<String, Object>> list = this.preInfoService.queryPreCount(wf_state, stage, pertainAreaId,
				serviceTypeId, year);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		if (Util.isEmpty(year)) {
			int y = Util.now().getYear() + 1900;
			map.put("year", y);
		} else {
			map.put("year", year);
		}
		result.setResult_data(map);
		return result;
	}

	@RequestMapping("/queryFormalPertainArea")
	@ResponseBody
	public Result queryFormalPertainArea(HttpServletRequest request) {
		Result result = new Result();
		List<Map<String, Object>> list = this.formalAssessmentInfoService.queryFormalPertainArea();
		result.setResult_data(list);
		return result;
	}

	@RequestMapping("/queryPrePertainArea")
	@ResponseBody
	public Result queryPrePertainArea(HttpServletRequest request) {
		Result result = new Result();
		List<Map<String, Object>> list = this.preInfoService.queryPrePertainArea();
		result.setResult_data(list);
		return result;
	}

	@RequestMapping("/queryNoticedPertainArea")
	@ResponseBody
	public Result queryNoticedPertainArea(HttpServletRequest request) {
		Result result = new Result();
		List<Map<String, Object>> list = this.deptworkService.queryNoticedPertainArea();
		result.setResult_data(list);
		return result;
	}

	@RequestMapping("/queryNoticedType")
	@ResponseBody
	public Result queryNoticedType(HttpServletRequest request) {
		Result result = new Result();
		List<Map<String, Object>> list = this.deptworkService.queryNoticedType();
		result.setResult_data(list);
		return result;
	}

	@RequestMapping("/queryProjects")
	@ResponseBody
	public Result queryProjects(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		List<Map<String, Object>> list = this.deptworkService.queryProjects(page);
		page.setList(list);
		result.setResult_data(page);
		return result;
	}

	@RequestMapping("/queryFormalAllViewById")
	@ResponseBody
	public Result queryFormalAllViewById(HttpServletRequest request) {
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		Map<String, Object> data = this.deptworkService.queryFormalAllViewById(businessId);
		result.setResult_data(data);
		return result;
	}

	@RequestMapping("/queryPreAllViewById")
	@ResponseBody
	public Result queryPreAllViewById(HttpServletRequest request) {
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		Map<String, Object> data = this.deptworkService.queryPreAllViewById(businessId);
		result.setResult_data(data);
		return result;
	}

	@RequestMapping("/queryProjectAllViewById")
	@ResponseBody
	public Result queryProjectAllViewById(HttpServletRequest request) {
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		Map<String, Object> data = this.deptworkService.queryProjectAllViewById(businessId);
		result.setResult_data(data);
		return result;
	}

	// 正式评审跟进中
	@RequestMapping("/queryFormalGoingList")
	@ResponseBody
	public Result queryFormalGoingList(HttpServletRequest request) {
		PageAssistant page = new PageAssistant(request.getParameter("json"));
		Result result = new Result();
		this.deptworkService.queryFormalGoingList(page);
		result.setResult_data(page);
		return result;
	}

	// 正式评审已上会
	@RequestMapping("/queryFormalDealedList")
	@ResponseBody
	public Result queryFormalDealedList(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("json"));
		this.deptworkService.queryFormalDealedList(page);
		result.setResult_data(page);
		return result;
	}

	// 预评审跟进中
	@RequestMapping("/queryPreGoingList")
	@ResponseBody
	public Result queryPreGoingList(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("json"));
		this.deptworkService.queryPreGoingList(page);
		result.setResult_data(page);
		return result;
	}

	// 预评审已完成
	@RequestMapping("/queryPreDealedList")
	@ResponseBody
	public Result queryPreDealedList(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("json"));
		this.deptworkService.queryPreDealedList(page);
		result.setResult_data(page);
		return result;
	}

	/**
	 * 查询所有评审项目
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAllFormalAssessmentInfoByPage")
	@ResponseBody
	public Result queryAllFormalAssessmentInfoByPage(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		formalAssessmentInfoService.queryAllInfoByPage(page);
		result.setResult_data(page);
		return result;
	}

	/**
	 * 查询所有投标评审项目
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAllPreInfoByPage")
	@ResponseBody
	public Result queryAllPreInfoByPage(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		preInfoService.queryAllInfoByPage(page);
		result.setResult_data(page);
		return result;
	}

	/**
	 * 初始化信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/initializeCount")
	@ResponseBody
	public Result initializeCount() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();

		// 获取申请中适量
		List<Map<String, Object>> formalApplyingList = formalAssessmentInfoService.queryByStageAndstate("1,2", "0,1");
		List<Map<String, Object>> preApplyingList = preInfoService.queryByStageAndstate("1,2", "0,1");
		List<Map<String, Object>> bulletinApplyingList = bulletinInfoService.queryByStageAndstate("1,1.5", "0,1");
		// 获取已终止项目
		List<Map<String, Object>> formalEndList = formalAssessmentInfoService.queryByStageAndstate("1", "3");
		List<Map<String, Object>> preEndList = preInfoService.queryByStageAndstate("1", "3");
		List<Map<String, Object>> bulletinEndList = bulletinInfoService.queryByStageAndstate("1", "3");
		resultData.put("formalApplyingCount", formalApplyingList.size());
		resultData.put("preApplyingCount", preApplyingList.size());
		resultData.put("bulletinApplyingCount", bulletinApplyingList.size());
		resultData.put("formalEndCount", formalEndList.size());
		resultData.put("preEndCount", preEndList.size());
		resultData.put("bulletinEndCount", bulletinEndList.size());
		// 获取所有投标评审(预评审)的数量
		int ypsCount = preInfoService.countAll();
		resultData.put("ypsCount", ypsCount);

		// 获取所有正式评审的数量
		int zspsCount = formalAssessmentInfoService.countAll();
		resultData.put("zspsCount", zspsCount);

		//// 获取所有其他需决策事项的数量
		int qtjcCount = bulletinInfoService.countAll();
		resultData.put("qtjcCount", qtjcCount);

		List<Map<String, Object>> countByStatus1 = this.deptworkService.countByStatus1();
		for (Map<String, Object> map : countByStatus1) {
			Object object = map.get("'FROMNAME'");
			String fromName = object.toString();
			if ("fromName".equals(fromName)) {
				String zsswghCount = map.get("COUNT(1)").toString();
				resultData.put("zsswghCount", zsswghCount);
			}
			if ("pre".equals(fromName)) {
				String ypswghCount = map.get("COUNT(1)").toString();
				resultData.put("ypswghCount", ypswghCount);
			}
			if ("bulletin".equals(fromName)) {
				String qtjcwghCount = map.get("COUNT(1)").toString();
				resultData.put("qtjcwghCount", qtjcwghCount);
			}
		}

		List<Map<String, Object>> countByStatus2 = this.deptworkService.countByStatus2();
		for (Map<String, Object> map : countByStatus2) {
			Object object = map.get("'FROMNAME'");
			String fromName = object.toString();
			if ("fromName".equals(fromName)) {
				String zssyghCount = map.get("COUNT(1)").toString();
				resultData.put("zssyghCount", zssyghCount);
			}
			if ("pre".equals(fromName)) {
				String ypsyghCount = map.get("COUNT(1)").toString();
				resultData.put("ypsyghCount", ypsyghCount);
			}
			if ("bulletin".equals(fromName)) {
				String qtjcyghCount = map.get("COUNT(1)").toString();
				resultData.put("qtjcyghCount", qtjcyghCount);
			}
		}

		// 获取所有投资决策的数量
		int tzjcCount = this.deptworkService.countTzjcAll();
		resultData.put("tzjcCount", tzjcCount);

		// 获取所有投资决策需上会的数量
		int tzjcWxshCount = this.deptworkService.countTzjcWxsh();
		resultData.put("tzjcWxshCount", tzjcWxshCount);

		// 获取所有投资决策无需上会的数量
		int tzjcXshCount = this.deptworkService.countTzjcXsh();
		resultData.put("tzjcXshCount", tzjcXshCount);

		result.setResult_data(resultData);
		return result;
	}

	/**
	 * 统计所有评审项目
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getProjectReport")
	@ResponseBody
	public Result getProjectReport() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();

		List<Map<String, Object>> getProjectReport0922ByYw = deptworkService.getProjectReport0922ByYw();
		resultData.put("getProjectReport0922ByYw", getProjectReport0922ByYw);

		List<Map<String, Object>> getProjectReport0922ByFL = deptworkService.getProjectReport0922ByFL();
		resultData.put("getProjectReport0922ByFL", getProjectReport0922ByFL);

		List<Map<String, Object>> queryPsfzrUsers = deptworkService.queryPsfzrUsers();
		resultData.put("queryPsfzrUsers", queryPsfzrUsers);

		List<Map<String, Object>> queryFlfzrUsers = deptworkService.queryFlfzrUsers();
		resultData.put("queryFlfzrUsers", queryFlfzrUsers);
		result.setResult_data(resultData);
		return result;
	}

	/**
	 * 项目报表详情： 投资评审-评审负责人
	 * 
	 * @param type
	 * @param json
	 * @return
	 */
	@RequestMapping("/getProjectReportDetails0706")
	@ResponseBody
	public Result getProjectReportDetails0706(String type, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Map<String, Object> paramMap = page.getParamMap();
		String rType = (String) paramMap.get("type");
		if ("formalAssessment".equals(rType)) {
			formalAssessmentInfoService.getProjectReportDetails0706(page);
		} else if ("preAssessment".equals(rType)) {
			preInfoService.getProjectReportDetails0706(page);
		}
		result.setResult_data(page);
		return result;
	}

	/**
	 * 项目报表详情： 投资评审-法律评审负责人
	 * 
	 * @param type
	 * @param json
	 * @return
	 */
	@RequestMapping("/getProjectReportDetails0710")
	@ResponseBody
	public Result getProjectReportDetails0710(String type, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		formalAssessmentInfoService.getProjectReportDetails0710(page);
		result.setResult_data(page);
		return result;
	}

	@RequestMapping("/getPreWghGroupAreaReports")
	@ResponseBody
	public Result getPreWghGroupAreaReports() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Integer>> data1401 = deptworkService.getPreWghGroupAreaReports1401();
		resultData.put("data1401", data1401);

		List<Map<String, Integer>> data1402 = deptworkService.getPreWghGroupAreaReports1402();
		resultData.put("data1402", data1402);
		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	@RequestMapping("/getFromWghGroupAreaReports")
	@ResponseBody
	public Result getFromWghGroupAreaReports() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Integer>> data1401 = deptworkService.getFormalaWghGroupAreaReports1401();
		resultData.put("data1401", data1401);

		List<Map<String, Integer>> data1402 = deptworkService.getFormalaWghGroupAreaReports1402();
		resultData.put("data1402", data1402);
		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	@RequestMapping("/showFzr")
	@ResponseBody
	public Result showFzr(HttpServletRequest request) {
		String teamID = request.getParameter("uuid");
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> data = deptworkService.getFzrByTeamId(teamID);
		resultData.put("data", data);
		result.setResult_code("S");
		result.setResult_data(resultData);
		return result;
	}

	@RequestMapping("/showFlFzr")
	@ResponseBody
	public Result showFlFzr(HttpServletRequest request) {
		String teamID = request.getParameter("uuid");
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> data = deptworkService.getFlFzrByTeamId(teamID);
		resultData.put("data", data);
		result.setResult_code("S");
		result.setResult_data(resultData);
		return result;
	}

	@RequestMapping("/getFromWghGroupArea")
	@ResponseBody
	public Result getFromWghGroupArea() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Integer>> FormalaWghGroupAreaReports = deptworkService.getFormalaWghGroupAreaReports();
		resultData.put("FormalaWghGroupAreaReports", FormalaWghGroupAreaReports);
		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	@RequestMapping("/getPreFromWghGroupArea")
	@ResponseBody
	public Result getPreFromWghGroupArea() {
		Result result = new Result();
		List<Map<String, Integer>> preaWghGroupAreaReports = deptworkService.getPreaWghGroupAreaReports();
		result.setResult_data(preaWghGroupAreaReports);
		return result;
	}

	@RequestMapping("/getFromYghGroupArea")
	@ResponseBody
	public Result getFromYghGroupArea() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Integer>> FormalaYghGroupAreaReports = deptworkService.getFormalaYghGroupAreaReports();
		resultData.put("FormalaYghGroupAreaReports", FormalaYghGroupAreaReports);
		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	@RequestMapping("/getPreFromYghGroupArea")
	@ResponseBody
	public Result getPreFromYghGroupArea() {
		Result result = new Result();
		List<Map<String, Integer>> FormalaYghGroupAreaReports = deptworkService.getPreaYghGroupAreaReports();
		result.setResult_data(FormalaYghGroupAreaReports);
		return result;
	}

	@RequestMapping("/getFromWghByServietype")
	@ResponseBody
	public Result getFromWghByServietype() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Integer>> FormalaWghReportsByServiceType = deptworkService.getFormalaWghReportsByServiceType();
		resultData.put("FormalaWghReportsByServiceType", FormalaWghReportsByServiceType);
		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	@RequestMapping("/getPreFromWghByServietype")
	@ResponseBody
	public Result getPreFromWghByServietype() {
		Result result = new Result();
		List<Map<String, Integer>> preaWghReportsByServiceType = deptworkService.getPreaWghReportsByServiceType();
		result.setResult_data(preaWghReportsByServiceType);
		return result;
	}

	/**
	 * 获取未过会项目规模，根据大区分组
	 * 
	 * @return
	 */
	@RequestMapping("/getFromWghGroupAreaWithGm")
	@ResponseBody
	public Result getFromWghGroupAreaWithGm() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Integer>> formalaWghReportsByAreaWithGm = deptworkService.getFormalaWghReportsByAreaWithGm();
		resultData.put("formalaWghReportsByAreaWithGm", formalaWghReportsByAreaWithGm);
		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	/**
	 * 获取未过会项目规模投标评审，根据大区分组
	 * 
	 * @return
	 */
	@RequestMapping("/getPreFromWghGroupAreaWithGm")
	@ResponseBody
	public Result getPreFromWghGroupAreaWithGm() {
		Result result = new Result();
		List<Map<String, Integer>> preaWghReportsByAreaWithGm = deptworkService.getPreaWghReportsByAreaWithGm();
		result.setResult_data(preaWghReportsByAreaWithGm);
		return result;
	}

	/**
	 * 获取未过会项目规模，根据业务类型分组
	 * 
	 * @return
	 */
	@RequestMapping("/getFromWghByServietypeWithGm")
	@ResponseBody
	public Result getFromWghByServietypeWithGm() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Integer>> formalaWghReportsByServiceWithGm = deptworkService
				.getFormalaWghReportsByServiceWithGm();
		resultData.put("formalaWghReportsByServiceWithGm", formalaWghReportsByServiceWithGm);
		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	/**
	 * 获取未过会项目规模，根据业务类型分组
	 * 
	 * @return
	 */
	@RequestMapping("/getPreFromWghByServietypeWithGm")
	@ResponseBody
	public Result getPreFromWghByServietypeWithGm() {
		Result result = new Result();
		List<Map<String, Integer>> preaWghReportsByServiceWithGm = deptworkService.getPreaWghReportsByServiceWithGm();
		result.setResult_data(preaWghReportsByServiceWithGm);
		return result;
	}

	/**
	 * 获取已过会项目规模，根据大区分组
	 * 
	 * @return
	 */
	@RequestMapping("/getFromYghGroupAreaWithGm")
	@ResponseBody
	public Result getFromYghGroupAreaWithGm() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Integer>> formalaYghReportsByAreaWithGm = deptworkService.getFormalaYghReportsByAreaWithGm();
		resultData.put("formalaYghReportsByAreaWithGm", formalaYghReportsByAreaWithGm);
		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	/**
	 * 获取投标评审已过会项目规模，根据大区分组
	 * 
	 * @return
	 */
	@RequestMapping("/getPreFromYghGroupAreaWithGm")
	@ResponseBody
	public Result getPreFromYghGroupAreaWithGm() {
		Result result = new Result();
		List<Map<String, Integer>> formalaYghReportsByAreaWithGm = deptworkService.getPreaYghReportsByAreaWithGm();
		result.setResult_data(formalaYghReportsByAreaWithGm);
		return result;
	}

	/**
	 * 获取已过会项目数量，根据业务类型分组
	 * 
	 * @return
	 */
	@RequestMapping("/getFromYghGroupServiceWithNum")
	@ResponseBody
	public Result getFromYghGroupServiceWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Integer>> fromYghGroupServiceWithNum = deptworkService.getFromYghGroupServiceWithNum();
		resultData.put("fromYghGroupServiceWithNum", fromYghGroupServiceWithNum);
		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	/**
	 * 获取投标评审已过会项目数量，根据业务类型分组
	 * 
	 * @return
	 */
	@RequestMapping("/getPreFromYghGroupServiceWithNum")
	@ResponseBody
	public Result getPreFromYghGroupServiceWithNum() {
		Result result = new Result();
		List<Map<String, Integer>> fromYghGroupServiceWithNum = deptworkService.getPreFromYghGroupServiceWithNum();
		result.setResult_data(fromYghGroupServiceWithNum);
		return result;
	}

	/**
	 * 获取已过会项目规模，根据业务类型分组
	 * 
	 * @return
	 */
	@RequestMapping("/getFromYghGroupServiceWithGm")
	@ResponseBody
	public Result getFromYghGroupServiceWithGm() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Integer>> fromYghGroupServiceWithGm = deptworkService.getFromYghGroupServiceWithGm();
		resultData.put("fromYghGroupServiceWithGm", fromYghGroupServiceWithGm);
		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	/**
	 * 获取投标评审已过会项目规模，根据业务类型分组
	 * 
	 * @return
	 */
	@RequestMapping("/getPreFromYghGroupServiceWithGm")
	@ResponseBody
	public Result getPreFromYghGroupServiceWithGm() {
		Result result = new Result();
		List<Map<String, Integer>> fromYghGroupServiceWithGm = deptworkService.getPreFromYghGroupServiceWithGm();
		result.setResult_data(fromYghGroupServiceWithGm);
		return result;
	}

	@RequestMapping("/getMonthlyReports")
	@ResponseBody
	public Map<String, int[]> getMonthlyReports() {
		Map<String, int[]> map = new HashMap<String, int[]>();
		String preAssessment = "preAssessment";
		String formalAssessment = "formalAssessment";
		map.put(preAssessment, getMonthlyReportstwo(preAssessment));
		map.put(formalAssessment, getMonthlyReportstwo(formalAssessment));
		return map;
	}

	private int[] getMonthlyReportstwo(String type) {
		int[] monthlyReports = new int[12];
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("type", type);
		List<Map<String, Object>> queryResults = DbUtil.openSession().selectList("projectInfo.countReportsByMonth",
				paramMap);
		for (Map<String, Object> queryResult : queryResults) {
			int month = Integer.valueOf((String) queryResult.get("MONTH")) - 1;
			BigDecimal num = (BigDecimal) queryResult.get("NUM");
			monthlyReports[month] = num.intValue();
		}
		return monthlyReports;
	}

	/**
	 * 初始化跟进中和已完成的数量
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/initFromCountReport")
	@ResponseBody
	public Result initFromCountReport() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();

		List<Map<String, Object>> countByStatus1 = this.deptworkService.countByStatus1();
		for (Map<String, Object> map : countByStatus1) {
			Object object = map.get("'FROMNAME'");
			String fromName = object.toString();
			if ("fromName".equals(fromName)) {
				String zsswghCount = map.get("COUNT(1)").toString();
				resultData.put("zsswghCount", zsswghCount);
			}
			if ("pre".equals(fromName)) {
				String ypswghCount = map.get("COUNT(1)").toString();
				resultData.put("ypswghCount", ypswghCount);
			}
		}

		List<Map<String, Object>> countByStatus2 = this.deptworkService.countByStatus2();
		for (Map<String, Object> map : countByStatus2) {
			Object object = map.get("'FROMNAME'");
			String fromName = object.toString();
			if ("fromName".equals(fromName)) {
				String zssyghCount = map.get("COUNT(1)").toString();
				resultData.put("zssyghCount", zssyghCount);
			}
			if ("pre".equals(fromName)) {
				String ypsyghCount = map.get("COUNT(1)").toString();
				resultData.put("ypsyghCount", ypsyghCount);
			}
		}
		result.setResult_data(resultData);
		return result;
	}

	@RequestMapping("/getFromWghByServietypeAndAreaWithNum")
	@ResponseBody
	public Result getFromWghByServietypeAndAreaWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> fromWghByServietypeAndAreaWithNum = deptworkService
				.getFromWghByServietypeAndAreaWithNum();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : fromWghByServietypeAndAreaWithNum) {
			String areaName = map.get("AREANAME").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String serviceName = (String) map.get("SERVICENAME");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();

			if (!areaList.contains(areaName)) {
				areaList.add(areaName);
			}
			if (!serviceTypeList.contains(serviceName)) {
				serviceTypeList.add(serviceName);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", serviceName);
				everyOne.put("data", new ArrayList<Integer>());
				everyOne.put("type", "bar");

				items.put(itemCode, everyOne);
			}

			List<Integer> nums = (List<Integer>) items.get(itemCode).get("data");
			nums.add(value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeList);
		resultData.put("areaList", areaList);
		resultData.put("dataList", dataList);

		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	@RequestMapping("/getPreFromWghByServietypeAndAreaWithNum")
	@ResponseBody
	public Result getPreFromWghByServietypeAndAreaWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> fromWghByServietypeAndAreaWithNum = deptworkService
				.getPreFromWghByServietypeAndAreaWithNum();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : fromWghByServietypeAndAreaWithNum) {
			String areaName = map.get("AREANAME").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String serviceName = (String) map.get("SERVICENAME");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();

			if (!areaList.contains(areaName)) {
				areaList.add(areaName);
			}
			if (!serviceTypeList.contains(serviceName)) {
				serviceTypeList.add(serviceName);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", serviceName);
				everyOne.put("data", new ArrayList<Integer>());
				everyOne.put("type", "bar");

				items.put(itemCode, everyOne);
			}

			List<Integer> nums = (List<Integer>) items.get(itemCode).get("data");
			nums.add(value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeList);
		resultData.put("areaList", areaList);
		resultData.put("dataList", dataList);

		result.setResult_data(resultData);
		return result;
	}

	@RequestMapping("/getFromWghByServietypeAndAreaWithGm")
	@ResponseBody
	public Result getFromWghByServietypeAndAreaWithGm() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> getFromWghByServietypeAndAreaWithGm = deptworkService
				.getFromWghByServietypeAndAreaWithGm();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : getFromWghByServietypeAndAreaWithGm) {
			String areaName = map.get("AREANAME").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String serviceName = (String) map.get("SERVICENAME");
			BigDecimal value;
			if (Util.isEmpty(map.get("NUM"))) {
				value = BigDecimal.ZERO;
			} else {
				value = ((BigDecimal) map.get("NUM"));
			}

			if (!areaList.contains(areaName)) {
				areaList.add(areaName);
			}
			if (!serviceTypeList.contains(serviceName)) {
				serviceTypeList.add(serviceName);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", serviceName);
				everyOne.put("data", new ArrayList<Integer>());
				everyOne.put("type", "bar");

				items.put(itemCode, everyOne);
			}

			List<BigDecimal> nums = (List<BigDecimal>) items.get(itemCode).get("data");
			nums.add(value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeList);
		resultData.put("areaList", areaList);
		resultData.put("dataList", dataList);

		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	@RequestMapping("/getPreFromWghByServietypeAndAreaWithGm")
	@ResponseBody
	public Result getPreFromWghByServietypeAndAreaWithGm() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> getFromWghByServietypeAndAreaWithGm = deptworkService
				.getPreFromWghByServietypeAndAreaWithGm();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : getFromWghByServietypeAndAreaWithGm) {
			String areaName = map.get("AREANAME").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String serviceName = (String) map.get("SERVICENAME");
			BigDecimal value;
			if (Util.isEmpty(map.get("NUM"))) {
				value = BigDecimal.ZERO;
			} else {
				value = ((BigDecimal) map.get("NUM"));
			}

			if (!areaList.contains(areaName)) {
				areaList.add(areaName);
			}
			if (!serviceTypeList.contains(serviceName)) {
				serviceTypeList.add(serviceName);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", serviceName);
				everyOne.put("data", new ArrayList<Integer>());
				everyOne.put("type", "bar");

				items.put(itemCode, everyOne);
			}

			List<BigDecimal> nums = (List<BigDecimal>) items.get(itemCode).get("data");
			nums.add(value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeList);
		resultData.put("areaList", areaList);
		resultData.put("dataList", dataList);
		result.setResult_data(resultData);
		return result;
	}

	@RequestMapping("/getFromYghByServietypeAndAreaWithNum")
	@ResponseBody
	public Result getFromYghByServietypeAndAreaWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> getFromYghByServietypeAndAreaWithNum = deptworkService
				.getFromYghByServietypeAndAreaWithNum();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : getFromYghByServietypeAndAreaWithNum) {
			String areaName = map.get("AREANAME").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String serviceName = (String) map.get("SERVICENAME");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();

			if (!areaList.contains(areaName)) {
				areaList.add(areaName);
			}
			if (!serviceTypeList.contains(serviceName)) {
				serviceTypeList.add(serviceName);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", serviceName);
				everyOne.put("data", new ArrayList<Integer>());
				everyOne.put("type", "bar");

				items.put(itemCode, everyOne);
			}

			List<Integer> nums = (List<Integer>) items.get(itemCode).get("data");
			nums.add(value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeList);
		resultData.put("areaList", areaList);
		resultData.put("dataList", dataList);

		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	@RequestMapping("/getPreFromYghByServietypeAndAreaWithNum")
	@ResponseBody
	public Result getPreFromYghByServietypeAndAreaWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> getFromYghByServietypeAndAreaWithNum = deptworkService
				.getPreFromYghByServietypeAndAreaWithNum();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : getFromYghByServietypeAndAreaWithNum) {
			String areaName = map.get("AREANAME").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String serviceName = (String) map.get("SERVICENAME");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();

			if (!areaList.contains(areaName)) {
				areaList.add(areaName);
			}
			if (!serviceTypeList.contains(serviceName)) {
				serviceTypeList.add(serviceName);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", serviceName);
				everyOne.put("data", new ArrayList<Integer>());
				everyOne.put("type", "bar");

				items.put(itemCode, everyOne);
			}

			List<Integer> nums = (List<Integer>) items.get(itemCode).get("data");
			nums.add(value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeList);
		resultData.put("areaList", areaList);
		resultData.put("dataList", dataList);
		result.setResult_data(resultData);
		return result;
	}

	@RequestMapping("/getFromYghByServietypeAndAreaWithGm")
	@ResponseBody
	public Result getFromYghByServietypeAndAreaWithGm() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> getFromYghByServietypeAndAreaWithGm = deptworkService
				.getFromYghByServietypeAndAreaWithGm();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : getFromYghByServietypeAndAreaWithGm) {
			String areaName = map.get("AREANAME").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String serviceName = (String) map.get("SERVICENAME");
			BigDecimal value;
			if (Util.isEmpty(map.get("NUM"))) {
				value = BigDecimal.ZERO;
			} else {
				value = (BigDecimal) map.get("NUM");
			}

			if (!areaList.contains(areaName)) {
				areaList.add(areaName);
			}
			if (!serviceTypeList.contains(serviceName)) {
				serviceTypeList.add(serviceName);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", serviceName);
				everyOne.put("data", new ArrayList<Integer>());
				everyOne.put("type", "bar");

				items.put(itemCode, everyOne);
			}

			List<BigDecimal> nums = (List<BigDecimal>) items.get(itemCode).get("data");
			nums.add(value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeList);
		resultData.put("areaList", areaList);
		resultData.put("dataList", dataList);

		result.setResult_data(resultData);

		result.setResult_code("S");
		return result;
	}

	@RequestMapping("/getPreFromYghByServietypeAndAreaWithGm")
	@ResponseBody
	public Result getPreFromYghByServietypeAndAreaWithGm() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> getFromYghByServietypeAndAreaWithGm = deptworkService
				.getPreFromYghByServietypeAndAreaWithGm();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : getFromYghByServietypeAndAreaWithGm) {
			String areaName = map.get("AREANAME").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String serviceName = (String) map.get("SERVICENAME");
			BigDecimal value;
			if (Util.isEmpty(map.get("NUM"))) {
				value = BigDecimal.ZERO;
			} else {
				value = (BigDecimal) map.get("NUM");
			}

			if (!areaList.contains(areaName)) {
				areaList.add(areaName);
			}
			if (!serviceTypeList.contains(serviceName)) {
				serviceTypeList.add(serviceName);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", serviceName);
				everyOne.put("data", new ArrayList<Integer>());
				everyOne.put("type", "bar");

				items.put(itemCode, everyOne);
			}

			List<BigDecimal> nums = (List<BigDecimal>) items.get(itemCode).get("data");
			nums.add(value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeList);
		resultData.put("areaList", areaList);
		resultData.put("dataList", dataList);

		result.setResult_data(resultData);
		return result;
	}

	@RequestMapping("/initTablePreWghByServietypeAndAreaWithNum")
	@ResponseBody
	public Result initTableFromWghByServietypeAndAreaWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> fromWghByServietypeAndAreaWithNum = deptworkService
				.getPreFromWghByServietypeAndAreaWithNum();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		List<Map<String, Object>> serviceTypeMapList = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : fromWghByServietypeAndAreaWithNum) {
			String areaId = map.get("AREAID").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String type = (String) map.get("SERVICENAME");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();

			if (!areaList.contains(areaId)) {
				areaList.add(areaId);
			}
			if (!serviceTypeList.contains(type)) {
				serviceTypeList.add(type);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", type);
				everyOne.put("data", new HashMap<String, Object>());
				everyOne.put("type", "bar");
				items.put(itemCode, everyOne);
				Map<String, Object> serviceTypeMap = new HashMap<String, Object>();
				serviceTypeMap.put("name", type);
				serviceTypeMap.put("value", itemCode);
				serviceTypeMapList.add(serviceTypeMap);
			}

			Map<String, Object> nums = (Map<String, Object>) items.get(itemCode).get("data");
			HashMap<String, Object> area = new HashMap<String, Object>();
			nums.put(areaId, value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeMapList);
		ArrayList<Map<String, Object>> areaMapList = new ArrayList<Map<String, Object>>();
		for (String areaId : areaList) {
			Map<String, Object> areaMap = new HashMap<String, Object>();
			Map<String, Object> queryByPkvalue = orgService.queryByPkvalue(areaId);
			areaMap.put("name", queryByPkvalue.get("NAME"));
			areaMap.put("value", areaId);
			areaMapList.add(areaMap);
		}
		resultData.put("areaList", areaMapList);
		resultData.put("dataList", dataList);
		List<Map<String, Integer>> totalList = deptworkService.getPreaWghGroupAreaReports();
		resultData.put("totalList", totalList);
		result.setResult_data(resultData);
		return result;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/initTableFormalWghByServietypeAndAreaWithNum")
	@ResponseBody
	public Result initTableFormalWghByServietypeAndAreaWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> fromWghByServietypeAndAreaWithNum = deptworkService
				.getFromWghByServietypeAndAreaWithNum();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		List<Map<String, Object>> serviceTypeMapList = new ArrayList<Map<String, Object>>();

		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : fromWghByServietypeAndAreaWithNum) {
			String AREAID = map.get("AREAID").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String type = (String) map.get("SERVICENAME");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();

			if (!areaList.contains(AREAID)) {
				areaList.add(AREAID);
			}
			if (!serviceTypeList.contains(type)) {
				serviceTypeList.add(type);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", type);
				everyOne.put("data", new HashMap<String, Object>());
				everyOne.put("type", "bar");
				items.put(itemCode, everyOne);
				Map<String, Object> serviceTypeMap = new HashMap<String, Object>();
				serviceTypeMap.put("name", type);
				serviceTypeMap.put("value", itemCode);
				serviceTypeMapList.add(serviceTypeMap);
			}

			Map<String, Object> nums = (Map<String, Object>) items.get(itemCode).get("data");
			HashMap<String, Object> area = new HashMap<String, Object>();
			nums.put(AREAID, value);

		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeMapList);
		ArrayList<Map<String, Object>> areaMapList = new ArrayList<Map<String, Object>>();
		for (String areaId : areaList) {
			Map<String, Object> areaMap = new HashMap<String, Object>();
			Map<String, Object> queryByPkvalue = orgService.queryByPkvalue(areaId);
			areaMap.put("name", queryByPkvalue.get("NAME"));
			areaMap.put("value", areaId);
			areaMapList.add(areaMap);
		}
		resultData.put("areaList", areaMapList);
		resultData.put("dataList", dataList);
		List<Map<String, Integer>> totalList = deptworkService.getFormalaWghGroupAreaReports();
		resultData.put("totalList", totalList);
		result.setResult_data(resultData);
		return result;
	}

	@RequestMapping("/initTablePreYghByServietypeAndAreaWithNum")
	@ResponseBody
	public Result initTablePreYghByServietypeAndAreaWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> fromWghByServietypeAndAreaWithNum = deptworkService
				.getPreFromYghByServietypeAndAreaWithNum();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();

		List<Map<String, Object>> serviceTypeMapList = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : fromWghByServietypeAndAreaWithNum) {
			String areaId = map.get("AREAID").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String type = (String) map.get("SERVICENAME");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();
			if (!areaList.contains(areaId)) {
				areaList.add(areaId);
			}
			if (!serviceTypeList.contains(type)) {
				serviceTypeList.add(type);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", type);
				everyOne.put("data", new HashMap<String, Object>());
				everyOne.put("type", "bar");
				items.put(itemCode, everyOne);
				Map<String, Object> serviceTypeMap = new HashMap<String, Object>();
				serviceTypeMap.put("name", type);
				serviceTypeMap.put("value", itemCode);
				serviceTypeMapList.add(serviceTypeMap);
			}

			Map<String, Object> nums = (Map<String, Object>) items.get(itemCode).get("data");
			HashMap<String, Object> area = new HashMap<String, Object>();
			nums.put(areaId, value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeMapList);
		ArrayList<Map<String, Object>> areaMapList = new ArrayList<Map<String, Object>>();
		for (String areaId : areaList) {
			Map<String, Object> areaMap = new HashMap<String, Object>();
			Map<String, Object> queryByPkvalue = orgService.queryByPkvalue(areaId);
			areaMap.put("name", queryByPkvalue.get("NAME"));
			areaMap.put("value", areaId);
			areaMapList.add(areaMap);
		}
		resultData.put("areaList", areaMapList);
		resultData.put("dataList", dataList);
		List<Map<String, Integer>> totalList = deptworkService.getPreaYghGroupAreaReports();
		resultData.put("totalList", totalList);
		result.setResult_data(resultData);
		return result;
	}

	@RequestMapping("/initTableFormalYghByServietypeAndAreaWithNum")
	@ResponseBody
	public Result initTableFormalYghByServietypeAndAreaWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> fromWghByServietypeAndAreaWithNum = deptworkService
				.getFromYghByServietypeAndAreaWithNum();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		List<Map<String, Object>> serviceTypeMapList = new ArrayList<Map<String, Object>>();

		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : fromWghByServietypeAndAreaWithNum) {
			String areaId = map.get("AREAID").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String type = (String) map.get("SERVICENAME");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();

			if (!areaList.contains(areaId)) {
				areaList.add(areaId);
			}
			if (!serviceTypeList.contains(type)) {
				serviceTypeList.add(type);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", type);
				everyOne.put("data", new HashMap<String, Object>());
				everyOne.put("type", "bar");
				items.put(itemCode, everyOne);
				Map<String, Object> serviceTypeMap = new HashMap<String, Object>();
				serviceTypeMap.put("name", type);
				serviceTypeMap.put("value", itemCode);
				serviceTypeMapList.add(serviceTypeMap);
			}

			Map<String, Object> nums = (Map<String, Object>) items.get(itemCode).get("data");
			HashMap<String, Object> area = new HashMap<String, Object>();
			nums.put(areaId, value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeMapList);
		ArrayList<Map<String, Object>> areaMapList = new ArrayList<Map<String, Object>>();
		for (String areaId : areaList) {
			Map<String, Object> areaMap = new HashMap<String, Object>();
			Map<String, Object> queryByPkvalue = orgService.queryByPkvalue(areaId);
			areaMap.put("name", queryByPkvalue.get("NAME"));
			areaMap.put("value", areaId);
			areaMapList.add(areaMap);
		}
		resultData.put("areaList", areaMapList);
		resultData.put("dataList", dataList);
		List<Map<String, Integer>> totalList = deptworkService.getFormalaYghGroupAreaReports();
		resultData.put("totalList", totalList);
		result.setResult_data(resultData);
		return result;
	}

	@RequestMapping("/queryBulletinCount")
	@ResponseBody
	public Result queryBulletinCount(HttpServletRequest request) {
		Result result = new Result();

		String wf_state = request.getParameter("wf_state");
		String stage = request.getParameter("stage");
		String pertainAreaId = request.getParameter("pertainAreaId");
		String serviceTypeId = request.getParameter("serviceTypeId");
		String year = request.getParameter("year");

		List<Map<String, Object>> list = bulletinInfoService.queryBulletinCount(wf_state, stage, pertainAreaId,
				serviceTypeId, year);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		if (Util.isEmpty(year)) {
			int y = Util.now().getYear() + 1900;
			map.put("year", y);
		} else {
			map.put("year", year);
		}
		result.setResult_data(map);
		return result;
	}

	@RequestMapping("/initTableBulletinWghByServietypeAndAreaWithNum")
	@ResponseBody
	public Result initTableBulletinWghByServietypeAndAreaWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> fromWghByServietypeAndAreaWithNum = deptworkService
				.getBulletinFromWghByServietypeAndAreaWithNum();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		List<Map<String, Object>> serviceTypeMapList = new ArrayList<Map<String, Object>>();

		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : fromWghByServietypeAndAreaWithNum) {
			String areaId = map.get("AREAID").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String serviceName = (String) map.get("SERVICENAME");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();

			if (!areaList.contains(areaId)) {
				areaList.add(areaId);
			}
			if (!serviceTypeList.contains(serviceName)) {
				serviceTypeList.add(serviceName);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", serviceName);
				everyOne.put("data", new HashMap<String, Object>());
				everyOne.put("type", "bar");
				items.put(itemCode, everyOne);
				Map<String, Object> serviceTypeMap = new HashMap<String, Object>();
				serviceTypeMap.put("name", serviceName);
				serviceTypeMap.put("value", itemCode);
				serviceTypeMapList.add(serviceTypeMap);
			}

			Map<String, Object> nums = (Map<String, Object>) items.get(itemCode).get("data");
			HashMap<String, Object> area = new HashMap<String, Object>();
			nums.put(areaId, value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeMapList);
		ArrayList<Map<String, Object>> areaMapList = new ArrayList<Map<String, Object>>();
		for (String areaId : areaList) {
			Map<String, Object> areaMap = new HashMap<String, Object>();
			Map<String, Object> queryByPkvalue = orgService.queryByPkvalue(areaId);
			areaMap.put("name", queryByPkvalue.get("NAME"));
			areaMap.put("value", areaId);
			areaMapList.add(areaMap);
		}
		resultData.put("areaList", areaMapList);
		resultData.put("dataList", dataList);
		List<Map<String, Integer>> totalList = deptworkService.getBulletinWghGroupAreaReports();
		resultData.put("totalList", totalList);
		result.setResult_data(resultData);
		return result;
	}

	@RequestMapping("/queryBulletinPertainArea")
	@ResponseBody
	public Result queryBulletinPertainArea(HttpServletRequest request) {
		Result result = new Result();
		List<Map<String, Object>> list = bulletinInfoService.queryBulletinPertainArea();
		result.setResult_data(list);
		return result;
	}

	/**
	 * 查询所有投标评审项目
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAllBulletinInfoByPage")
	@ResponseBody
	public Result queryAllBulletinInfoByPage(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		bulletinInfoService.queryAllInfoByPage(page);
		result.setResult_data(page);
		return result;
	}

	@RequestMapping("/initTableBulletinYghByServietypeAndAreaWithNum")
	@ResponseBody
	public Result initTableBulletinYghByServietypeAndAreaWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> fromWghByServietypeAndAreaWithNum = deptworkService
				.getBulletinFromYghByServietypeAndAreaWithNum();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		List<Map<String, Object>> serviceTypeMapList = new ArrayList<Map<String, Object>>();

		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : fromWghByServietypeAndAreaWithNum) {
			String areaId = map.get("AREAID").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String serviceName = (String) map.get("SERVICENAME");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();

			if (!areaList.contains(areaId)) {
				areaList.add(areaId);
			}
			if (!serviceTypeList.contains(serviceName)) {
				serviceTypeList.add(serviceName);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", serviceName);
				everyOne.put("data", new HashMap<String, Object>());
				everyOne.put("type", "bar");
				items.put(itemCode, everyOne);
				Map<String, Object> serviceTypeMap = new HashMap<String, Object>();
				serviceTypeMap.put("name", serviceName);
				serviceTypeMap.put("value", itemCode);
				serviceTypeMapList.add(serviceTypeMap);
			}

			Map<String, Object> nums = (Map<String, Object>) items.get(itemCode).get("data");
			HashMap<String, Object> area = new HashMap<String, Object>();
			nums.put(areaId, value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeMapList);
		ArrayList<Map<String, Object>> areaMapList = new ArrayList<Map<String, Object>>();
		for (String areaId : areaList) {
			Map<String, Object> areaMap = new HashMap<String, Object>();
			Map<String, Object> queryByPkvalue = orgService.queryByPkvalue(areaId);
			areaMap.put("name", queryByPkvalue.get("NAME"));
			areaMap.put("value", areaId);
			areaMapList.add(areaMap);
		}
		resultData.put("areaList", areaMapList);
		resultData.put("dataList", dataList);
		List<Map<String, Integer>> totalList = deptworkService.getBulletinYghGroupAreaReports();
		resultData.put("totalList", totalList);
		result.setResult_data(resultData);
		return result;
	}

	/**
	 * 已过会
	 * 
	 * @return
	 */
	@RequestMapping("/getBulletinFromYghByServietypeAndAreaWithNum")
	@ResponseBody
	public Result getBulletinFromYghByServietypeAndAreaWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> getFromYghByServietypeAndAreaWithNum = deptworkService
				.getBulletinFromYghByServietypeAndAreaWithNum();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : getFromYghByServietypeAndAreaWithNum) {
			String areaName = map.get("AREANAME").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String serviceName = (String) map.get("SERVICENAME");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();

			if (!areaList.contains(areaName)) {
				areaList.add(areaName);
			}
			if (!serviceTypeList.contains(serviceName)) {
				serviceTypeList.add(serviceName);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", serviceName);
				everyOne.put("data", new ArrayList<Integer>());
				everyOne.put("type", "bar");

				items.put(itemCode, everyOne);
			}

			List<Integer> nums = (List<Integer>) items.get(itemCode).get("data");
			nums.add(value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeList);
		resultData.put("areaList", areaList);
		resultData.put("dataList", dataList);
		result.setResult_data(resultData);
		return result;
	}

	/**
	 * 没过会
	 * 
	 * @return
	 */
	@RequestMapping("/getBulletinFromWghByServietypeAndAreaWithNum")
	@ResponseBody
	public Result getBulletinFromWghByServietypeAndAreaWithNum() {
		Result result = new Result();
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> fromWghByServietypeAndAreaWithNum = deptworkService
				.getBulletinFromWghByServietypeAndAreaWithNum();
		List<String> areaList = new ArrayList<String>();
		List<String> serviceTypeList = new ArrayList<String>();
		Map<String, Map<String, Object>> items = new TreeMap<String, Map<String, Object>>();
		for (Map<String, Object> map : fromWghByServietypeAndAreaWithNum) {
			String areaName = map.get("AREANAME").toString();
			String itemCode = (String) map.get("ITEM_CODE");
			String serviceName = (String) map.get("SERVICENAME");
			Integer value = ((BigDecimal) map.get("NUM")).intValue();

			if (!areaList.contains(areaName)) {
				areaList.add(areaName);
			}
			if (!serviceTypeList.contains(serviceName)) {
				serviceTypeList.add(serviceName);
				Map<String, Object> everyOne = new HashMap<String, Object>();
				everyOne.put("name", serviceName);
				everyOne.put("data", new ArrayList<Integer>());
				everyOne.put("type", "bar");

				items.put(itemCode, everyOne);
			}

			List<Integer> nums = (List<Integer>) items.get(itemCode).get("data");
			nums.add(value);
		}
		Collection dataList = items.values();
		resultData.put("serviceTypeList", serviceTypeList);
		resultData.put("areaList", areaList);
		resultData.put("dataList", dataList);

		result.setResult_data(resultData);
		return result;
	}

	/**
	 * 已决策，同意投资
	 * 
	 * @return
	 */
	@RequestMapping("/getNoticeTYCount")
	@ResponseBody
	public Result getNoticeTYCount() {
		Result result = new Result();
		Map<String, Object> noticeTYCount = this.deptworkService.getNoticeTYCount();
		result.setResult_data(noticeTYCount);
		return result;
	}

	/**
	 * 已决策，不同意投资
	 * 
	 * @return
	 */
	@RequestMapping("/getNoticeBTYCount")
	@ResponseBody
	public Result getNoticeBTYCount() {
		Result result = new Result();
		Map<String, Object> noticeTYCount = this.deptworkService.getNoticeBTYCount();
		result.setResult_data(noticeTYCount);
		return result;
	}

	/**
	 * 获取2017-当前年数组
	 * 
	 * @return
	 */
	@RequestMapping("/getYearArr")
	@ResponseBody
	public Result getYearArr() {
		Result result = new Result();
		int oldYear = 2017;
		Date now = Util.now();
		// 获取当前年
		int year = now.getYear() + 1900;
		List<Integer> yearArr = new ArrayList<Integer>();
		for (int i = 1; year >= oldYear; i++) {
			yearArr.add(oldYear++);
		}
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("yearArr", yearArr);
		hashMap.put("currYear", year);
		result.setResult_data(hashMap);
		return result;
	}

	/**
	 * 获取所有员工当前工作量
	 * 
	 * @return
	 */
	@RequestMapping("/getAllStaffWork")
	@ResponseBody
	public Result getAllStaffWork() {
		Result result = new Result();
		Map<String, Object> allStaffWork = this.deptworkService.getAllStaffWork();
		result.setResult_data(allStaffWork);
		return result;
	}
	
	/**
	 * 获取所有员工当前工作量
	 * 
	 * @return
	 */
	@RequestMapping("/getAllLegalStaffWork")
	@ResponseBody
	public Result getAllLegalStaffWork() {
		Result result = new Result();
		Map<String, Object> allStaffWork = this.deptworkService.getAllLegalStaffWork();
		result.setResult_data(allStaffWork);
		return result;
	}
	
	/**
	 * 获取所有员工当前工作量
	 * 
	 * @return
	 */
	@RequestMapping("/getOneStaffWork")
	@ResponseBody
	public Result getOneStaffWork(HttpServletRequest request) {
		Result result = new Result();
		String TEAM_MEMBER_ID = request.getParameter("TEAM_MEMBER_ID");
		List<Map<String, Object>> oneStaffWork = this.deptworkService.getOneStaffWork(TEAM_MEMBER_ID);
		result.setResult_data(oneStaffWork);
		return result;
	}
	
	/**
	 * 获取所有员工当前工作量
	 * 
	 * @return
	 */
	@RequestMapping("/getOneLegalStaffWork")
	@ResponseBody
	public Result getOneLegalStaffWork(HttpServletRequest request) {
		Result result = new Result();
		String TEAM_MEMBER_ID = request.getParameter("TEAM_MEMBER_ID");
		List<Map<String, Object>> oneStaffWork = this.deptworkService.getOneLegalStaffWork(TEAM_MEMBER_ID);
		result.setResult_data(oneStaffWork);
		return result;
	}

}
