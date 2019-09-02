package com.daxt.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daxt.dao.IDaxtMapper;
import com.daxt.service.IDaxtService;
import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.power.service.IOrgService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import com.yk.rcm.decision.serevice.IDecisionService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.meeting.service.IPigeonholeService;
import com.yk.rcm.pre.service.IPreInfoService;
import com.yk.rcm.project.service.IWsCallService;

import common.Constants;
import report.Path;
import util.FileUtil;
import util.Util;

@Service("com.daxt.service.impl.DaxtJob")
@Transactional(value="daxtTxManager",rollbackFor=java.lang.Exception.class)
public class DaxtJob{
	
	@Resource
	private IDecisionService decisionService;
	
	@Resource
	private IOrgService orgService;
	
	@Resource
	private IDaxtMapper daxtMapper;
	
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	
	@Resource
	private IPreInfoService preInfoService;
	
	@Resource
	private IBulletinInfoService bulletinInfoService;
	
	@Resource
	private IBaseMongo baseMongo;
	
	@Resource
	private IDaxtService daxtService;
	
	@Resource
	private IPigeonholeService pigeonholeService;
	
	@Resource
	private IWsCallService wsCallService;
	
	public void execute() {
		List<Map<String, Object>> info = pigeonholeService.queryProject(new Date());
		for (Map<String, Object> map : info) {
			String businessId = map.get("BUSINESSID").toString();
			String projectType = (String) map.get("PROJECT_TYPE");
			try {
				if("pfr".equals(projectType)){
					pfr(businessId);
				}else if("bulletin".equals(projectType)){
					bulletin(businessId);
				}else if("pre".equals(projectType)){
					pre(businessId);
				}
			} catch (Exception e) {
				wsCallService.saveWsServer("DAXT_".concat(projectType.toUpperCase()),"归档出错;businessId:".concat(businessId),Util.parseException(e).replace(" ", ""), false);
			}
		}
	}
	
	/**
	 * 获取正式评审  	基本信息
	 * @param essFkId
	 * @param businessId
	 * @param baseInfo 
	 * @return
	 */
	private Map<String, Object> getPrfInfo(String essFkId,String businessId) {
		Document assessmentInfo = (Document) baseMongo.queryById(businessId,Constants.RCM_FORMALASSESSMENT_INFO);
		Document apply = (Document) assessmentInfo.get("apply");
		Map<String,Object> essFk = new HashMap<String,Object>();
		//主键ID
		essFk.put("ID",essFkId);
		
		//项目名称
		essFk.put("XMMC", apply.getString("projectName"));
				
		//申报单位
		Document reportingUnit = (Document) apply.get("reportingUnit");
		Map<String, Object> orgMap = orgService.queryByPkvalue(reportingUnit.getString("KEY"));
		//ess_fk.put("ZRZ", reportingUnit.getString("VALUE"));
		essFk.put("ZRZ", orgMap.get("NAME"));
		
		//所属战区reportingUnit
		Document pertainAreaDoc = (Document) apply.get("pertainArea");
		orgMap = orgService.queryByPkvalue(pertainAreaDoc.getString("KEY"));
		String pertainArea = null;
		if(null == orgMap){
			orgMap = orgService.queryPertainArea(reportingUnit.getString("KEY"));
		}
		if(null != orgMap){
			pertainArea = orgMap.containsKey("NAME")?orgMap.get("NAME").toString():null;
		}
		if(null == pertainArea){
			throw new RuntimeException("找不到所属战区");
		}
		essFk.put("GSDW",pertainArea);
		
		//项目编码
		essFk.put("XMBM", apply.get("projectNo"));
		
		//经办人
		Document createBy = (Document) apply.get("createBy");
		if(Util.isEmpty(createBy)){
			createBy = (Document) apply.get("createby");
		}
		if(Util.isEmpty(createBy)){
			createBy = (Document) assessmentInfo.get("createBy");
		}
		if(Util.isEmpty(createBy)){
			createBy = (Document) assessmentInfo.get("createby");
		}
		essFk.put("JBR",createBy.getString("NAME"));
		
		//类别	投标评审/正式评审/其他决策事项
		essFk.put("YWLX", "正式评审");
		essFk.put("ZFS", "1");
		return essFk;
	}

