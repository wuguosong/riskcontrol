package com.yk.rcm.decision.serevice.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daxt.service.IDaxtService;
import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.power.service.IDictService;
import com.yk.power.service.IRoleService;
import com.yk.power.service.IUserService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import com.yk.rcm.decision.dao.IDecisionMapper;
import com.yk.rcm.decision.serevice.IDecisionService;
import com.yk.rcm.decision.serevice.IMeetingIssueService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionInfoService;
import com.yk.rcm.pre.service.IPreInfoService;

import common.Constants;
import util.ThreadLocalUtil;
import util.Util;
import ws.client.TzAfterNoticeClient;
import ws.client.TzAfterPreReviewClient;

@Service
@Transactional
public class DecisionServiceImpl implements IDecisionService{

	@Resource
	public IDecisionMapper decisionMapper;
	
	@Resource
	public IFormalAssessmentAuditService assessmentAuditService;
	
	@Resource
	public IBulletinInfoService bulletinInfoService;
	
	@Resource
	public IPreInfoService preInfoService;
	
	@Resource
	public IUserService userService;
	
	@Resource
	public IRoleService roleService;
	
	@Resource
	public IBaseMongo baseMongo;
	
	@Resource
	private IDictService dictService;
	
	@Resource
	private IMeetingIssueService meetingIssueService;
	
	@Resource
	private INoticeDecisionInfoService noticeDecisionInfoService;
	
	@Resource
	private IDaxtService daxtService;
	
