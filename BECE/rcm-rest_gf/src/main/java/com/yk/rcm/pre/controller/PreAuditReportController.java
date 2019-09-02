package com.yk.rcm.pre.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.goukuai.constant.YunkuConf;
import com.goukuai.dto.FileDto;
import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import com.yk.rcm.file.service.IFileService;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.pre.service.IPreAuditReportService;

import common.PageAssistant;
import common.Result;
import util.UserUtil;

/**
 * 投资评审报告
 * 
 * @author dsl
 *
 */
@Controller
@RequestMapping("/preAuditReport")
public class PreAuditReportController {

	@Resource
	private IPreAuditReportService preAuditReportService;

	/**
	 * 获取还未新建投标评审报告的项目
	 * 
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/queryNotNewlyPreAuditProject")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "获取还未新建投标评审报告的项目")
	public Result queryNotNewlyPreAuditProject(HttpServletRequest request) {
		Result result = new Result();

		List<Map<String, Object>> listMap = this.preAuditReportService.queryNotNewlyPreAuditProject();
		result.setResult_data(listMap);
		return result;
	}

	/**
	 * 查询已新建投资评审报告但未提交报告的项目
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @param json
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/queryUncommittedReportByPage")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "查询已新建投资评审报告但未提交报告的项目")
	public Result queryUncommittedReportByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.preAuditReportService.queryUncommittedReportByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);
		return result;
	}

	/**
	 * 查询已提交投资评审报告的项目
	 * 
	 * @param page
	 *            {@link PageAssistant}
	 * @param json
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/querySubmittedReportByPage")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "查询已提交投资评审报告的项目")
	public Result querySubmittedReportByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.preAuditReportService.querySubmittedReportByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);
		return result;
	}

	/**
	 * 删除已新建投资评审报告但未提交报告的项目
	 * 
	 * @param ids
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/batchDeletePreReport")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.DELETE, description = "删除已新建投资评审报告但未提交报告的项目")
	public Result batchDeletePreReport(String ids, HttpServletRequest request) {
		Result result = new Result();
		String[] businessids = ids.split(",");
		this.preAuditReportService.batchDeletePreReport(businessids);

		return result;
	}

	/**
	 * 获取新建评审报告项目信息
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/getPreProjectFormalReviewByID")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "获取新建评审报告项目信息")
	public Result getPreProjectFormalReviewByID(String id, HttpServletRequest request) {
		Result result = new Result();
		Document doc = this.preAuditReportService.getPreProjectFormalReviewByID(id);
		result.setResult_data(doc);

		return result;
	}

	/**
	 * 保存评审报告项目信息
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/saveReviewReportById")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.CREATE, description = "保存评审报告项目信息")
	public Result saveReviewReportById(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.preAuditReportService.saveReviewReportById(json);
		result.setResult_data(id);

		return result;
	}

	/**
	 * 修改报告
	 * 
	 * @param json
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/updateReviewReport")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.UPDATE, description = "修改报告")
	public Result updateReviewReport(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.preAuditReportService.updateReviewReport(json);
		result.setResult_data(id);

		return result;
	}

	/**
	 * 提交报告
	 * 
	 * @param businessid
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/submitAndupdate")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.UPDATE, description = "提交报告")
	public Result submitAndupdate(String businessid, HttpServletRequest request) {
		Result result = new Result();
		this.preAuditReportService.submitAndupdate(businessid);

		return result;
	}

	/**
	 * 查询项目信息
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "查询项目信息")
	public Result getById(String id, HttpServletRequest request) {
		Result result = new Result();
		Document document = this.preAuditReportService.getById(id);
		result.setResult_data(document);

		return result;
	}

	@Resource
	private IFileService fileService;
	/**
	 * 生成word文档
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/getPreWordReport")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.CREATE, description = "生成word文档")
	public Result getPreWordReport(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, String> map = this.preAuditReportService.getPreWordReport(id);
		result.setResult_data(map);
		/*云库同步代码开始 add by LiPan 2019-05-14*/
		String fullPath = YunkuConf.UPLOAD_ROOT + "/FormalReportInfo/" + id + "/";
		String filePath = map.get("filePath");
		String fileName = map.get("fileName");// 这里是项目名称,不是XXX.doc格式
		String fileExt = filePath.substring(filePath.lastIndexOf("."));
		String finalName = "投资评审-" + fileName + "报告" + fileExt;
		File file = new File(filePath);
		String optName = UserUtil.getCurrentUserName();
		Integer optId = new Integer(UserUtil.getCurrentUserId());
		String docType = "preReportInfo";
		String docCode = id;
		String pageLocation = "preReport";
		// 删除之前的
		try{
			fileService.fileDelete(fullPath.replaceFirst(YunkuConf.UPLOAD_ROOT, "") + finalName, optName);
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			// 上传最新的
			FileDto fileDto = fileService.fileUpload(fullPath.replaceFirst(YunkuConf.UPLOAD_ROOT, "") + finalName, file.getAbsolutePath(), docType, docCode, pageLocation, optName, optId);
			// 获取其链接
			List<FileDto> list = fileService.createFileList(docType, docCode, pageLocation);
			if(CollectionUtils.isNotEmpty(list)){
				fileDto = list.get(0);
			}
			Map<String, Object> newMap = new HashMap<String, Object>();
			newMap.putAll(map);
			newMap.put("fileDto", fileDto);
			result.setResult_data(newMap);
		}catch(Exception e){
			e.printStackTrace();
		}
		/*云库同步代码结束 add by LiPan 2019-05-14*/
		return result;
	}
	
	/**
	 * 查看报告是否可提交(如果流程未结束，则不可提交报告)
	 * 
	 * @param businessid
	 *            String
	 * @param request
	 *            {@link HttpServletRequest}
	 * @return {@link Result}
	 */
	@RequestMapping("/isPossible2Submit")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "查看报告是否可提交(如果流程未结束，则不可提交报告)")
	public Result isPossible2Submit(String businessid, HttpServletRequest request) {
		Result result = new Result();
		boolean flag = this.preAuditReportService.isPossible2Submit(businessid);
		result.setResult_data(flag);

		return result;
	}
	@RequestMapping("/getByBusinessId")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "基本信息(投标评审报告表)")
	public Result getByBusinessId(String businessId, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.preAuditReportService.getOracleByBusinessId(businessId);
		result.setResult_data(map);
		return result;
	}
}
