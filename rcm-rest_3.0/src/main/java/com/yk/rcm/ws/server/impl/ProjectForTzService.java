/**
 * 
 */
package com.yk.rcm.ws.server.impl;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import util.DbUtil;
import util.PropertiesUtil;
import util.Util;
import ws.service.DownloadFileForTZ;

import com.mongodb.BasicDBObject;
import com.yk.ext.filter.ProjectTzFilterChain;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.project.service.IFormalAssesmentService;
import com.yk.rcm.project.service.IPreAssementService;
import com.yk.rcm.ws.server.IProjectForTzService;
import common.Constants;
import common.Result;

/**
 * @author wufucan
 *
 */
@Component
public class ProjectForTzService implements IProjectForTzService {
	
	private Logger log = LoggerFactory.getLogger(ProjectForTzService.class);
	@Autowired
	private ProjectTzFilterChain projectTzFilterChain;
	@Autowired
	private IFormalAssesmentService formalAssessmentService;
	@Autowired
	private IPreAssementService preAssessmentService;
	static Date nowdate = new Date();
	static SimpleDateFormat dateFormatforfile = new SimpleDateFormat("yyyyMM");
	private final String DocumentNamePreFeedBack=Constants.RCM_FEEDBACK_INFO;
	static String  dateforfiles= dateFormatforfile.format(nowdate);
	DownloadFileForTZ downloadFileForTZ=new DownloadFileForTZ();
	
	/* (non-Javadoc)
	 * @see com.yk.rcm.ws.server.IProjectForTzService#createPre(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String createPre(String json) {
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		try{
			Document doc = Document.parse(json);
			Result validResult = this.valid(doc);
			if(!validResult.isSuccess()){
				map.put("result_status", false);
				map.put("error_info", validResult.getResult_name());
				map.put("error_code", validResult.getResult_code());
				return JsonUtil.toJson(map);
			}
			Result result = this.preAssessmentService.saveOrUpdate(doc);
			map = (Map<String, Object>) result.getResult_data();
		}catch (Exception e) {
			StringBuffer sb = new StringBuffer();
			sb.append("***********************[")
				.append(Util.getTime()).append("]:").append("touzi system call out interface createPre, create pre")
				.append(System.getProperty("line.separator"))
				.append("json:")
				.append(json)
				.append(System.getProperty("line.separator"));
			String error = Util.parseException(e);
			map.put("result_status", false);
			map.put("error_info", error);
		}
		return JsonUtil.toJson(map);
	}

	/* (non-Javadoc)
	 * @see com.yk.rcm.ws.server.IProjectForTzService#createPfr(java.lang.String)
	 */
	public String createPfr(String json) {
		Result result = new Result();		
		Document doc = Document.parse(json);
		this.projectTzFilterChain.doFilter(doc, result, this.projectTzFilterChain);
		return result.getResult_data().toString();
	}

	/* (non-Javadoc)
	 * @see com.yk.rcm.ws.server.IProjectForTzService#deletePre(java.lang.String)
	 * 根据TZ系统传过来的businessid 来判断数据库中是否存在起草中或者流程被终止的业务流程进行删除
	 * 验证business非空
	 * 验证流程是否符合要求
	 * 
	 * 
	 * 参数需要businessid
	 */
	@Override
	public String deletePre(String json) {
		Map<String,Object> map = new HashMap<String, Object>();
		Document doc = Document.parse(json);
		String businessid = doc.getString("businessid");
		//验证业务businessid
		if(null==businessid || "".equals(businessid)){
			map.put("result_status", "false");
			map.put("error_info", "所传参数为空！");
			map.put("error_code", "1001");
			return JsonUtil.toJson(map);
		}
		Map<String, Object> queryOracleById = this.preAssessmentService.queryOracleById(businessid);
		if(queryOracleById.size() == 0 || queryOracleById == null){
			map.put("result_status", "false");
			map.put("error_info", "根据参数businessid["+businessid+"]没有找到数据！");
			map.put("error_code", "1002");
			return JsonUtil.toJson(map);
		}
		String wf_state = queryOracleById.get("WF_STATE").toString();
		if("1".equals(wf_state) || "2".equals(wf_state)){
			map.put("result_status", "false");
			map.put("error_info", "业务businessid["+businessid+"]的状态不是起草中，不允许删除！");
			map.put("error_code", "1003");
			return JsonUtil.toJson(map);
		}
		this.preAssessmentService.deleteById(businessid);
		map.put("result_status", "true");
		map.put("error_info", "执行成功！");
		map.put("error_code", null);
		return JsonUtil.toJson(map);
	}
	
	/* (non-Javadoc)
	 * @see com.yk.rcm.ws.server.IProjectForTzService#deletePfr(java.lang.String)
	 * 根据TZ系统传过来的businessid 来判断数据库中是否存在起草中或者流程被终止的业务流程进行删除
	 * 
	 * 参数需要businessid
	 */
	@Override
	public String deletePfr(String json) {
		Map<String,Object> map = new HashMap<String, Object>();
		Document doc = Document.parse(json);
		String businessid = doc.getString("businessid");
		//验证业务businessid
		if(null==businessid || "".equals(businessid)){
			map.put("result_status", "false");
			map.put("error_info", "所传参数为空！");
			map.put("error_code", "1001");
			return JsonUtil.toJson(map);
		}
		//查询当前信息状态
		Map<String, Object> queryOracleById = this.formalAssessmentService.queryOracleById(businessid);
		if(queryOracleById.size() == 0 || queryOracleById == null){
			map.put("result_status", "false");
			map.put("error_info", "根据参数businessid["+businessid+"]没有找到数据！");
			map.put("error_code", "1002");
			return JsonUtil.toJson(map);
		}
		String wf_state = queryOracleById.get("WF_STATE").toString();
		if("1".equals(wf_state) || "2".equals(wf_state)){
			map.put("result_status", "false");
			map.put("error_info", "业务businessid["+businessid+"]的状态不是起草中，不允许删除！");
			map.put("error_code", "1003");
			return JsonUtil.toJson(map);
		}
		this.formalAssessmentService.deleteById(businessid);
		map.put("result_status", "true");
		map.put("error_info", "执行成功！");
		map.put("error_code", null);
		return JsonUtil.toJson(map);
	}
	