	/**
	 * 获取正式评审  	相关资源
	 * @param essFkId
	 * @param businessId
	 * @param newFileFolder
	 * @param index
	 * @return
	 */
	private List<Map<String, Object>> getPrfAttachment(String essFkId,String businessId,String newFileFolder,
			Map<String, Object> baseInfo) {
		Integer index = (Integer) baseInfo.get("index");
		Document assessmentInfo = (Document) baseMongo.queryById(businessId,Constants.RCM_FORMALASSESSMENT_INFO);
		List<Map<String,Object>> essFkDzwjs = new ArrayList<Map<String,Object>>();
		List<Document> attachments = (List<Document>) assessmentInfo.get("attachment");
		for (Document attachment : attachments) {
			//取最新版本的附件
			List<Document> files = (List<Document>) attachment.get("files");
			if(Util.isEmpty(files)){
				continue;
			}
			Document fileDoc = files.get(files.size()-1);
			//旧数据会存在没有  filePath字段
			if(!fileDoc.containsKey("filePath")){
				continue;
			}
			
			Map<String,Object> ess_fk_dzwj = new HashMap<String,Object>();
			ess_fk_dzwj.put("ID", Util.getUUID());
			ess_fk_dzwj.put("FID", essFkId);
			//文件名称
			ess_fk_dzwj.put("WJMC", fileDoc.getString("fileName"));
			//文件类型
			ess_fk_dzwj.put("WJLX", "附件");
			//文件大小
			ess_fk_dzwj.put("WJDX", null);
			//排序
			ess_fk_dzwj.put("WJXH",index++);
			
			//替换文件路径中的斜杠
			String filePath = fileDoc.getString("filePath");
			String srcPath = filePath.replace("\\", File.separator).replace("\\\\", File.separator);
			//原文件名
		    File srcFile = new File(srcPath);
		    String extSuffix = FileUtil.getFileSuffix(srcFile);
			//新文件名
			String targetPath = newFileFolder.concat(Util.getUUID()).concat(".").concat(extSuffix);
			File newFile = new File(targetPath);
			try {
				FileUtil.copyFile(srcFile, newFile);
			} catch (Exception e) {
				wsCallService.saveWsServer("DAXT_PRF_ATTACHMENT","缺失文件;businessId:".concat(businessId).concat(",src:").concat(srcPath).
						concat(",target:").concat(targetPath),Util.parseException(e).replace(" ", ""), false);
				baseInfo.put("status","3");
			}
			//电子文件
			targetPath = targetPath.substring(Path.daxtPathRoot().length());
			ess_fk_dzwj.put("YWLJ", targetPath);
			
			essFkDzwjs.add(ess_fk_dzwj);
		}
		return essFkDzwjs;
	}
	
