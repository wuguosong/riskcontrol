/**
 * 
 */
package com.yk.rcm.project.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import util.Util;
import ws.client.IWebClient;
import ws.client.Portal2Client;
import ws.client.contract.SendProjectThread;
import ws.client.portal.dto.PortalClientModel;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.ext.InitWithJson;
import com.yk.ext.Initable;
import com.yk.power.service.IOrgService;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionInfoService;
import com.yk.rcm.project.dao.IWsCallMapper;
import com.yk.rcm.project.service.IWsCallService;
import common.Constants;
import common.PageAssistant;
import common.Result;

/**
 * @author 80845530
 *
 */
@Service
@Transactional(propagation=Propagation.REQUIRES_NEW)
public class WsCallService implements IWsCallService {
//	private static Logger logger = LoggerFactory.getLogger(JournalService.class);
	@Resource
	private IWsCallMapper wsCallMapper;
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IOrgService orgService;
	
	/* (non-Javadoc)
	 * @see com.yk.rcm.service.IWsCallService#save(java.util.Map)
	 */
	@Override
	public void save(Map<String, Object> data) {
		String time = Util.getTime();
		data.put("id", Util.getUUID());
		data.put("createtime", time);
		data.put("updatetime", time);
		data.put("state", "0");
		data.put("calledtimes", 1);
		this.wsCallMapper.insert(data);
	}

