/**
 * 
 */
package com.yk.rcm.noticeofdecision.controller;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionDraftInfoService;

import common.PageAssistant;
import common.Result;

/**
 * @author yaphet
 *
 */
@Controller
@RequestMapping("/noticeDecisionDraftInfo")
public class NoticeDecisionDraftInfoContoller {
	
	@Resource
	private INoticeDecisionDraftInfoService noticeDecisionDraftService;
	@Resource
	private IFormalAssessmentAuditService formalAssessmentAuditService;
	
	/**
	 * 分页查询待提交数据
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryApplyList")
	@ResponseBody
	public Result queryApplyList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.noticeDecisionDraftService.queryStartByPage(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 分页查询未上会数据
	 * @param request
	 * @return
	 */
	@RequestMapping("/notMeetingList")
	@ResponseBody
	public Result notMeetingList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.noticeDecisionDraftService.notMeetingList(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 分页查询已上会数据
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryApplyedList")
	@ResponseBody
	public Result queryApplyedList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.noticeDecisionDraftService.queryOverByPage(page);
		result.setResult_data(page);
		return result;
	}

	/**
	 * 修改决策通知书信息
	 * @param request
	 * @return Result
	 */
	@RequestMapping("/update")
	@ResponseBody
	public Result update(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("nod");
		this.noticeDecisionDraftService.update(json);
		return result;
	}
	/**
	 * 根据businessId删除决策通知书
	 * @param request
	 * @return Result
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public Result delete(HttpServletRequest request){
		Result result = new Result();
		String formalIds = request.getParameter("formalIds");
		this.noticeDecisionDraftService.deleteByFormalIds(formalIds);
		return result;
	}
	
	/**
	 * 根据ID查询
	 * 查询新增页面初始数据
	 * @param request
	 * @return
	 */
	@RequestMapping("/querySaveDefaultInfo")
	@ResponseBody
	public Result querySaveDefaultInfo(HttpServletRequest request){
		Result result = new Result();
		String userId = request.getParameter("businessId");
		Map<String, Object> queryUpdateDefaultInfo = this.noticeDecisionDraftService.querySaveDefaultInfo(userId);
		result.setResult_data(queryUpdateDefaultInfo);
		return result;
	}
	
	/**
	 * 新增决策通知书
	 * @param request
	 * @return Result
	 */
	@RequestMapping("/create")
	@ResponseBody
	public Result create(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("nod");
		String create = this.noticeDecisionDraftService.create(json);
		result.setResult_data(create);
		return result;
	}
	
	/**
	 * 提交之后修改stage的状态='3.9'
	 * @param request
	 * @return
	 */
	@RequestMapping("submitUpdateStage")
	@ResponseBody
	public Result submitUpdateStage(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("projectFormalId");
		String stage = request.getParameter("stage");
		this.noticeDecisionDraftService.updateStageByBusinessId(businessId, stage);
		return result;
	}
	
	/**
	 * 根据正式评审的ID查询对应的决策通知书
	 * @param request
	 * @return Result
	 */
	@RequestMapping("/queryNoticeDecstion")
	@ResponseBody
	public Result queryNoticeDecstion(HttpServletRequest request){
		Result result = new Result();
		String formalId = request.getParameter("formalId");
		Map<String, Object> doc = this.noticeDecisionDraftService.queryNoticeDecstionByFormalId(formalId);
		result.setResult_data(doc);
		return result;
	}
	
	/**
	 * 查询可以起草决策通知书的项目
	 * @param request
	 * @return Result
	 */
	@RequestMapping("/queryFormalForCreate")
	@ResponseBody
	public Result queryFormalForCreate(HttpServletRequest request){
		Result result = new Result();
		List<Map<String, Object>> formals = this.noticeDecisionDraftService.queryFormalForCreate();
		result.setResult_data(formals);
		return result;
	}
	/**
	 * 决策通知书生成word文档
	 * @param request
	 * @return
	 */
	@RequestMapping("/getNoticeOfDecisionWord")
	@ResponseBody
	public Result getNoticeOfDecisionWord(HttpServletRequest request){
		Result result = new Result();
		String formalId = request.getParameter("formalId");
		Map<String, String> map = this.noticeDecisionDraftService.getNoticeOfDecisionWord(formalId);
		result.setResult_data(map);
		return result;
	}
	
	/**
	 * 决策通知书生成word文档
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveDecisionFile")
	@ResponseBody
	public Result saveDecisionFile(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.noticeDecisionDraftService.saveDecisionFile(json);
		return result;
	}
}