	/**
	 * 获取正式评审  	项目报告
	 * @param businessId
	 * @param newFileFolder
	 * @param essFkId
	 * @param index
	 * @return
	 */
	private Map<String, Object> getPrfReportAttachment(String essFkId,String businessId,String newFileFolder,Map<String, Object> baseInfo) {
		BasicDBObject queryWhere = new BasicDBObject();
		queryWhere.put("projectFormalId", businessId);
		List<Map<String, Object>> formalReports = baseMongo.queryByCondition(queryWhere,Constants.RCM_FORMALREPORT_INFO);
		if(Util.isEmpty(formalReports)){
			return null;
		}
		Integer index = (Integer) baseInfo.get("index");
		Map<String, Object> formalReport = formalReports.get(0);
		
		Map<String,Object> ess_fk_dzwj = new HashMap<String,Object>();
		ess_fk_dzwj.put("ID", Util.getUUID());
		ess_fk_dzwj.put("FID", essFkId);
		//文件名称
		Object fileName = formalReport.get("fileName");
		String filePath = formalReport.get("filePath").toString();
		if(Util.isEmpty(fileName)){
			String projectName = formalReport.get("projectName").toString();
			String suffix = filePath.substring(filePath.lastIndexOf("."));
			fileName = projectName.substring(0, projectName.length() > 15 ? 15:projectName.length()).concat("报告").concat(suffix);
		}
		
		ess_fk_dzwj.put("WJMC", fileName);
		//文件类型
		ess_fk_dzwj.put("WJLX", "报告");
		//文件大小
		ess_fk_dzwj.put("WJDX", null);
		//排序
		ess_fk_dzwj.put("WJXH",index++);
		//替换文件路径中的斜杠
		String srcPath = filePath.replace("\\", File.separator).replace("\\\\", File.separator);
		//原文件名
	    File srcFile = new File(srcPath);
	    String extSuffix = FileUtil.getFileSuffix(srcFile);
		//新文件名
		String targetPath = newFileFolder.concat(Util.getUUID()).concat(".").concat(extSuffix);
		File newFile = new File(targetPath);
		try {
			FileUtil.copyFile(srcFile, newFile);
		} catch (Exception e) {
			wsCallService.saveWsServer("DAXT_PRF_REPORT","缺失文件;businessId:".concat(businessId).concat(",src:").concat(srcPath).
					concat(",target:").concat(targetPath),Util.parseException(e).replace(" ", ""), false);
			baseInfo.put("status","3");
		}
		//电子文件
		targetPath = targetPath.substring(Path.daxtPathRoot().length());
		ess_fk_dzwj.put("YWLJ", targetPath);
		return ess_fk_dzwj;
	}
	
	/**
	 * 获取正式评审  	决策通知书
	 * @param essFkId
	 * @param businessId
	 * @param newFileFolder
	 * @param index
	 * @return
	 */
	private Map<String, Object> getPrfNoticeDecisionAttachment(
			String essFkId,String businessId,String newFileFolder,Map<String, Object> baseInfo) {
		BasicDBObject queryWhere = new BasicDBObject();
		queryWhere.put("projectFormalId", businessId);
		List<Map<String, Object>> queryByCondition = baseMongo.queryByCondition(queryWhere,Constants.RCM_NOTICEDECISION_INFO);
		if(Util.isEmpty(queryByCondition)){
			return null;
		}
		
		Map<String, Object> noticeDecision = queryByCondition.get(0);
		List<Map<String, Object>> attachmentObjects = (List<Map<String, Object>>) noticeDecision.get("attachment");
		if(Util.isEmpty(attachmentObjects)){
			return null;
		}
		Integer index = (Integer) baseInfo.get("index");
		Map<String, Object> attachmentObject = attachmentObjects.get(0);
		
		HashMap<String,Object> essFkDzwj = new HashMap<String,Object>();
		essFkDzwj.put("ID", Util.getUUID());
		essFkDzwj.put("FID", essFkId);
		//文件名称
		essFkDzwj.put("WJMC", attachmentObject.get("fileName"));
		//文件类型
		essFkDzwj.put("WJLX", "决策通知书");
		//文件大小
		essFkDzwj.put("WJDX", null);
		//排序
		essFkDzwj.put("WJXH",index++);
		//替换文件路径中的斜杠
		String filePath = attachmentObject.get("filePath").toString();
		String srcPath = filePath.replace("\\", File.separator).replace("\\\\", File.separator);
		//原文件名
		File srcFile = new File(srcPath);
	    String extSuffix = FileUtil.getFileSuffix(srcFile);
		//新文件名
		String targetPath = newFileFolder.concat(Util.getUUID()).concat(".").concat(extSuffix);
		File newFile = new File(targetPath);
		try {
			FileUtil.copyFile(srcFile, newFile);
		} catch (Exception e) {
			wsCallService.saveWsServer("DAXT_PRF_NOTICED","缺失文件;businessId:".concat(businessId).concat(",src:").concat(srcPath).
					concat(",target:").concat(targetPath),Util.parseException(e).replace(" ", ""), false);
			baseInfo.put("status","3");
		}
		//电子文件
		targetPath = targetPath.substring(Path.daxtPathRoot().length());
		essFkDzwj.put("YWLJ", targetPath);
		return essFkDzwj;
	}
	
