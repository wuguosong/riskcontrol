package com.yk.ext.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.daxt.dao.IDaxtMapper;
import com.daxt.service.IDaxtService;
import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;
import com.yk.power.service.IDictService;
import com.yk.power.service.IOrgService;
import com.yk.rcm.bulletin.service.IBulletinAuditLogService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import com.yk.rcm.decision.serevice.IDecisionService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionInfoService;
import com.yk.rcm.pre.service.IPreInfoService;

/**
 * 
 * 将会议委员中会议主席刷到专门的  按照顺序  存储到会议主席字段中
 * @author hubiao
 *
 */
@Service("refreshDaxt")
@Transactional
public class RefreshDaxt implements Initable {

	@Resource
	private IBaseMongo baseMongo;
	
	@Resource
	private IDaxtService daxtService;
	
	@Resource
	public IFormalAssessmentInfoService formalAssessmentInfoService;
	
	@Resource
	private INoticeDecisionInfoService noticeDecisionInfoService;
	
	@Resource
	private IDecisionService decisionService;
	
	@Resource
	private IOrgService orgService;
	
	@Resource
	private IDaxtMapper daxtMapper;
	
	@Resource
	private IBulletinInfoService bulletinInfoService;
	
	@Resource
	private IBulletinAuditLogService bulletinAuditLogService;
	
	@Resource
	private IPreInfoService preInfoService;
	
	@Resource
	private IDictService dictService;
	
	
	@Override
	public void execute() {
		//正式
		List<Map<String,Object>> pfrs = formalAssessmentInfoService.queryAllByDaxt();
		for (Map<String, Object> pfr : pfrs) {
			String businessId = pfr.get("BUSINESSID").toString();
			String stage = pfr.get("STAGE").toString();
			
			//流程中止
			if("3".equals(pfr.get("WF_STATE"))){
				Date today = (Date) pfr.get("COMPLETE_DATE");
				if(null == today){
					continue;
				}
				daxtService.SRSM006(today,businessId);
			//无需上会
			}else if("9".equals(stage)){
				Date today = (Date) pfr.get("COMPLETE_DATE");
				if(null == today){
					continue;
				}
				daxtService.SRSM002(today,businessId);
			//确认决策通知书
			}else if("7".equals(stage)){
				Map<String, Object> decisionInfo = decisionService.queryByBusinessId(businessId);
				String decisionResult = decisionInfo.get("DECISION_RESULT").toString();
				//无合同签订
				if("2".equals(decisionResult) || "4".equals(decisionResult)){
					//取完决策时间做为开始时间
					Date today = (Date) decisionInfo.get("DECISION_DATE");
					daxtService.SRSM003(today,businessId);
				}else{
					//需要签订合同，是否已签订合同?
					List<Map<String, Object>> contractInfo = formalAssessmentInfoService.queryContractByBusinessId(businessId);
					if(Util.isNotEmpty(contractInfo)){
						//取完成决策通知书做为开始时间
						Date today = (Date) pfr.get("NOTICE_CONFIRM_TIME");
						daxtService.SRSM001(today,businessId);
					}
					else{
						//取完决策时间做为开始时间
						Date today = (Date) decisionInfo.get("DECISION_DATE");
						//需要签订合同，还没签订合同，设置合同的等待最大时间
						daxtService.SRSM003(today,businessId);
					}
				}
			}
		}
		
		//投标
		List<Map<String,Object>> pres = preInfoService.queryAllByDaxt();
		for (Map<String, Object> pre : pres) {
			String businessId = pre.get("BUSINESSID").toString();
			String stage = pre.get("STAGE").toString();
			//流程终止或无需上会
			if("3".equals(pre.get("WF_STATE")) || "9".equals(stage)){
				Date today = (Date) pre.get("COMPLETE_DATE");
				if(null == today){
					continue;
				}
				daxtService.SRSM007(today,businessId);
			//已表决
			}else if("6".equals(stage)){
				Map<String, Object> decisionInfo = decisionService.queryByBusinessId(businessId);
				Date today = (Date) decisionInfo.get("DECISION_DATE");
				daxtService.SRSM005(today,businessId);
			}
		}
		
		//其他事项
		List<Map<String,Object>> bulletins = bulletinInfoService.queryAllByDaxt();
		for (Map<String, Object> bulletin : bulletins) {
			String businessId = bulletin.get("BUSINESSID").toString();
			String stage = bulletin.get("STAGE").toString();
			//流程终止或无需上会,则将流程审批日志按审核时间降序排序取第1个做为归档的开始时间
			if("3".equals(bulletin.get("AUDITSTATUS")) || "9".equals(stage)){
				Map<String, Object> bulletinLog = bulletinAuditLogService.queryLastLogsByDaxt(businessId);
				Date today = null;
				if(Util.isEmpty(bulletinLog)){
					today = (Date) bulletin.get("APPLYTIME");
				}else{
					today = (Date) bulletinLog.get("AUDITTIME");
				}
				daxtService.SRSM008(today,businessId);
			//出会议纪要
			}else if("5".equals(stage)){
				Date today = (Date) bulletin.get("METTINGSUMMARYTIME");
				daxtService.SRSM004(today,businessId);
			}
		}
	}
}