	@Override
	public List<Map<String, Object>> queryList() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("meetingTime", new Date());
		List<Map<String, Object>> resultData = decisionMapper.queryList(params);
		return resultData;
	}

	@Override
	public void updateVoteStatus(String id, int status) {
		decisionMapper.updateVoteStatus(id,status);
	}

	@Override
	public Map<String, Object> getCurrUnderwayProject() {
		BasicDBObject queryAndWhere = new BasicDBObject();
		//获取当前用户
		String userId = ThreadLocalUtil.getUserId();
		//获取当前决策项目
		Map<String, Object> resultData = decisionMapper.getCurrUnderwayProject();
		if(null == resultData)
			return null;
		
		//项目类型(0:评审项目,1:通报项目)
		String formalId = resultData.get("FORMAL_ID").toString();
		int formalType = Integer.parseInt(resultData.get("FORMAL_TYPE").toString());

		Map meetingData = null;
		List<Map<String, Object>> queryData = null;
		List<Map<String,Object>> decisionOpinionList = null;
		switch (formalType) {
		case 0:
			queryAndWhere.put("formalId", formalId);
			queryAndWhere.put("decisionMakingCommitteeStaff.VALUE", userId);
			queryData = baseMongo.queryByCondition(queryAndWhere, Constants.FORMAL_MEETING_INFO);
			if(null == queryData || 0 == queryData.size())
				return null;
			
			resultData.put("project_name", queryData.get(0).get("projectName"));
			if(userId.equals(queryData.get(0).get("jueCeHuiYiZhuXiId"))) {
				resultData.put("isZhuXi", "1");
			} else {
				resultData.put("isZhuXi", "0");
			}
			decisionOpinionList = (List<Map<String, Object>>) queryData.get(0).get("decisionOpinionList");
			break;
		case 1:
			queryAndWhere.put("_id", formalId);
			queryAndWhere.put("meeting.meetingLeaders.VALUE", userId);
			queryData = baseMongo.queryByCondition(queryAndWhere, Constants.RCM_BULLETIN_INFO);
			if(null == queryData || 0 == queryData.size())
				return null;
			
			resultData.put("project_name", queryData.get(0).get("bulletinName"));
			meetingData = (Map) queryData.get(0).get("meeting");
			decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
			break;
		case 2:
			queryAndWhere.put("_id", new ObjectId(formalId));
			queryAndWhere.put("meetingInfo.meetingLeaders.VALUE", userId);
			queryData = baseMongo.queryByCondition(queryAndWhere, Constants.RCM_PRE_INFO);
			if(null == queryData || 0 == queryData.size())
				return null;
			
			Map apply = (Map) queryData.get(0).get("apply");
			resultData.put("project_name", apply.get("projectName"));
			meetingData = (Map) queryData.get(0).get("meetingInfo");
			decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
			break;
		}
		//如果当前用户已表决，则直接跳转到等待页
		if(null != decisionOpinionList){
			for (Map<String, Object> map : decisionOpinionList) {
				if(userId.equals(map.get("userId").toString())){
					resultData.put("userVoteStatus", "1");
					break;
				}
			}
		}
		return resultData;
	}
	
	@Override
	public void addDecisionOpinion(String formalId, String formalTypeStr,
			String aagreeOrDisagree) {
		synchronized (formalId) {
			//获取当前用户
			String userId = ThreadLocalUtil.getUserId();
			String userName = (String) userService.queryById(userId).get("NAME");
			
			//项目类型(0:正式评审,1:其他评审,2:投标评审)
			int formalType = Integer.parseInt(formalTypeStr);
			Map meetingData = null;
			Map<String,Object> queryData = null;
			List<Map<String,Object>> decisionOpinionList = null;
			Map<String,Object> userData = new HashMap<String,Object>(1);
			Map<String,Object> data = new HashMap<String,Object>(1);
			boolean isAddOpinion = true;
			switch (formalType) {
			case 0:
				queryData = baseMongo.queryByCondition(new BasicDBObject("formalId",formalId), Constants.FORMAL_MEETING_INFO).get(0);
				decisionOpinionList = (List<Map<String, Object>>) queryData.get("decisionOpinionList");
				if(null == decisionOpinionList){
					decisionOpinionList = new ArrayList<Map<String,Object>>();
				}
				//防止重复提交!
				if(decisionOpinionList.size() > 0){
					for (Map<String, Object> dataMap : decisionOpinionList) {
						if(dataMap.get("userId").equals(userId)){
							isAddOpinion = false;
							break;
						}
					}
				}
				if(isAddOpinion){
					userData.put("particularView","");
					userData.put("aagreeOrDisagree",aagreeOrDisagree);
					userData.put("userId",userId);
					userData.put("userName",userName);
					decisionOpinionList.add(userData);
					data.put("decisionOpinionList", decisionOpinionList);
					
					String objectId = queryData.get("_id").toString();
					baseMongo.updateSetByObjectId(objectId, data, Constants.FORMAL_MEETING_INFO);
				}
				break;
			case 1:
				queryData = baseMongo.queryByCondition(new BasicDBObject("_id",formalId), Constants.RCM_BULLETIN_INFO).get(0);
				meetingData = (Map) queryData.get("meeting");
				decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
				if(null == decisionOpinionList){
					decisionOpinionList = new ArrayList<Map<String,Object>>();
				}
				//防止重复提交!
				if(decisionOpinionList.size() > 0){
					for (Map<String, Object> dataMap : decisionOpinionList) {
						if(dataMap.get("userId").equals(userId)){
							isAddOpinion = false;
							break;
						}
					}
				}
				if(isAddOpinion){
					userData.put("particularView","");
					userData.put("aagreeOrDisagree",aagreeOrDisagree);
					userData.put("userId",userId);
					userData.put("userName",userName);
					decisionOpinionList.add(userData);
					data.put("meeting.decisionOpinionList", decisionOpinionList);
					baseMongo.updateSetById(formalId, data, Constants.RCM_BULLETIN_INFO);
				}
				break;
			case 2:
				queryData = baseMongo.queryById(formalId, Constants.RCM_PRE_INFO);
				meetingData = (Map) queryData.get("meetingInfo");
				decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
				if(null == decisionOpinionList){
					decisionOpinionList = new ArrayList<Map<String,Object>>();
				}
				//防止重复提交!
				if(decisionOpinionList.size() > 0){
					for (Map<String, Object> dataMap : decisionOpinionList) {
						if(dataMap.get("userId").equals(userId)){
							isAddOpinion = false;
							break;
						}
					}
				}
				if(isAddOpinion){
					userData.put("particularView","");
					userData.put("aagreeOrDisagree",aagreeOrDisagree);
					userData.put("userId",userId);
					userData.put("userName",userName);
					decisionOpinionList.add(userData);
					data.put("meetingInfo.decisionOpinionList", decisionOpinionList);
					baseMongo.updateSetByObjectId(formalId, data, Constants.RCM_PRE_INFO);
				}
				break;
			}
		}
	}

	@Override
	public boolean isCurrUnderwayProject() {
		int status = decisionMapper.isCurrUnderwayProject();
		return 0 != status;
	}

	@Override
	public Map<String, Object> getCurrDecisionOpinion(String id) {
		Map<String, Object> decision = decisionMapper.queryById(id);
		//如果项目已被撤消,则通知前台，执行跳转到表决页面
		if("0".equals(decision.get("VOTE_STATUS").toString())){
			decision.put("userVoteStatus", "0");
			return decision;
		}
		
		//项目类型(0:正式评审,1:其他评审,2:投标评审)
		String formalId = decision.get("FORMAL_ID").toString();
		int formalType = Integer.parseInt(decision.get("FORMAL_TYPE").toString());
		Map meetingData = null;
		Map<String,Object> queryData = null;
		List<Map<String,Object>> meetingLeaders = null;
		List<Map<String,Object>> decisionOpinionList = null;
		switch (formalType) {
		case 0:
			queryData = baseMongo.queryByCondition(new BasicDBObject("formalId",formalId), Constants.FORMAL_MEETING_INFO).get(0);
			meetingLeaders  = (List<Map<String, Object>>) queryData.get("decisionMakingCommitteeStaff");
			decisionOpinionList = (List<Map<String, Object>>) queryData.get("decisionOpinionList");
			decision.put("project_name", queryData.get("projectName"));
			break;
		case 1:
			queryData = baseMongo.queryByCondition(new BasicDBObject("_id",formalId), Constants.RCM_BULLETIN_INFO).get(0);
			meetingData = (Map) queryData.get("meeting");
			meetingLeaders  = (List<Map<String, Object>>) meetingData.get("meetingLeaders");
			decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
			decision.put("project_name", queryData.get("bulletinName"));
			break;
		case 2:
			queryData = baseMongo.queryById(formalId, Constants.RCM_PRE_INFO);
			meetingData = (Map) queryData.get("meetingInfo");
			meetingLeaders  = (List<Map<String, Object>>) meetingData.get("meetingLeaders");
			decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
			Map apply = (Map) queryData.get("apply");
			decision.put("project_name", apply.get("projectName"));
			break;
		}
		//获取已决策人数
		int yiJueCeRenShu = 0;
		if(null != decisionOpinionList){
			yiJueCeRenShu = decisionOpinionList.size();
			//统计谁还没有投票
			for (Map<String, Object> map : meetingLeaders) {
				for (Map<String, Object> map2 : decisionOpinionList) {
					if(map2.get("userId").toString().equals(map.get("VALUE").toString())){
						map.put("isVote","1");
						break;
					}
				}
			}
		}
		decision.put("meetingLeaders", meetingLeaders);
		int zongRenShu = meetingLeaders.size();
		decision.put("weiJueCeRenShu", zongRenShu-yiJueCeRenShu);
		decision.put("zongRenShu", zongRenShu);
		return decision;
	}

	@Override
	public Map<String, Object> getDecisionResult(String id) {
		Map<String, Object> resultData = decisionMapper.queryById(id);

		//项目类型(0:评审项目,1:通报项目)
		String formalId = resultData.get("FORMAL_ID").toString();
		int formalType = Integer.parseInt(resultData.get("FORMAL_TYPE").toString());
		Map meetingData = null;
		Map<String,Object> queryData = null;
		List<Map<String,Object>> meetingLeaders = null;
		List<Map<String,Object>> decisionOpinionList = null;
		String jueCeHuiYiZhuXiId = null;
		switch (formalType) {
		case 0:
			queryData = baseMongo.queryByCondition(new BasicDBObject("formalId",formalId), Constants.FORMAL_MEETING_INFO).get(0);
			jueCeHuiYiZhuXiId = queryData.get("jueCeHuiYiZhuXiId").toString();
			meetingLeaders  = (List<Map<String, Object>>) queryData.get("decisionMakingCommitteeStaff");
			decisionOpinionList = (List<Map<String, Object>>) queryData.get("decisionOpinionList");
			resultData.put("project_name", queryData.get("projectName"));
			break;
		case 1:
			queryData = baseMongo.queryByCondition(new BasicDBObject("_id",formalId), Constants.RCM_BULLETIN_INFO).get(0);
			meetingData = (Map) queryData.get("meeting");
			jueCeHuiYiZhuXiId = meetingData.get("jueCeHuiYiZhuXiId").toString();
			meetingLeaders  = (List<Map<String, Object>>) meetingData.get("meetingLeaders");
			decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
			resultData.put("project_name", queryData.get("bulletinName"));
			break;
		case 2:
			queryData = baseMongo.queryById(formalId, Constants.RCM_PRE_INFO);
			meetingData = (Map) queryData.get("meetingInfo");
			jueCeHuiYiZhuXiId = meetingData.get("jueCeHuiYiZhuXiId").toString();
			meetingLeaders  = (List<Map<String, Object>>) meetingData.get("meetingLeaders");
			decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
			Map apply = (Map) queryData.get("apply");
			resultData.put("project_name", apply.get("projectName"));
			break;
		}
		//决策没有结束，跳转到等待页面!
		if(null == decisionOpinionList || meetingLeaders.size() != decisionOpinionList.size() ){
			resultData.put("userVoteStatus", "1");
			return resultData;
		}
		int zongRenShu = meetingLeaders.size();
		int huiYiZhuXi = 0;
		
		int tongYiCount = 0;
		int buTongYiCount = 0;
		int tiaoJianTongYiCount = 0;
		int zeQiShangHuiCount = 0;
		for (Map<String, Object> map : decisionOpinionList) {
			int aagreeOrDisagree = Integer.parseInt(map.get("aagreeOrDisagree").toString());
			//1:同意投资,2:不同意投资,3:有条件投资,4:择期上会
			switch (aagreeOrDisagree) {
			case 1:
				tongYiCount++;
				break;
			case 2:
				buTongYiCount++;
				break;
			case 3:
				tiaoJianTongYiCount++;
				break;
			case 4:
				zeQiShangHuiCount++;
				break;
			}
			//记录会议主席的  投票决策
			if(jueCeHuiYiZhuXiId.equals(map.get("userId"))){
				huiYiZhuXi = aagreeOrDisagree;
			}
		}
		resultData.put("zongRenShu", zongRenShu);
		resultData.put("tongYiCount", tongYiCount);
		resultData.put("buTongYiCount", buTongYiCount);
		resultData.put("tiaoJianTongYiCount", tiaoJianTongYiCount);
		resultData.put("zeQiShangHuiCount", zeQiShangHuiCount);
		resultData.put("huiYiZhuXi", huiYiZhuXi);
		
		meetingLeaders = meetingIssueService.queryMeetingLeadersByMeetingIssueId(resultData.get("MEETING_ISSUE_ID").toString());
		int meetingLeaderCount = meetingLeaders.size();
		
		//---------------------------------------------------------------------
		//  根据规则求 决策 结果   start
		//---------------------------------------------------------------------
		synchronized (id) {
			Object decisionResultText = resultData.get("DECISION_RESULT");
			int decisionResult = 0;
			if(null != decisionResultText){
				decisionResult = Integer.parseInt(decisionResultText.toString());
			}
			//会议主席选择 不同意
			if(huiYiZhuXi == 2){
				decisionResult = 2;
			//会议主席选择择期再议
			}else if(huiYiZhuXi == 4){
				decisionResult = 4;
			}else {
				//走正常计算流程
				decisionResult = decisionCalculation(tongYiCount, tiaoJianTongYiCount, zongRenShu, buTongYiCount, huiYiZhuXi);
				
				//表决人数小于7个，考虑离场
				//表决人数少于会议安排人数(5+1)，考虑离场
				//如果有条件同意票数  等于  表决人数，则不考虑离场人员
				if(zongRenShu < 7 && zongRenShu < meetingLeaderCount && tongYiCount+tiaoJianTongYiCount != zongRenShu){
					resultData.put("isLiChangGanRaoQuan", 0);

					int differSize = meetingLeaderCount - zongRenShu;
					//第1种情况，离场人员选择同意
					int tongYiCount2 = tongYiCount + differSize;
					int decisionResult1 = decisionCalculation(tongYiCount2, tiaoJianTongYiCount, meetingLeaders.size(), buTongYiCount, huiYiZhuXi);
					
					//第2种情况，离场人员选择不同意
					int buTongYiCount2 = buTongYiCount + differSize;
					int decisionResult2 = decisionCalculation(tongYiCount, tiaoJianTongYiCount, meetingLeaders.size(), buTongYiCount2, huiYiZhuXi);
					
					//如果两个结果一致,则为一致的结果
					int decisionResultNew;
					if(decisionResult1 == decisionResult2){
						decisionResultNew = decisionResult1; 
					}else{
						//如果结果不一致,则结果为择期再议!
						decisionResultNew = 4;
					}
					//如果加入离场人员表决  导致   现场正常表决结果    发生变化,则友情提示
					if(decisionResultNew != decisionResult){
						decisionResult = decisionResultNew;
						resultData.put("isLiChangGanRaoQuan", 1);
						//安排委员人数
						resultData.put("meetingLeaderCount", meetingLeaderCount);
					}
				}
			}
			if(null == decisionResultText || "0".equals(decisionResultText.toString())){
				//更新决策结果
				decisionMapper.updateDecisionResult(id,new Date(),decisionResult);
				//更新项目 流程阶段 状态  
				switch (formalType) {
					case 0:
						assessmentAuditService.updateAuditStageByBusinessId(formalId, "6");
						//更新决策通知书的表决结果
						noticeDecisionInfoService.updateDecisionByBusinessId(formalId,decisionResult);
						//给投资系统推送信息
						TzAfterNoticeClient tzAfterNoticeClient = new TzAfterNoticeClient(formalId, decisionResult+"", null);
						Thread t = new Thread(tzAfterNoticeClient);
						t.start();
						break;
					case 1:
						bulletinInfoService.updateAuditStageByBusinessId(formalId, "4");
						break;
					case 2:
						preInfoService.updateAuditStageByBusinessId(formalId, "6");
						daxtService.preStart(formalId);
						TzAfterPreReviewClient tzAfterPreReviewClient = new TzAfterPreReviewClient(formalId, decisionResult+"", null);
						Thread h = new Thread(tzAfterPreReviewClient);
						h.start();
						break;
				}
			}
			resultData.put("decisionResult", decisionResult);
			//System.out.printf(String.format("zongRenShu:%s,tongYiCount:%s,buTongYiCount:%s,tiaoJianTongYiCount:%s,zeQiShangHuiCount:%s,huiYiZhuXi:%s,decisionResult:%s,decisionResultText:%s\r\n",zongRenShu,tongYiCount,buTongYiCount, tiaoJianTongYiCount,zeQiShangHuiCount,huiYiZhuXi,decisionResult,decisionResultText));
		}
		//---------------------------------------------------------------------
		// 根据规则求 决策 结果   end
		//---------------------------------------------------------------------
		return resultData;
	}
	
	static final DecimalFormat decimal = new DecimalFormat("##.0");   
	private static boolean isDaYuFanMu(int n,int sum,float fan,float mu) {
		float floatN = (float)n;
		float num = ((float)sum / fan) * mu;
		float parseFloat = Float.parseFloat(decimal.format(num));
		return floatN > parseFloat; 
	}
	
	private static boolean isDaYuDengYuFanMu(int n,int sum,float fan,float mu) {
		float floatN = (float)n;
		float num = ((float)sum / fan) * mu;
		float parseFloat = Float.parseFloat(decimal.format(num));
		return floatN >= parseFloat; 
	}
	
	/**
	 * 根据规则计算决策结果
	 */
	private int decisionCalculation(int tongYiCount,int tiaoJianTongYiCount,int zongRenShu,int buTongYiCount,int huiYiZhuXi) {
		int decisionResult;
		//如果“同意投资与同意有条件投资”表决票数合计超过三分之二
		if(isDaYuFanMu(tongYiCount+tiaoJianTongYiCount,zongRenShu,3,2)){
			//且其中至少一半为“同意有条件投资”或当期会议主席意见为“同意有条件投资”，则最终决议为有条件投资
			if(huiYiZhuXi == 3 || isDaYuDengYuFanMu(tiaoJianTongYiCount,tongYiCount+tiaoJianTongYiCount,2,1)){
				decisionResult = 3;
			}else{
				//否则就是同意投资
				decisionResult = 1;
			}
		//不同意投资   大于等于3分之1 ，则为不同意投资
		}else if(isDaYuDengYuFanMu(buTongYiCount,zongRenShu,3,1)){
			decisionResult = 2;
		//否则视为项目条件不成熟或重大问题尚未核实，不具备决策条件，应进一步完善资料择期上会决议。
		}else{
			decisionResult = 4;
		}
		return decisionResult;
	}

	@Override
	public void isShiYongFouJueQuan(Map<String, Object> resultData) {
		int huiYiZhuXi = Integer.parseInt(resultData.get("huiYiZhuXi").toString());
		int zongRenShu = Integer.parseInt(resultData.get("zongRenShu").toString());
		int tongYiCount = Integer.parseInt(resultData.get("tongYiCount").toString());
		int buTongYiCount = Integer.parseInt(resultData.get("buTongYiCount").toString());
		int tiaoJianTongYiCount = Integer.parseInt(resultData.get("tiaoJianTongYiCount").toString());
		
		//如果表决结果为同意,则无会议主席干扰
		int biaoJueJieGuo = Integer.parseInt(resultData.get("decisionResult").toString());
		if(1 == biaoJueJieGuo){
			return;
		}
		
		//如果加入离场人员的计算，则不计算干扰 
		if(resultData.containsKey("isLiChangGanRaoQuan")){
			//如果会议主席选择了，不同意和决期再议，则显示会议选择了什么！
			if(huiYiZhuXi == 2 || huiYiZhuXi == 4){
				resultData.put("isShiYongFouJueQuan", 1);
			}
			return;
		}
		
		int decisionResult = 0;
		if(isDaYuFanMu(tongYiCount+tiaoJianTongYiCount,zongRenShu,3,2)){
			if(isDaYuDengYuFanMu(tiaoJianTongYiCount,tongYiCount+tiaoJianTongYiCount,2,1)){
				decisionResult = 3;
			}else{
				decisionResult = 1;
			}
		}else if(isDaYuDengYuFanMu(buTongYiCount,zongRenShu,3,1)){
			decisionResult = 2;
		}else{
			decisionResult = 4;
		}
		
		//普通投票		跟	会议主席投票结果不同，则视为已干扰
		int isShiYongFouJueQuan = 0;
		if(decisionResult != biaoJueJieGuo){
			//会议主席使用了一票否决权
			isShiYongFouJueQuan = 1;
		}
		resultData.put("isShiYongFouJueQuan", isShiYongFouJueQuan);
	}

	@Override
	public List<Map<String, Object>> queryHistory() {
		//获取当前用户
		String userId = ThreadLocalUtil.getUserId();

		//获取今天已表决的项目
		List<Map<String, Object>> resultData = decisionMapper.queryHistory(Util.format(new Date(), "yyyy-MM-dd"));
		BasicDBObject queryAndWhere = new BasicDBObject();
		for (int i = 0; i < resultData.size(); i++) {
			Map<String, Object> data = resultData.get(i);
			//项目类型(0:评审项目,1:通报项目)
			String formalId = data.get("FORMAL_ID").toString();
			int formalType = Integer.parseInt(data.get("FORMAL_TYPE").toString());
			List<Map<String, Object>> queryData = null;
			List<Map<String,Object>> decisionOpinionList = null;
			switch (formalType) {
			case 0:
				queryAndWhere.put("formalId", formalId);
				queryAndWhere.put("decisionMakingCommitteeStaff.VALUE", userId);
				queryData = baseMongo.queryByCondition(queryAndWhere, Constants.FORMAL_MEETING_INFO);
				if(null == queryData || 0 == queryData.size())
				{
					//当前用户没是 有参与  该项目决策，则不能看到该项目
					resultData.remove(i);
					i--;
				}else{
					data.put("project_name", queryData.get(0).get("projectName"));
					decisionOpinionList = (List<Map<String, Object>>) queryData.get(0).get("decisionOpinionList");
				}
				break;
			case 1:
				queryAndWhere.put("_id", formalId);
				queryAndWhere.put("meeting.meetingLeaders.VALUE", userId);
				queryData = baseMongo.queryByCondition(queryAndWhere, Constants.RCM_BULLETIN_INFO);
				if(null == queryData || 0 == queryData.size())
				{
					//当前用户没是 有参与  该项目决策，则不能看到该项目
					resultData.remove(i);
					i--;
				} else{
					data.put("project_name", queryData.get(0).get("bulletinName"));
					Map meetingData = (Map) queryData.get(0).get("meeting");
					decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
				}
				break;
			case 2:
				queryAndWhere.put("_id", new ObjectId(formalId));
				queryAndWhere.put("meetingInfo.meetingLeaders.VALUE", userId);
				queryData = baseMongo.queryByCondition(queryAndWhere, Constants.RCM_PRE_INFO);
				if(null == queryData || 0 == queryData.size())
				{
					//当前用户没是 有参与  该项目决策，则不能看到该项目
					resultData.remove(i);
					i--;
				} else{
					Map apply = (Map) queryData.get(0).get("apply");
					data.put("project_name",apply.get("projectName"));
					Map meetingData = (Map) queryData.get(0).get("meetingInfo");
					decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
				}
				break;
			}
			queryAndWhere.clear();
			if(null != decisionOpinionList){
				for (Map<String, Object> map : decisionOpinionList) {
					if(userId.equals(map.get("userId").toString())){
						data.put("aagreeOrDisagree",map.get("aagreeOrDisagree"));
						break;
					}
				}
			}
		}
		return resultData;
	}
	
	@Override
	public void updateMeetingLeadersById(String id,
			List<Map<String, Object>> meetingLeaders,String chairmanId) {
		Map<String, Object> decision = this.decisionMapper.queryById(id);
		//项目类型(0:评审项目,1:通报项目)
		String businessId = (String) decision.get("FORMAL_ID");
		String formalType = decision.get("FORMAL_TYPE")+"";
		Map<String, Object> data = new HashMap<String, Object>();
		if("0".equals(formalType)){
			data.put("decisionMakingCommitteeStaff", meetingLeaders);
			data.put("decisionOpinionList", null);
			data.put("jueCeHuiYiZhuXiId", chairmanId);
			this.baseMongo.updateSetByFilter(new BasicDBObject("formalId", businessId), data, Constants.FORMAL_MEETING_INFO);
		}else if("1".equals(formalType)){
			data.put("meeting.meetingLeaders", meetingLeaders);
			data.put("meeting.decisionOpinionList", null);
			data.put("meeting.jueCeHuiYiZhuXiId", chairmanId);
			this.baseMongo.updateSetById(businessId, data, Constants.RCM_BULLETIN_INFO);
		}else if("2".equals(formalType)){
			data.put("meetingInfo.meetingLeaders", meetingLeaders);
			data.put("meetingInfo.decisionOpinionList", null);
			data.put("meetingInfo.jueCeHuiYiZhuXiId", chairmanId);
			this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PRE_INFO);
		}
	}

	@Override
	public boolean isTodayDecision() {
//		页面js判断
//		boolean ifUserIsJueCe = roleService.ifRoleContainUser(Constants.ROLE_CODE_DECISION_LEADERS);
//		if(!ifUserIsJueCe){
//			return false;
//		}
		int countNeedDecisionProject = decisionMapper.countNeedDecisionProject(new Date());
		return countNeedDecisionProject > 0;
	}

	@Override
	public boolean isUserDecision() {
		BasicDBObject queryAndWhere = new BasicDBObject();
		//获取当前用户
		String userId = ThreadLocalUtil.getUserId();
		//获取当前决策项目
		Map<String, Object> resultData = decisionMapper.getCurrUnderwayProject();
		if(null == resultData)
			return false;
		
		//项目类型(0:评审项目,1:通报项目)
		String formalId = resultData.get("FORMAL_ID").toString();
		int formalType = Integer.parseInt(resultData.get("FORMAL_TYPE").toString());

		Map meetingData = null;
		List<Map<String, Object>> queryData = null;
		List<Map<String,Object>> decisionOpinionList = null;
		switch (formalType) {
		case 0:
			queryAndWhere.put("formalId", formalId);
			queryAndWhere.put("decisionMakingCommitteeStaff.VALUE", userId);
			queryData = baseMongo.queryByCondition(queryAndWhere, Constants.FORMAL_MEETING_INFO);
			if(null == queryData || 0 == queryData.size())
				return false;
			decisionOpinionList = (List<Map<String, Object>>) queryData.get(0).get("decisionOpinionList");
			break;
		case 1:
			queryAndWhere.put("_id", formalId);
			queryAndWhere.put("meeting.meetingLeaders.VALUE", userId);
			queryData = baseMongo.queryByCondition(queryAndWhere, Constants.RCM_BULLETIN_INFO);
			if(null == queryData || 0 == queryData.size())
				return false;
			meetingData = (Map) queryData.get(0).get("meeting");
			decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
			break;
		case 2:
			queryAndWhere.put("_id", new ObjectId(formalId));
			queryAndWhere.put("meetingInfo.meetingLeaders.VALUE", userId);
			queryData = baseMongo.queryByCondition(queryAndWhere, Constants.RCM_PRE_INFO);
			if(null == queryData || 0 == queryData.size())
				return false;
			meetingData = (Map) queryData.get(0).get("meetingInfo");
			decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
			break;
		}
		//如果当前用户已表决，则不用跳转到去表决页面了
		if(null != decisionOpinionList){
			for (Map<String, Object> map : decisionOpinionList) {
				if(userId.equals(map.get("userId").toString())){
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String getChairman(
			List<Map<String, Object>> meetingLeadersList) {
		List<Map<String, Object>> chairmans = roleService.queryMeetingChairman();
		String chairmanId = null;
		for (Map<String, Object> chairman : chairmans) {
			for (Map<String, Object> committee : meetingLeadersList) {
				if(committee.get("VALUE").equals(chairman.get("USER_ID"))){
					return chairman.get("USER_ID").toString();
				}
			}
		}
		return chairmanId;
	}

	@Override
	public void cancelDecision() {
		//1:获取当前决策项目
		Map<String, Object> decision = decisionMapper.getCurrUnderwayProject();
		//2:更改数据库状态
		decisionMapper.cancelDecision();
		//3:清除表决项目  已表决的委员
		if(null != decision){
			removeMongoDecisionOpinion(decision);
		}
	}

	private void removeMongoDecisionOpinion(Map<String, Object> decision) {
		//项目类型(0:评审项目,1:通报项目)
		String businessId = (String) decision.get("FORMAL_ID");
		String formalType = decision.get("FORMAL_TYPE")+"";
		Map<String, Object> data = new HashMap<String, Object>();
		if("0".equals(formalType)){
			data.put("decisionOpinionList", null);
			this.baseMongo.updateSetByFilter(new BasicDBObject("formalId", businessId), data, Constants.FORMAL_MEETING_INFO);
		}else if("1".equals(formalType)){
			data.put("meeting.decisionOpinionList", null);
			baseMongo.updateSetById(businessId, data, Constants.RCM_BULLETIN_INFO);
		}else if("2".equals(formalType)){
			data.put("meetingInfo.decisionOpinionList", null);
			baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PRE_INFO);
		}
	}

	@Override
	public Map<String, Object> queryById(String id) {
		return decisionMapper.queryById(id);
	}
	
	@Override
	public Map<String, Object> queryByBusinessId(String businessId) {
		return decisionMapper.queryByBusinessId(businessId);
	}

	@Override
	public void insert(Map<String, Object> decision) {
		decisionMapper.insert(decision);
	}

	@Override
	public void insertBeforeDelete(Map<String, Object> decision) {
		decisionMapper.insertBeforeDelete(decision);
	}
	
	@Override
	public void resetDecision(String id) {
		decisionMapper.resetDecision(id);
		Map<String, Object> result = decisionMapper.queryById(id);
		String businessId = result.get("FORMAL_ID").toString();
		int formalType = Integer.parseInt(result.get("FORMAL_TYPE").toString());
		switch (formalType) {
			case 0:
				noticeDecisionInfoService.updateDecisionByBusinessId(businessId,0);
				assessmentAuditService.updateAuditStageByBusinessId(businessId, "5");
				break;
			case 1:
				bulletinInfoService.updateAuditStageByBusinessId(businessId, "3");
				break;
			case 2:
				preInfoService.updateAuditStageByBusinessId(businessId, "5");
				break;
		}
	}
	
	@Override
	public List<Map<String, Object>> queryByIds(List<Map<String,Object>> projectArray) {
		return decisionMapper.queryByIds(projectArray);
	}

	@Override
	public void addDecisionOpinionNew(String formalId, String formalTypeStr, String aagreeOrDisagree, String zhuxiStatus) {
		synchronized (formalId) {
			//获取当前用户
			String userId = ThreadLocalUtil.getUserId();
			String userName = (String) userService.queryById(userId).get("NAME");
			
			//项目类型(0:正式评审,1:其他评审,2:投标评审)
			int formalType = Integer.parseInt(formalTypeStr);
			Map meetingData = null;
			Map<String,Object> queryData = null;
			List<Map<String,Object>> decisionOpinionList = null;
			Map<String,Object> userData = new HashMap<String,Object>(1);
			Map<String,Object> data = new HashMap<String,Object>(1);
			boolean isAddOpinion = true;
			switch (formalType) {
			case 0:
				queryData = baseMongo.queryByCondition(new BasicDBObject("formalId",formalId), Constants.FORMAL_MEETING_INFO).get(0);
				decisionOpinionList = (List<Map<String, Object>>) queryData.get("decisionOpinionList");
				if(null == decisionOpinionList){
					decisionOpinionList = new ArrayList<Map<String,Object>>();
				}
				//防止重复提交!
				if(decisionOpinionList.size() > 0){
					for (Map<String, Object> dataMap : decisionOpinionList) {
						if(dataMap.get("userId").equals(userId)){
							isAddOpinion = false;
							break;
						}
					}
				}
				if(isAddOpinion){
					userData.put("particularView","");
					userData.put("aagreeOrDisagree",aagreeOrDisagree);
					userData.put("userId",userId);
					userData.put("userName",userName);
					userData.put("zhuxiStatus",zhuxiStatus);
					decisionOpinionList.add(userData);
					data.put("decisionOpinionList", decisionOpinionList);
					
					String objectId = queryData.get("_id").toString();
					baseMongo.updateSetByObjectId(objectId, data, Constants.FORMAL_MEETING_INFO);
				}
				break;
			case 1:
				queryData = baseMongo.queryByCondition(new BasicDBObject("_id",formalId), Constants.RCM_BULLETIN_INFO).get(0);
				meetingData = (Map) queryData.get("meeting");
				decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
				if(null == decisionOpinionList){
					decisionOpinionList = new ArrayList<Map<String,Object>>();
				}
				//防止重复提交!
				if(decisionOpinionList.size() > 0){
					for (Map<String, Object> dataMap : decisionOpinionList) {
						if(dataMap.get("userId").equals(userId)){
							isAddOpinion = false;
							break;
						}
					}
				}
				if(isAddOpinion){
					userData.put("particularView","");
					userData.put("aagreeOrDisagree",aagreeOrDisagree);
					userData.put("userId",userId);
					userData.put("userName",userName);
					userData.put("zhuxiStatus",zhuxiStatus);
					decisionOpinionList.add(userData);
					data.put("meeting.decisionOpinionList", decisionOpinionList);
					baseMongo.updateSetById(formalId, data, Constants.RCM_BULLETIN_INFO);
				}
				break;
			case 2:
				queryData = baseMongo.queryById(formalId, Constants.RCM_PRE_INFO);
				meetingData = (Map) queryData.get("meetingInfo");
				decisionOpinionList =  (List<Map<String, Object>>) meetingData.get("decisionOpinionList");
				if(null == decisionOpinionList){
					decisionOpinionList = new ArrayList<Map<String,Object>>();
				}
				//防止重复提交!
				if(decisionOpinionList.size() > 0){
					for (Map<String, Object> dataMap : decisionOpinionList) {
						if(dataMap.get("userId").equals(userId)){
							isAddOpinion = false;
							break;
						}
					}
				}
				if(isAddOpinion){
					userData.put("particularView","");
					userData.put("aagreeOrDisagree",aagreeOrDisagree);
					userData.put("userId",userId);
					userData.put("userName",userName);
					userData.put("zhuxiStatus",zhuxiStatus);
					decisionOpinionList.add(userData);
					data.put("meetingInfo.decisionOpinionList", decisionOpinionList);
					baseMongo.updateSetByObjectId(formalId, data, Constants.RCM_PRE_INFO);
				}
				break;
			}
		}
		
	}
}