	/**
	 * 其他决策事项  流程附件
	 */
	private List<Map<String,Object>> getBulletinAttachment(String essFkId,
			String businessId, String newFileFolder, Map<String,Object> baseInfo) {
		Integer index = (Integer) baseInfo.get("index");
		Document bulletinInfo = (Document) baseMongo.queryById(businessId,Constants.RCM_BULLETIN_INFO);
		List<Map<String,Object>> essFkDzwjs = new ArrayList<Map<String,Object>>();
		List<Document> attachments = (List<Document>) bulletinInfo.get("fileList");
		for (Document attachment : attachments) {
			//取最新版本的附件
			Document file = (Document) attachment.get("files");
			if(Util.isEmpty(file)){
				continue;
			}
			
			Map<String,Object> ess_fk_dzwj = new HashMap<String,Object>();
			ess_fk_dzwj.put("ID", Util.getUUID());
			ess_fk_dzwj.put("FID", essFkId);
			//文件名称
			ess_fk_dzwj.put("WJMC", file.getString("fileName"));
			//文件类型
			ess_fk_dzwj.put("WJLX", "附件");
			//文件大小
			ess_fk_dzwj.put("WJDX", null);
			//排序
			ess_fk_dzwj.put("WJXH",index++);
			
			//替换文件路径中的斜杠
			String filePath = file.getString("filePath");
			String srcPath = filePath.replace("\\", File.separator).replace("\\\\", File.separator);
			//原文件名
		    File srcFile = new File(srcPath);
		    String extSuffix = FileUtil.getFileSuffix(srcFile);
			//新文件名
			String targetPath = newFileFolder.concat(Util.getUUID()).concat(".").concat(extSuffix);
			File newFile = new File(targetPath);
			try {
				FileUtil.copyFile(srcFile, newFile);
			} catch (Exception e) {
				wsCallService.saveWsServer("DAXT_BULLETIN_ATTACHMENT","缺失文件;businessId:".concat(businessId).concat(",src:").concat(srcPath).
						concat(",target:").concat(targetPath),Util.parseException(e).replace(" ", ""), false);
				baseInfo.put("status","3");
			}
			//电子文件
			targetPath = targetPath.substring(Path.daxtPathRoot().length());
			ess_fk_dzwj.put("YWLJ", targetPath);
			
			essFkDzwjs.add(ess_fk_dzwj);
		}
		return essFkDzwjs;
	}

	/**
	 * 其他决策事项  基本信息
	 */
	private Map<String, Object> getBulletinInfo(String essFkId,
			String businessId) {
		Document bulletinInfo = (Document) baseMongo.queryById(businessId,Constants.RCM_BULLETIN_INFO);
		Map<String,Object> essFk = new HashMap<String,Object>();
		//主键ID
		essFk.put("ID",essFkId);
		
		//项目名称
		essFk.put("XMMC", bulletinInfo.getString("bulletinName"));
				
		//申报单位
		Document reportingUnit = (Document) bulletinInfo.get("applyUnit");
		Map<String, Object> orgMap = orgService.queryByPkvalue(reportingUnit.getString("VALUE"));
		if(null == orgMap){
			essFk.put("ZRZ", reportingUnit.getString("NAME"));
		}else{
			essFk.put("ZRZ", orgMap.get("NAME"));
		}
		
		//所属战区
		orgMap = orgService.queryPertainArea(reportingUnit.getString("VALUE"));
		if(null == orgMap){
			throw new RuntimeException("找不到所属战区");
		}else{
			essFk.put("GSDW", orgMap.get("NAME"));
		}
		
		//项目编码
		essFk.put("XMBM",null);
		
		//经办人
		Document applyUser = (Document) bulletinInfo.get("applyUser");
		essFk.put("JBR",applyUser.getString("NAME"));
		
		//类别	投标评审/正式评审/其他决策事项
		essFk.put("YWLX", "其他决策事项");
		essFk.put("ZFS", "1");
		return essFk;
	}

