/**
 * 
 */
package com.yk.rcm.noticeofdecision.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.PropertiesUtil;
import util.ThreadLocalUtil;
import util.Util;
import ws.client.HpgInfoClient;
import ws.client.TzAfterNoticeClient;
import ws.client.contract.SendProjectThread;

import com.daxt.service.IDaxtService;
import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentInfoMapper;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.formalAssessment.service.IFormalReportService;
import com.yk.rcm.noticeofdecision.dao.INoticeDecisionConfirmInfoMapper;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionConfirmInfoService;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionDraftInfoService;

import common.Constants;
import common.PageAssistant;

/**
 * @author wufucan
 *
 */
@Service
@Transactional
public class NoticeDecisionConfirmInfoService implements
		INoticeDecisionConfirmInfoService {
	@Resource
	private INoticeDecisionConfirmInfoMapper noticeDecisionConfirmInfoMapper;
	@Resource
	private IFormalAssessmentInfoMapper formalAssessmentInfoMapper;
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private INoticeDecisionDraftInfoService noticeDecisionDraftInfoService;
	@Resource
	private IFormalReportService formalReportService;
	@Resource
	private IDaxtService daxtService;
	/* (non-Javadoc)
	 * @see com.yk.rcm.noticeofdecision.service.INoticeDecisionConfirmInfoService#queryWaitConfirm(common.PageAssistant)
	 */
	@Override
	public void queryWaitConfirm(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("userId", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " apply_date desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.noticeDecisionConfirmInfoMapper.queryWaitConfirm(params);
		page.setList(list);
	}

	/* (non-Javadoc)
	 * @see com.yk.rcm.noticeofdecision.service.INoticeDecisionConfirmInfoService#queryConfirmed(common.PageAssistant)
	 */
	@Override
	public void queryConfirmed(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " create_date desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.noticeDecisionConfirmInfoMapper.queryConfirmed(params);
		page.setList(list);

	}

	@Override
	public void confirm(Map<String, Object> data) {
		String formalId = (String) data.get("formalId");
		Document attachment = (Document) data.get("attachment");
		String noticeInfoStr = (String) data.get("noticeInfo");
		Document noticeInfo = (Document)Document.parse(noticeInfoStr);
		//0、保存决策通知书基本信息
		this.noticeDecisionDraftInfoService.update(noticeInfoStr);
		//1、修改正式评审的阶段，6改成7
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("stage", "7");
		map.put("notice_confirm_time",Util.getTime());
		map.put("businessId", formalId);
		this.noticeDecisionConfirmInfoMapper.updateStageConfirmTime(map);
		//2、保存决策通知书附件，即上传的决策通知书
		ArrayList<Document> list = new ArrayList<Document>();
		list.add(attachment);
		data.put("attachment", list);
		BasicDBObject filter =new BasicDBObject();
		filter.put("projectFormalId", formalId);
		this.baseMongo.updateSetByFilter(filter, data, Constants.RCM_NOTICEDECISION_INFO);
		//3、如果是同意投资或有条件同意投资，需要向合同系统推送数据
		String decisionResult = (String) noticeInfo.get("consentToInvestment");
		if("1".equals(decisionResult) || "3".equals(decisionResult)){
			String businessId = noticeInfo.get("_id").toString();
			SendProjectThread sendProject = new SendProjectThread(businessId);
			new Thread(sendProject).start();
		}
		
		HpgInfoClient hpgInfoClient = new HpgInfoClient(formalId);
		Thread thread = new Thread(hpgInfoClient);
		thread.start();
		
		String filepath = "", ext = "",fileName="";
		if (attachment != null) {
			filepath = attachment.getString("filePath");
			fileName = attachment.getString("fileName");
			if (filepath != null) {
				ext = filepath.substring(filepath.lastIndexOf("."));
			}
		}

		String serverPath = PropertiesUtil.getProperty("domain.allow") + PropertiesUtil.getProperty("contextPath");
		serverPath = serverPath + "/common/RcmFile/downLoad?filePath=" + filepath + "&fileName="+fileName;

		TzAfterNoticeClient tzAfterNoticeClient = new TzAfterNoticeClient(formalId, decisionResult, serverPath);
		Thread t = new Thread(tzAfterNoticeClient);
		t.start();
		
		this.daxtService.prfStart(formalId);
		
	}

}