	/**
	 * 验证数据信息是否合法
	 * @param doc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Result valid(Document doc){
		Result result = new Result();
		Document apply = (Document) doc.get("apply");
		//验证附件不能为空
		
		List<Document> attachment = (List<Document>) doc.get("attachment");
		if(attachment != null && attachment.size()==0){
			return result.setSuccess(false)
						.setResult_name("附件不能空！")
						.setResult_code("attachment");
		}
		//验证一级业务类型
		List<Document> serviceType = (List<Document>) apply.get("serviceType");
		if(serviceType != null && serviceType.size()==0){
			return result.setSuccess(false)
						.setResult_name("一级业务类型不能为空！")
						.setResult_code("serviceType");
		}
		for(int i = 0; i < serviceType.size(); i++){
			Document type = serviceType.get(i);
			String key = (String) type.get("KEY");
			//如果是水务类项目，那么区域负责人不能为空
			if("1".equals(key)||"4".equals(key)||"2".equals(key)){
				Document investmentPerson = (Document) apply.get("investmentPerson");
				if(investmentPerson == null || investmentPerson.getString("value")==null||
						investmentPerson.getString("name")==null||"".equals(investmentPerson.getString("name"))
						||"".equals(investmentPerson.getString("value"))){
					return result.setSuccess(false)
							.setResult_name("一级业务类型如果为'市政水务','村镇水务','水环境综合治理'，区域负责人不能为空！")
							.setResult_code("investmentPerson");
				}
			}
			
		}
		return result;
	}

	@Override
	public String createPreFeedBack(String json) {
		Map<String,Object> map=new HashMap<String, Object>();
		/*if (json!=null) {
			Pattern p = Pattern.compile("\\s*|\r|\n");
		     Matcher m = p.matcher(json);
		     json = m.replaceAll("");
		}*/
		try{
			Document doc = Document.parse(json);
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String  date= dateFormat.format(now);
			String id=null;
			BasicDBObject queryAndWhere=new BasicDBObject();
			queryAndWhere.put("projectNo",doc.get("projectNo").toString());
			Document oldDoc = DbUtil.getColl(DocumentNamePreFeedBack).find(queryAndWhere).first();
			String filepan=Constants.UPLOAD_DIR+"preAssessment/"+dateforfiles+"/"+doc.get("projectNo")+"/";
			if(null!=oldDoc){
				//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
				oldDoc.put("_id",new ObjectId(oldDoc.get("_id").toString()));
				oldDoc.put("projectName", doc.get("projectName"));
				oldDoc.put("projectNo", doc.get("projectNo"));
				oldDoc.put("create_by", doc.get("create_by"));
				oldDoc.put("create_name", doc.get("create_name"));
				oldDoc.put("projectOpentime", doc.get("projectOpentime"));
				oldDoc.put("projectOurprice", doc.get("projectOurprice"));
				oldDoc.put("tenderResults", doc.get("tenderResults"));
				oldDoc.put("projectOtherprice", doc.get("projectOtherprice"));
				oldDoc.put("projectNoticefile", doc.get("projectNoticefile"));
				oldDoc.append("update_date", date);
				BasicDBObject updateSetValue=new BasicDBObject("$set",oldDoc);
				DbUtil.getColl(DocumentNamePreFeedBack).updateOne(queryAndWhere,updateSetValue);
				ObjectId oid= (ObjectId)oldDoc.get("_id");
				id = oid.toHexString();
			}else{
				doc.append("create_date", date);
				Format format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				doc.append("currentTimeStamp", format.format(new Date()));
				DbUtil.getColl(DocumentNamePreFeedBack).insertOne(doc);
				ObjectId oid= (ObjectId)doc.get("_id");
				id = oid.toHexString();
			}
			map.put("result_status", true);
			map.put("id", id);
			String prefix = PropertiesUtil.getProperty("domain.allow");
			map.put("URL", prefix + "/html/index.html#/AuctionResultFeedback/View/"+id);
			try {
				downloadFileForTZ.downloadFile("preFeedBack", id, filepan);
			} catch (Exception e) {
				StringBuffer sb = new StringBuffer();
				log.error(sb.append("***********************[")
					.append(Util.getTime()).append("]:").append("touzi system call out interface createPfr, create formal")
					.append(System.getProperty("line.separator")).toString());
				String error = Util.parseException(e);
				log.error(error);
				e.printStackTrace();
			}
		}catch (Exception e) {
			String error = Util.parseException(e);
			log.error(error);
			map.put("result_status", false);
			map.put("error_info", error);
		}
		JSONObject jsonMap=new JSONObject(map);
		return jsonMap.toString();
	}

}