	public void pfr(String businessId) {
		Map<String, Object> prfInfo = formalAssessmentInfoService.getOracleByBusinessId(businessId);
		String newFileFolder = Path.daxtPath()+prfInfo.get("PROJECTNUM")+"/";
		
		//公共信息
		Map<String,Object> baseInfo = new HashMap<String, Object>(1);
		Integer index = 1;
		baseInfo.put("index", index);
		baseInfo.put("status","2");
		
		//条目信息
		String essFkId = businessId;
		Map<String,Object> essFk = getPrfInfo(essFkId,businessId);
		
		//文件信息
		List<Map<String,Object>> essFkDzwjs = getPrfAttachment(essFkId,businessId,newFileFolder,baseInfo);
		Map<String,Object> reportEssFkDzwj = getPrfReportAttachment(essFkId,businessId,newFileFolder,baseInfo);
		if(null != reportEssFkDzwj){
			essFkDzwjs.add(reportEssFkDzwj);
		}
		
		Map<String,Object> decisionEssFkDzwj = getPrfNoticeDecisionAttachment(essFkId,businessId,newFileFolder,baseInfo);
		if(null != decisionEssFkDzwj){
			essFkDzwjs.add(decisionEssFkDzwj);
		}
		//记录电子文件份数
		essFk.put("FJFS", String.valueOf(essFkDzwjs.size()));
		//先删除已有的(essFkId:来自风控系统业务ID)
		daxtMapper.deleteESSFK(essFkId);
		daxtMapper.deleteESSFKDZWJ(essFkId);
		//归档
		daxtMapper.addEssFk(essFk);
		if(essFkDzwjs.size() > 0){
		daxtMapper.addDssFkDzwj(essFkDzwjs);
		}
		//刷新风控系统归档状态
		formalAssessmentInfoService.updatePigeStatByBusiId(businessId,(String)baseInfo.get("status"));
	}

	/**
	 * 获取正式评审  	流程附件
	 * @param essFkId
	 * @param businessId
	 * @param newFileFolder
	 * @param index
	 * @return
	 */
	private List<Map<String, Object>> getPreAttachment(String essFkId,
			String businessId, String newFileFolder, Map<String, Object> baseInfo) {
		Integer index = (Integer) baseInfo.get("index");
		Document assessmentInfo = (Document) baseMongo.queryById(businessId,Constants.RCM_PRE_INFO);
		List<Map<String,Object>> essFkDzwjs = new ArrayList<Map<String,Object>>();
		List<Document> attachments = (List<Document>) assessmentInfo.get("attachment");
		for (Document attachment : attachments) {
			//取最新版本的附件
			List<Document> files = (List<Document>) attachment.get("files");
			if(Util.isEmpty(files)){
				continue;
			}
			Document fileDoc = files.get(files.size()-1);
			//旧数据会存在没有  filePath字段
			if(!fileDoc.containsKey("filePath")){
				continue;
			}
			
			Map<String,Object> ess_fk_dzwj = new HashMap<String,Object>();
			ess_fk_dzwj.put("ID", Util.getUUID());
			ess_fk_dzwj.put("FID", essFkId);
			//文件名称
			ess_fk_dzwj.put("WJMC", fileDoc.getString("fileName"));
			//文件类型
			ess_fk_dzwj.put("WJLX", "附件");
			//文件大小
			ess_fk_dzwj.put("WJDX", null);
			//排序
			ess_fk_dzwj.put("WJXH",index++);
			
			//替换文件路径中的斜杠
			String filePath = fileDoc.getString("filePath");
			String srcPath = filePath.replace("\\", File.separator).replace("\\\\", File.separator);
			//原文件名
		    File srcFile = new File(srcPath);
		    String extSuffix = FileUtil.getFileSuffix(srcFile);
			//新文件名
			String targetPath = newFileFolder.concat(Util.getUUID()).concat(".").concat(extSuffix);
			File newFile = new File(targetPath);
			try {
				FileUtil.copyFile(srcFile, newFile);
			} catch (Exception e) {
				wsCallService.saveWsServer("DAXT_PRE_ATTACHMENT","缺失文件;businessId:".concat(businessId).concat(",src:").concat(srcPath).
						concat(",target:").concat(targetPath),Util.parseException(e).replace(" ", ""), false);
				baseInfo.put("status","3");
			}
			//电子文件
			targetPath = targetPath.substring(Path.daxtPathRoot().length());
			ess_fk_dzwj.put("YWLJ", targetPath);
			
			essFkDzwjs.add(ess_fk_dzwj);
		}
		return essFkDzwjs;
	}

