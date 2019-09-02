/**
 * 
 */
package com.yk.rcm.formalAssessment.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.formalAssessment.service.IProfessionService;
import common.PageAssistant;
import common.Result;

/**
 * 专业评审人员维护controller
 * @author wufucan
 *
 */
@Controller
@RequestMapping("/profession")
public class ProfessionController {
	
	@Resource
	private IProfessionService professionService;
	
	
	/**
	 * 查询所有的组，不分启用禁用
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAllTeams")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询所有的组，不分启用禁用")
	public Result queryAllTeams(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.professionService.queryAllTeams(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 新增组
	 * @return
	 */
	@RequestMapping("/addTeam")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "新增组")
	public Result addTeam(HttpServletRequest request){
		String json = request.getParameter("profession");
		Result result = this.professionService.addTeam(json);
		return result;
	}
	/**
	 * 根据组的id查询组内成员
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryMembersByTeamId")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询组内成员")
	public Result queryMembersByTeamId(HttpServletRequest request){
		Result result = new Result();
		String teamId = request.getParameter("teamId");
		List<Map<String, Object>> info = this.professionService.queryMembersByTeamId(teamId);
		result.setResult_data(info);
		return result;
	}
	/**
	 * 查询一个用户的数据
	 * @param request
	 * @return
	 */
	@RequestMapping("/getTeamByID")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "询一个用户的数据")
	public Result getTeamByID(HttpServletRequest request){
		Result result = new Result();
		String teamId = request.getParameter("teamId");
		Map<String, Object> info = this.professionService.getTeamByID(teamId);
		result.setResult_data(info);
		return result;
	}
	/**
	 * 更新组信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/updateTeam")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.UPDATE, description = "更新组信息")
	public Result updateTeam(HttpServletRequest request){
		String json = request.getParameter("profession");
		Result result = this.professionService.updateTeam(json);
		return result;
	}
	/**
	 * 删除成员信息并且修改组为禁用
	 * @param request
	 * @return
	 */
	@RequestMapping("/updateTeamSatusById")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.DELETE, description = "删除成员信息并且修改组为禁用")
	public Result updateTeamSatusById(HttpServletRequest request){
		Result result = new Result();
		String teamId = request.getParameter("teamId");
		this.professionService.updateTeamSatusById(teamId);
		return result;
	}
	/**
	 * 查询专业评审负责人
	 * @return
	 */
	@RequestMapping("/queryProfessionReview")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询专业评审负责人")
	public Result queryProfessionReview(){
		Result result = new Result();
		List<Map<String, Object>> paramMap = professionService.queryProfessionReview();
		result.setResult_data(paramMap);
		return result;
	}

}
