/**
 * 
 */
package com.yk.rcm.formalAssessment.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
	public Result queryProfessionReview(){
		Result result = new Result();
		List<Map<String, Object>> paramMap = professionService.queryProfessionReview();
		result.setResult_data(paramMap);
		return result;
	}

}