	/**
	 * 获取正式评审  	基本信息
	 * @param essFkId
	 * @param businessId
	 * @return
	 */
	private Map<String, Object> getPreInfo(String essFkId,
			String businessId) {
		Document assessmentInfo = (Document) baseMongo.queryById(businessId,Constants.RCM_PRE_INFO);
		Document apply = (Document) assessmentInfo.get("apply");
		Map<String,Object> essFk = new HashMap<String,Object>();
		//主键ID
		essFk.put("ID",essFkId);
		
		//项目名称
		essFk.put("XMMC", apply.getString("projectName"));
				
		//申报单位
		Document reportingUnit = (Document) apply.get("reportingUnit");
		Map<String, Object> orgMap = orgService.queryByPkvalue(reportingUnit.getString("KEY"));
		if(null == orgMap){
			essFk.put("ZRZ", reportingUnit.getString("VALUE"));
		}else{
			essFk.put("ZRZ", orgMap.get("NAME"));
		}
		
		//所属战区
		Document pertainArea = (Document) apply.get("pertainArea");
		orgMap = orgService.queryByPkvalue(pertainArea.getString("KEY"));
		if(null == orgMap){
			essFk.put("GSDW", pertainArea.get("VALUE"));
		}else{
			essFk.put("GSDW", orgMap.get("NAME"));
		}
		if(Util.isEmpty(essFk.get("GSDW"))){
			throw new RuntimeException("找不到所属战区");
		}
		
		//项目编码
		essFk.put("XMBM", apply.get("projectNo"));
		
		//经办人
		Document createBy = (Document) apply.get("createby");
		if(Util.isEmpty(createBy)){
			createBy = (Document) apply.get("createBy");
		}
		if(Util.isEmpty(createBy)){
			createBy = (Document) assessmentInfo.get("createBy");
		}
		if(Util.isEmpty(createBy)){
			createBy = (Document) assessmentInfo.get("createby");
		}
		essFk.put("JBR",createBy.getString("NAME"));
		
		//类别	投标评审/正式评审/其他决策事项
		essFk.put("YWLX", "投标评审");
		essFk.put("ZFS", "1");
		return essFk;
	}
	
