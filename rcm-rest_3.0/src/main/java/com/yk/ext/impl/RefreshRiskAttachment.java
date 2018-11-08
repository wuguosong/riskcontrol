package com.yk.ext.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.pre.service.IPreInfoService;

import common.Constants;

/**
 * 
 * 迁移风控附件
 * 
 * @author yaphet
 *
 */
@Service("refreshRiskAttachment")
@Transactional
public class RefreshRiskAttachment implements Initable {
	
	@Resource
	private IBaseMongo baseMongo;
	
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	@Resource
	private IPreInfoService preInfoService;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		
		List<Map<String, Object>> formalList = formalAssessmentInfoService.queryByStageAndstate(null, "1,2");
		
		for (Map<String, Object> formal : formalList) {
			String businessId = (String) formal.get("BUSINESSID");
			BasicDBObject queryWhere = new BasicDBObject();
			queryWhere.put("projectFormalId", businessId);
			List<Map<String, Object>> noticeList = this.baseMongo.queryByCondition(queryWhere, Constants.RCM_NOTICEDECISION_INFO);
			if(Util.isNotEmpty(noticeList)){
				Map<String, Object> notice = noticeList.get(0);
				List<Map<String, Object>> riskAttachment = (List<Map<String, Object>>) notice.get("attachment");
				if(Util.isNotEmpty(riskAttachment)){
					Map<String, Object> map = new HashMap<String,Object>();
					map.put("riskAttachment", riskAttachment);
					this.baseMongo.updateSetByObjectId(businessId, map, Constants.RCM_FORMALASSESSMENT_INFO);
				}
			}
			
			List<Map<String, Object>> reportList = this.baseMongo.queryByCondition(queryWhere, Constants.RCM_FORMALREPORT_INFO);
			if(Util.isNotEmpty(reportList)){
				Map<String, Object> report = reportList.get(0);
				Map<String, Object> policyDecision = (Map<String, Object>) report.get("policyDecision");
				if(Util.isNotEmpty(policyDecision)){
					List<Map<String, Object>> meetingFiles = (List<Map<String, Object>>) policyDecision.get("decisionMakingCommitteeStaffFiles");
					if(Util.isNotEmpty(meetingFiles)){
						Map<String, Object> map = new HashMap<String,Object>();
						map.put("meetingFiles", meetingFiles);
						this.baseMongo.updateSetByObjectId(businessId, map, Constants.RCM_FORMALASSESSMENT_INFO);
					}
				}
			}
		}
		
		List<Map<String, Object>> preList = preInfoService.queryByStageAndstate(null, "1,2,3");
		
		for (Map<String, Object> pre : preList) {
			
			String businessId = (String) pre.get("BUSINESSID");
			
			Map<String, Object> preMongo = this.baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
			
			if(Util.isEmpty(preMongo)){
				continue;
			}
			
			Map<String, Object> policyDecision = (Map<String, Object>) preMongo.get("policyDecision");
			
			if(Util.isEmpty(policyDecision)){
				continue;
			}
			
			List<Map<String, Object>> fileList = (List<Map<String, Object>>)  policyDecision.get("fileList");
			
			if(Util.isNotEmpty(fileList)){
				List<Map<String, Object>> newFileList = new ArrayList<Map<String, Object>>();
				
				for (Map<String, Object> files : fileList) {
					
					Map<String, Object> file = (Map<String, Object>) files.get("files");
					
					if(Util.isEmpty(file)){
						continue;
					}
					Map<String, Object> map = new HashMap<String,Object>();
					
					map.put("UUID", (String)file.get("UUID"));
					map.put("fileName", (String)file.get("fileName"));
					map.put("filePath", (String)file.get("filePath"));
					newFileList.add(map);
				}
				HashMap<String, Object> riskFiles = new HashMap<String,Object>();
				riskFiles.put("riskAttachment", newFileList);
				this.baseMongo.updateSetByObjectId(businessId, riskFiles, Constants.RCM_PRE_INFO);
			}
			
			List<Map<String, Object>> meetingFiles = (List<Map<String, Object>>)  policyDecision.get("decisionMakingCommitteeStaffFiles");
			
			if(Util.isNotEmpty(meetingFiles)){
				HashMap<String, Object> riskFiles = new HashMap<String,Object>();
				riskFiles.put("meetingFiles", meetingFiles);
				this.baseMongo.updateSetByObjectId(businessId, riskFiles, Constants.RCM_PRE_INFO);
			}
			
		}
		
	}
}