	@Override
	public void queryByPage(PageAssistant page) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String,Object>> list = this.wsCallMapper.queryByPage(params);
		page.setList(list);
	}

	@Override
	public Result repeatCallBatch(String[] wscallIds) {
		Result result = new Result();
		List<String> ids = Arrays.asList(wscallIds);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", ids);
		List<Map<String, Object>> rows = this.wsCallMapper.queryByIds(params);
		int total = wscallIds.length;
		int successCount = 0;
		for(int i = 0; i < rows.size(); i++){
			Map<String, Object> row = rows.get(i);
			String type = (String) row.get("TYPE");
			String content = (String) row.get("CONTENT");
			String success = (String) row.get("SUCCESS");
			if("1".equals(success)){
				continue;
			}
			IWebClient webClient = (IWebClient) SpringUtil.getBean(type);
			boolean resendSuccess = webClient.resend(content);
			if(resendSuccess){
				successCount++;
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", row.get("ID"));
			data.put("updatetime", Util.now());
			int calledTimes = Integer.parseInt(row.get("CALLEDTIMES").toString());
			data.put("calledTimes", calledTimes+1);
			data.put("success", resendSuccess);
			this.updateById(data);
		}
		result.setResult_name("总共调用["+total+"]条，执行成功["+successCount+"]条！");
		return result;
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void updateById(Map<String, Object> data){
		this.wsCallMapper.updateById(data);
	}

	@Override
	public Result repeatCallOne(String id) {
		Result result = new Result();
		Map<String, Object> row = this.wsCallMapper.queryById(id);
		if(row == null || row.size() == 0){
			result.setSuccess(false);
			return result.setResult_name("指定的记录已不存在！");
		}
		String type = (String) row.get("TYPE");
		String content = (String) row.get("CONTENT");
		String success = (String) row.get("SUCCESS");
		if("1".equals(success)){
			result.setSuccess(false);
			return result.setResult_name("该记录已经执行成功！");
		}
		IWebClient webClient = (IWebClient) SpringUtil.getBean(type);
		boolean resendSuccess = webClient.resend(content);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", row.get("ID"));
		data.put("updatetime", Util.now());
		int calledTimes = Integer.parseInt(row.get("CALLEDTIMES").toString());
		data.put("calledTimes", calledTimes+1);
		data.put("success", resendSuccess);
		this.updateById(data);
		result.setResult_name(resendSuccess?"重新调用成功！":"重新调用失败！");
		return result;
	}

	@Override
	public Result repeatCallByNoticeId(String id) {
		SendProjectThread contractClient = (SendProjectThread) SpringUtil.getBean("contractClient");
		contractClient.resendByNoticeOfDecisionId(id);
		return new Result().setResult_name("重调执行完成！");
	}

	@Override
	public Result initReportStatus(String beanName) {
		Initable bean = (Initable) SpringUtil.getBean(beanName);
		bean.execute();
		return new Result();
	}
	
	@Override
	public Result initWithJson(String beanName, String json) {
		InitWithJson bean = (InitWithJson) SpringUtil.getBean(beanName);
		return bean.execute(json);
	}
	
	private Result daiBan(String params) {
		
		
		String[] split = params.split(",");
		
		String title = split[0];
		String url = split[1];
		String userId = split[2];
		String status = split[3];
		String time = split[4];
		
		
		PortalClientModel model = new PortalClientModel();
		
		model.setTitle(title);
		model.setUrl(url);
		model.setMobileUrl(url);
		model.setOwner(userId);
		model.setStatus(status);
		Date d = null;
		d = Util.parse(time, "yyyy-MM-dd HH:mm:ss");
		model.setCreated(d);
		model.setType("1");
		Portal2Client ptClientProxy = (Portal2Client) SpringUtil.getBean("ptClient");
		ptClientProxy.setModel(model);
		Thread t = new Thread(ptClientProxy);
		t.start();
		return null;
	}
	
	private Result initDaQu(){
	
		
//		MongoCollection<Document> noticeColl = this.baseMongo.getCollection(Constants.RCM_NOTICEOFDECISION_INFO);
//		FindIterable<Document> noticeIt = noticeColl.find();
//		noticeIt.forEach(new Block<Document>(){
//			@Override
//			public void apply(Document t) {
//				String formalId = (String) t.get("projectFormalId");
//				Map<String, Object> data = new HashMap<String, Object>();
//				data.put("status", "3");
//				BasicDBObject filter = new BasicDBObject("projectFormalId", formalId);
//				WsCallService.this.baseMongo.updateSetByFilter(filter, data, Constants.RCM_FORMALREPORT_INFO);
//			}
//			
//		});
	  /*MongoCollection<Document> meetingColl = this.baseMongo.getCollection(Constants.FORMAL_MEETING_INFO);
		FindIterable<Document> meetingcoll = meetingColl.find();
		
		meetingcoll.forEach(new Block<Document>(){
			@Override
			public void apply(Document t) {
				
				String formalId = (String) t.get("formalId");
				String meetingTime = (String) t.get("meetingTime");
				String startTime = (String) t.get("startTime");
				String isUrgent  = (String) t.get("isUrgent");
				
				IFormalAssesmentService formalAssesmentService = (IFormalAssesmentService)SpringUtil.getBean("formalAssesmentService");
				
				//处理会议时间
				if(""!=meetingTime && null!=meetingTime){
					if(""!=startTime && null!=startTime){
						
						String suffix = startTime.substring(5, 7);//获取后缀
						String hour = startTime.substring(0, 1);//获取小时数
						String minute = startTime.substring(1, 4);//获取时间，把时间的第一位去掉
						
						int parseHour = 0;
						if(suffix.equals("PM")){
							parseHour = Integer.parseInt(hour);
							parseHour += 12;
						}
						//最终结果
						Date concatDateWithTime = concatDateWithTime(meetingTime, parseHour+minute);
//						IFormalAssesmentService formalAssesmentService = (IFormalAssesmentService)SpringUtil.getBean("formalAssesmentService");
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("meetingDate", concatDateWithTime);
						formalAssesmentService.updateOracleById(formalId, params);
						
					}
				}
				//处理是否紧急的数据
				if(null!=isUrgent && ""!= isUrgent ){
//					IFormalAssesmentService formalAssesmentService = (IFormalAssesmentService)SpringUtil.getBean("formalAssesmentService");
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("isUrgent", isUrgent);
					formalAssesmentService.updateOracleById(formalId, params);
				}
				if(null == isUrgent || isUrgent.equals("")){
					isUrgent ="0";
//					IFormalAssesmentService formalAssesmentService = (IFormalAssesmentService)SpringUtil.getBean("formalAssesmentService");
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("isUrgent", isUrgent);
					formalAssesmentService.updateOracleById(formalId, params);//修改oracle
					baseMongo.updateSetByObjectId(t.get("_id").toString(), params, Constants.FORMAL_MEETING_INFO);
				}
			}
		});*/
		//更新正式评审业务类型原始数据
//		MongoCollection<Document> projectFormalColl = this.baseMongo.getCollection(Constants.RCM_PROJECTFORMALREVIEW_INFO);
//		FindIterable<Document> projectColl = projectFormalColl.find();
//		projectColl.forEach(new Block<Document>(){
//			@SuppressWarnings("unchecked")
//			@Override
//			public void apply(Document doc) {
//				//Map<String,Object> map=(Map<String, Object>) doc;
//				//System.err.println(map.get("serviceType"));
//				Document apply = (Document) doc.get("apply");
//				List<Document> serviceTypeList = (List<Document>) apply.get("serviceType");
//				if(serviceTypeList != null || !(serviceTypeList.isEmpty())){
//				String str1 = JsonUtil.toJson(serviceTypeList);
//				List<Document> oldServiceType = JsonUtil.fromJson(str1, List.class);
//				apply.append("oldServiceType", oldServiceType);
//				HashSet<String> serviceTypeSet = new HashSet<String>();
//				List<Document> newServiceType = new ArrayList<Document>();
//				for(int i=0; i<serviceTypeList.size(); i++){
//					Document serviceType = serviceTypeList.get(i);
//					if(!Util.isEmpty(serviceType)){
//						String serviceTypeKey = serviceType.getString("KEY");
//						if(!Util.isEmpty(serviceTypeKey)){
//							if(serviceTypeKey.equals("1") || serviceTypeKey.equals("4")){
//								serviceTypeKey = "1401";
//							}
//							if(serviceTypeKey.equals("2")){
//								serviceTypeKey = "1402";
//							}
//							if(serviceTypeKey.equals("5")){
//								serviceTypeKey = "1404";
//							}
//							if(serviceTypeKey.equals("6")){
//								serviceTypeKey = "1403";
//							}
//							if(serviceTypeKey.equals("7")){
//								serviceTypeKey = "1405";
//							}
//							serviceTypeSet.add(serviceTypeKey);
//						}
//					}
//				}
//				for (String  KEY : serviceTypeSet) {
//					String VALUE="";
//					if(KEY.equals("1401")){
//						VALUE  = "传统水务";
//					}
//					if(KEY.equals("1402")){
//						VALUE  = "水环境";
//					}
//					if(KEY.equals("1403")){
//						VALUE  = "固废";
//					}
//					if(KEY.equals("1404")){
//						VALUE  = "环卫";
//					}
//					if(KEY.equals("1405")){
//						VALUE  = "其他";
//					}
//					Document serviceType = new Document();
//					serviceType.put("KEY", KEY);
//					serviceType.put("VALUE", VALUE);
//					newServiceType.add(serviceType);
//				}
//				apply.put("serviceType", newServiceType);
//				Document reportingUnit = (Document) apply.get("reportingUnit");
//				
//				Document pertainArea = null;
//				
//				if(!Util.isEmpty(reportingUnit)){
//					//处理大区pertainArea
//					String orgpkvalue = reportingUnit.getString("value"); 
//					if(!Util.isEmpty(orgpkvalue)){
//						 pertainArea =  getPertainArea(orgpkvalue);
//						apply.put("pertainArea", pertainArea);
//					}
//				}
//				String id=doc.get("_id").toString();
//				Map<String, Object> params = new HashMap<String, Object>();
//				
//				if(!Util.isEmpty(pertainArea)){
//					String pertainAreaId = pertainArea.getString("KEY");
//						if(!Util.isEmpty(pertainAreaId)){
//							params.put("pertainArea", pertainAreaId);
//							IFormalAssesmentService formalAssesmentService = (IFormalAssesmentService)SpringUtil.getBean("formalAssesmentService");
//							formalAssesmentService.updateOracleById(id, params);
//						}
//				}
//				baseMongo.updateSetByObjectId(id, doc, Constants.RCM_PROJECTFORMALREVIEW_INFO);
//			}
//			}
//		});
//		//更新预评审业务类型原始数据
//		MongoCollection<Document> projectPreColl = this.baseMongo.getCollection(Constants.PREASSESSMENT);
//		FindIterable<Document> preColl = projectPreColl.find();
//		preColl.forEach(new Block<Document>(){
//			@SuppressWarnings("unchecked")
//			@Override
//			public void apply(Document doc) {
//				//Map<String,Object> map=(Map<String, Object>) doc;
//				//System.err.println(map.get("serviceType"));
//				Document apply = (Document) doc.get("apply");
//				List<Document> serviceTypeList = (List<Document>) apply.get("serviceType");
//				if(serviceTypeList != null || !(serviceTypeList.isEmpty())){
//				String str1 = JsonUtil.toJson(serviceTypeList);
//				List<Document> oldServiceType = JsonUtil.fromJson(str1, List.class);
//				apply.append("oldServiceType", oldServiceType);
//				HashSet<String> serviceTypeSet = new HashSet<String>();
//				List<Document> newServiceType = new ArrayList<Document>();
//				for(int i=0; i<serviceTypeList.size(); i++){
//					Document serviceType = serviceTypeList.get(i);
//					if(!Util.isEmpty(serviceType)){
//						String serviceTypeKey = serviceType.getString("KEY");
//						if(!Util.isEmpty(serviceTypeKey)){
//							if(serviceTypeKey.equals("1") || serviceTypeKey.equals("4")){
//								serviceTypeKey = "1401";
//							}
//							if(serviceTypeKey.equals("2")){
//								serviceTypeKey = "1402";
//							}
//							if(serviceTypeKey.equals("5")){
//								serviceTypeKey = "1404";
//							}
//							if(serviceTypeKey.equals("6")){
//								serviceTypeKey = "1403";
//							}
//							if(serviceTypeKey.equals("7")){
//								serviceTypeKey = "1405";
//							}
//							serviceTypeSet.add(serviceTypeKey);
//						}
//					}
//				}
//				for (String  KEY : serviceTypeSet) {
//					String VALUE="";
//					if(KEY.equals("1401")){
//						VALUE  = "传统水务";
//					}
//					if(KEY.equals("1402")){
//						VALUE  = "水环境";
//					}
//					if(KEY.equals("1403")){
//						VALUE  = "固废";
//					}
//					if(KEY.equals("1404")){
//						VALUE  = "环卫";
//					}
//					if(KEY.equals("1405")){
//						VALUE  = "其他";
//					}
//					Document serviceType = new Document();
//					serviceType.put("KEY", KEY);
//					serviceType.put("VALUE", VALUE);
//					newServiceType.add(serviceType);
//				}
//				apply.put("serviceType", newServiceType);
//				Document reportingUnit = (Document) apply.get("reportingUnit");
//				Document pertainArea = null;
//				if(!Util.isEmpty(reportingUnit)){
//					//处理大区pertainArea
//					String orgpkvalue = reportingUnit.getString("value");
//					if(!Util.isEmpty(orgpkvalue)){
//						pertainArea =  getPertainArea(orgpkvalue);
//						apply.put("pertainArea", pertainArea);
//					}
//				}
//				String id=doc.get("_id").toString();
//				Map<String, Object> params = new HashMap<String, Object>();
//				if(!Util.isEmpty(pertainArea)){
//					String pertainAreaId = pertainArea.getString("KEY");
//						if(!Util.isEmpty(pertainAreaId)){
//							params.put("pertainArea", pertainAreaId);
//							IFormalAssesmentService formalAssesmentService = (IFormalAssesmentService)SpringUtil.getBean("formalAssesmentService");
//							formalAssesmentService.updateOracleById(id, params);
//						}
//				}
//				baseMongo.updateSetByObjectId(id, doc, Constants.PREASSESSMENT);
//			}
//			}
//		});
		return new Result();
		
	}
	//决策通知书旧表数据添加到新表
	private Result initNoticeDicision(){
		MongoCollection<Document> noticeCell = this.baseMongo.getCollection(Constants.RCM_NOTICEOFDECISION_INFO);
		FindIterable<Document> noticeDicisionCell = noticeCell.find();
		noticeDicisionCell.forEach(new Block<Document>(){
			
			@Override
			public void apply(Document t) {
				Document returnNoticeDicision = selectMongoData(t);
				String id =  returnNoticeDicision.get("_id").toString();
				Map<String, Object> row = baseMongo.queryById(id, Constants.RCM_NOTICEDECISION_INFO);
				if(!Util.isEmpty(row)){
					baseMongo.updateSetByObjectId(id, returnNoticeDicision, Constants.RCM_NOTICEDECISION_INFO);
				}else{
					baseMongo.save(returnNoticeDicision, Constants.RCM_NOTICEDECISION_INFO);
				}
			}
		});
		//查oracle数据
		List<Map<String, Object>> noticeDicisionList = this.wsCallMapper.queryNotice();
			for (int i = 0; i < noticeDicisionList.size(); i++) {
					Map<String,Object> noticeDicision = noticeDicisionList.get(i);
					String businessid = (String) noticeDicision.get("BUSINESSID");
//					String reportingunit = (String) noticeDicision.get("REPORTINGUNIT");
					//String contractscale = (String) noticeDicision.get("CONTRACTSCALE");
					//String evaluationscale = (String) noticeDicision.get("EVALUATIONSCALE");
					String reviewoftotalinvestment = (String) noticeDicision.get("REVIEWOFTOTALINVESTMENT");
					String decisionstage = (String) noticeDicision.get("DECISIONSTAGE");
					String dateofmeeting = (String) noticeDicision.get("DATEOFMEETING");
					String consenttoinvestment = (String) noticeDicision.get("CONSENTTOINVESTMENT");
					String responsibilityunitvalue = (String) noticeDicision.get("RESPONSIBILITYUNITVALUE");
					String wf_state = (String) noticeDicision.get("WF_STATE");
					String apply_date = (String) noticeDicision.get("APPLY_DATES");
					String create_date = (String) noticeDicision.get("CREATE_DATES");
					String last_update_date = (String) noticeDicision.get("LAST_UPDATE_DATES");
					String projectformalid = (String) noticeDicision.get("PROJECTFORMALID");
					String reportingunitid= wsCallMapper.getUnitIdById(businessid);
					String createby = wsCallMapper.getGreateBy(businessid,"0706");
					
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("businessId", businessid);
//					params.put("reportingunit", reportingunit);
					//params.put("contractScale", null);
					//params.put("evaluationScale", null);
					params.put("reviewOfTotalInvestment", reviewoftotalinvestment);
					params.put("decisionStage", decisionstage);
					params.put("dateOfMeeting", dateofmeeting);
					params.put("consentToInvestment", consenttoinvestment);
					params.put("responsibilityUnitValue", responsibilityunitvalue);
					params.put("wf_State", wf_state);
					params.put("apply_date", apply_date);
					params.put("create_date", create_date);
					params.put("last_update_date", last_update_date);
					params.put("projectFormalid", projectformalid);
					params.put("reportingUnitId", reportingunitid);
					params.put("createBy", createby);
					params.put("oldData", "0");
					
					if(wf_state.equals("1") || wf_state.equals("2")){
						params.put("oldData", "1");
					}
					String id = (String) params.get("businessid");
					List<Map<String, Object>> not = wsCallMapper.queryOracleById(businessid);
					INoticeDecisionInfoService noticeDecisionInfoService = (INoticeDecisionInfoService)SpringUtil.getBean("noticeDecisionInfoService");
					if(!Util.isEmpty(not)){
						noticeDecisionInfoService.deleteOracle(businessid);
						noticeDecisionInfoService.insert(params);
					}else{
						noticeDecisionInfoService.insert(params);
					}
				}
		return new Result();
	}
	//查询mongodb数据
	private Document selectMongoData(Document t){
			String id = t.get("_id").toString();
			Document createBy = (Document) t.get("createBy");
			String projectFormalId = (String) t.get("projectFormalId");
			String projectName = (String) t.get("projectName");
			String value= wsCallMapper.getUnitIdById(id);
			String name = wsCallMapper.getorgpkvalueIdById(value);
			String contractScale = (String) t.get("contractScale");
			String evaluationScale = (String) t.get("evaluationScale");
			String reviewOfTotalInvestment = (String) t.get("reviewOfTotalInvestment");
			String additionalReview = (String) t.get("additionalReview");
			String dateOfMeeting = (String) t.get("dateOfMeeting");
			String decisionStage = (String) t.get("decisionStage");
			String consentToInvestment = (String) t.get("consentToInvestment");
			String implementationMatters = (String) t.get("implementationMatters");
			Document subjectOfImplementation = (Document) t.get("subjectOfImplementation");
			String equityRatio = (String) t.get("equityRatio");
			String registeredCapital = (String) t.get("registeredCapital");
			Document responsibilityUnit = (Document) t.get("responsibilityUnit");
			
			Object object = t.get("personLiable");
			String person = object.toString();
			person = person.substring(person.length()-1);
			List<Document> list = new ArrayList<Document>();
			if(person.equals("]")){
				List<Document> personLiableList = (List<Document>) t.get("personLiable");
				for(int i=0; i<personLiableList.size(); i++){
					Document map = personLiableList.get(i);
					String names = (String) map.get("name");
					String values = (String) map.get("value");
					Document personLia = new Document();
					personLia.put("name", names);
					personLia.put("value", values);
					list.add(personLia);
				}
			}else{
				Document personLiable = (Document) t.get("personLiable");
				list.add(personLiable);
			}
			
			String implementationRequirements = (String) t.get("implementationRequirements");
			String create_date = (String) t.get("create_date");
			String currentTimeStamp = (String) t.get("currentTimeStamp");
			
			Document noticeDicision = new Document();
			noticeDicision.put("_id", new ObjectId(id));
			noticeDicision.put("createBy", createBy);
			noticeDicision.put("projectFormalId", projectFormalId);
			noticeDicision.put("projectName", projectName);
			Document reportingUnitNew = new Document();
			reportingUnitNew.put("value", value);
			reportingUnitNew.put("name", name);
			noticeDicision.put("reportingUnit", reportingUnitNew);
			noticeDicision.put("contractScale", contractScale);
			noticeDicision.put("evaluationScale", evaluationScale);
			noticeDicision.put("reviewOfTotalInvestment", reviewOfTotalInvestment);
			noticeDicision.put("additionalReview", additionalReview);
			noticeDicision.put("dateOfMeeting", dateOfMeeting);
			noticeDicision.put("decisionStage", decisionStage);
			noticeDicision.put("consentToInvestment", consentToInvestment);
			noticeDicision.put("implementationMatters", implementationMatters);
			noticeDicision.put("subjectOfImplementation", subjectOfImplementation);
			noticeDicision.put("equityRatio", equityRatio);
			noticeDicision.put("registeredCapital", registeredCapital);
			noticeDicision.put("responsibilityUnit", responsibilityUnit);
			noticeDicision.put("personLiable", list);
			noticeDicision.put("implementationRequirements", implementationRequirements);
			noticeDicision.put("create_date", create_date);
			noticeDicision.put("currentTimeStamp", currentTimeStamp);
			noticeDicision.put("oldData", "1");
			
			return noticeDicision;
	}
	
	private Document getPertainArea(String orgpkvalue) {
		Document pertainArea = new Document();
		Map<String, Object> queryPertainArea = this.orgService.queryPertainAreaByPkvalue(orgpkvalue);
		if(Util.isNotEmpty(queryPertainArea) ){
			pertainArea.put("KEY", queryPertainArea.get("ORGPKVALUE"));
			pertainArea.put("VALUE", queryPertainArea.get("NAME"));
		}
		return pertainArea;
	}

	
	private Date concatDateWithTime(String date, String time){
		String regex = "(\\d+):(\\d+)\\s+(\\w+)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(time);
		if (m.find()) {
			String g1 = m.group(1);
			String g2 = m.group(2);
			String g3 = m.group(3);
			Integer hour = Integer.parseInt(g1);
			if("PM".equals(g3) && hour != 12){
				hour += 12;
			}
			time = hour+":"+g2;
		}
		String newDateStr =  date+" "+time;
		String format = "yyyy-MM-dd HH:mm";
		Date newDate = null;
		newDate = Util.parse(newDateStr, format);
		return newDate;
	}

	@Override
	public Result sendTask(String json) {
		
		Document doc = Document.parse(json);
		PortalClientModel model = new PortalClientModel();
		
		String taskId = (String) doc.get("taskId");
		String url = (String) doc.get("url");
		String createDate = (String) doc.get("createDate");
		String title = (String) doc.get("title");
		String owner = (String) doc.get("owner");
		String status = (String) doc.get("status");
		String sender = (String) doc.get("sender");
		Date d = null;
		d = Util.parse(createDate, "yyyy-MM-dd HH:mm:ss");
		if(Util.isEmpty(d)){
			return new Result().setSuccess(false).setResult_name("日期格式有误！");
		}
		model.setCreated(d);
		model.setUniid(taskId);
		model.setTitle(title);
		model.setType("1");
		model.setUrl(url);
		model.setMobileUrl(url);
		model.setOwner(owner);
		model.setStatus(status);
		model.setSender(sender);
		
		Portal2Client ptClientProxy = (Portal2Client) SpringUtil.getBean("ptClient");
		ptClientProxy.setModel(model);
		Thread ptClientProxyThread = new Thread(ptClientProxy);
		ptClientProxyThread.start();
		return new Result();
	}

	@Override
	public void saveWsServer(String type, String content, String result,boolean success) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", Util.getUUID());
		param.put("type", type);
		param.put("content", content);
		param.put("create_time",new Date());
		param.put("result",result);
		param.put("success",success?"1":"0");
		param.put("state","1");
		wsCallMapper.saveWsServer(param);
	}

	@Override
	public Map<String, Object> queryById(String id) {
		return wsCallMapper.queryById(id);
	}
}