	/**
	 * 获取投标评审  	项目报告
	 * @param businessId
	 * @param newFileFolder
	 * @param essFkId
	 * @param index
	 * @return
	 */
	private Map<String, Object> getPreReportAttachment(String essFkId,String businessId,String newFileFolder, Map<String, Object> baseInfo) {
		Map<String, Object> formalReport = baseMongo.queryById(businessId,Constants.RCM_PRE_INFO);
		if(!formalReport.containsKey("reviewReport")){
			return null;
		}
		Map<String, Object> reviewReport = (Map<String, Object>) formalReport.get("reviewReport");
		Object filePathObject = reviewReport.get("filePath");
		if(null == filePathObject)
			return null;
		
		Integer index = (Integer) baseInfo.get("index");
		Map<String,Object> ess_fk_dzwj = new HashMap<String,Object>();
		ess_fk_dzwj.put("ID", Util.getUUID());
		ess_fk_dzwj.put("FID", essFkId);
		//文件名称
		ess_fk_dzwj.put("WJMC", reviewReport.get("fileName"));
		//文件类型
		ess_fk_dzwj.put("WJLX", "报告");
		//文件大小
		ess_fk_dzwj.put("WJDX", null);
		//排序
		ess_fk_dzwj.put("WJXH",index++);
		//替换文件路径中的斜杠
		String filePath = filePathObject.toString();
		String srcPath = filePath.replace("\\", File.separator).replace("\\\\", File.separator);
		//原文件名
	    File srcFile = new File(srcPath);
	    String extSuffix = FileUtil.getFileSuffix(srcFile);
		//新文件名
		String targetPath = newFileFolder.concat(Util.getUUID()).concat(".").concat(extSuffix);
		File newFile = new File(targetPath);
		try {
			FileUtil.copyFile(srcFile, newFile);
		} catch (Exception e) {
			wsCallService.saveWsServer("DAXT_PRE_REPORT","缺失文件;businessId:".concat(businessId).concat(",src:").concat(srcPath).
					concat(",target:").concat(targetPath),Util.parseException(e).replace(" ", ""), false);
			baseInfo.put("status","3");
		}
		//电子文件
		targetPath = targetPath.substring(Path.daxtPathRoot().length());
		ess_fk_dzwj.put("YWLJ", targetPath);
		return ess_fk_dzwj;
	}

	public void pre(String businessId) {
		Map<String, Object> prfInfo = preInfoService.getOracleByBusinessId(businessId);
		String newFileFolder = Path.daxtPath()+prfInfo.get("PROJECTNUM")+"/";
		
		//公共信息
		Map<String,Object> baseInfo = new HashMap<String, Object>(1);
		Integer index = 1;
		baseInfo.put("index", index);
		baseInfo.put("status","2");
		
		//条目信息
		String essFkId = businessId;
		Map<String,Object> essFk = getPreInfo(essFkId,businessId);
		
		//文件信息
		List<Map<String,Object>> essFkDzwjs = getPreAttachment(essFkId,businessId,newFileFolder,baseInfo);
		Map<String, Object> reportEssFkDzwj = getPreReportAttachment(essFkId,businessId,newFileFolder,baseInfo);
		if(null != reportEssFkDzwj){
			essFkDzwjs.add(reportEssFkDzwj);
		}
		//记录电子文件份数
		essFk.put("FJFS", essFkDzwjs.size());
		//先删除已有的(essFkId:来自风控系统业务ID)
		daxtMapper.deleteESSFK(essFkId);
		daxtMapper.deleteESSFKDZWJ(essFkId);
		//归档
		daxtMapper.addEssFk(essFk);
		if(essFkDzwjs.size() > 0){
			daxtMapper.addDssFkDzwj(essFkDzwjs);
		}
		//刷新风控系统归档状态
		preInfoService.updatePigeStatByBusiId(businessId,(String)baseInfo.get("status"));
	}

	public void bulletin(String businessId) {
		String newFileFolder = Path.daxtPath()+businessId+"/";
		//公共信息
		Map<String,Object> baseInfo = new HashMap<String, Object>(1);
		Integer index = 1;
		baseInfo.put("index", index);
		baseInfo.put("status","2");
		
		//条目信息
		String essFkId = businessId;
		Map<String,Object> essFk = getBulletinInfo(essFkId,businessId);
		
		//文件信息
		List<Map<String, Object>> essFkDzwjs = getBulletinAttachment(essFkId,businessId,newFileFolder,baseInfo);
		
		//记录电子文件份数
		essFk.put("FJFS", essFkDzwjs.size());
		
		//先删除已有的(essFkId:来自风控系统业务ID)
		daxtMapper.deleteESSFK(essFkId);
		daxtMapper.deleteESSFKDZWJ(essFkId);
		//归档
		daxtMapper.addEssFk(essFk);
		if(essFkDzwjs.size() > 0){
			daxtMapper.addDssFkDzwj(essFkDzwjs);
		}
		//刷新风控系统归档状态
		bulletinInfoService.updatePigeStatByBusiId(businessId,(String)baseInfo.get("status"));
	}
}
