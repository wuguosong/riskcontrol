package formalAssessment;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;

import rcm.ProjectInfo;
import report.FormalAssessmentReportUtil;
import util.CrudUtil;
import util.DbUtil;
import util.FileUtil;
import util.PageMongoDBUtil;
import util.Util;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.rcm.report.service.IReportInfoService;

import common.BaseService;
import common.Constants;
import common.ExportExcel;
import common.PageAssistant;

public class FormalReport extends BaseService{
	/**
	 * 正式评审管理
	 */
	private final String DocumentName=Constants.RCM_FORMALREPORT_INFO;	
	private final String DocumentNameMeeting=Constants.FORMAL_MEETING_INFO;	
	private final String RCM_PROJECTFORMALREVIEW_INFO=Constants.RCM_PROJECTFORMALREVIEW_INFO;	
	
	//获取列表
	public PageAssistant getAllFormalReport(String json) {
		PageAssistant assistant = new PageAssistant(json);
		Map<String, Object> map=assistant.getParamMap();
		String uid=map.get("user_id").toString();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_id", uid);
		paramMap.put("code", Constants.ROLE_SYSTEM_ADMIN);
		String o=DbUtil.openSession().selectOne("role.hasRole",paramMap);
		if("1".equals(o)){
			map.put("user_id", null);
			assistant.setParamMap(map);
		}
		
		assistant.getParamMap().put("type", Constants.FORMAL_ASSESSMENT);
		this.selectByPage(assistant, "projectInfo.selectProjectReviewReport");
		
		
		List<Map<String, Object>> list = (List<Map<String, Object>>) assistant.getList();
		
		MongoCollection<Document> userCollection = DbUtil.getColl(DocumentNameMeeting);
		
		for (Map<String, Object> result : list) {
			String bussinessId = (String) result.get("BUSINESS_ID");
			if(bussinessId!=null&&bussinessId!=""){
				BasicDBObject queryAndWhere =new BasicDBObject();
				queryAndWhere.put("formalId", bussinessId);
				Document doc = userCollection.find(queryAndWhere).first();
				if(null != doc && null != doc.get("isUrgent")){
					result.put("isUrgent", doc.get("isUrgent").toString());
				}
			}
		}
		assistant.setList(list);
		return assistant;
	}
	public PageMongoDBUtil getAll(String json){
		Document bjson = Document.parse(json);
		//当前页默认0
		Integer page=1,pageSize=10,lastId=0;
		Object o=bjson.get("currentPage");
		if(null != o && !"".equals(o) && !"null".equals(o)){
			page=(Integer) o;
		}	
		//默认显示10
		Object itemsp=bjson.get("itemsPerPage");
		if(null != itemsp && !"".equals(itemsp) && !"null".equals(itemsp)){
			pageSize=(Integer) itemsp;
		}
	    MongoCollection<Document> userCollection = DbUtil.getColl(DocumentName);
	    MongoCursor<Document> limit = null,limitcount=null;  
	    
	    BasicDBObject sortVal =new BasicDBObject();
	    sortVal.put("currentTimeStamp", 1);
	    
	    limit = userCollection.find().sort(sortVal).skip((page-1)*pageSize).limit(pageSize).iterator();
    	List<Document>  docList = CrudUtil.convertCursorToList(limit);
    	
		limitcount = userCollection.find().sort(sortVal).iterator();
		List<Document> docListcount = CrudUtil.convertCursorToList(limitcount);
		long total = docListcount.size();
		
		PageMongoDBUtil pageUtil=new  PageMongoDBUtil(Long.toString(total),pageSize.toString(),page.toString(),null,docList);
		
		
		
		return pageUtil;
	}
	//添加信息
	public String create(String json){
		Document doc = Document.parse(json);
		//设置添加时间
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
		String  createDate= dateFormat.format(now);
		doc.append("create_date", createDate);
		Format format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		doc.append("currentTimeStamp", format.format(new Date()));
		doc.append("state", 0);
		DbUtil.getColl(DocumentName).insertOne(doc);
		ObjectId id= (ObjectId)doc.get("_id");
		this.updateProjectInfo(id.toHexString(), doc);
		
		IReportInfoService reportInfoService = (IReportInfoService) SpringUtil.getBean("reportInfoService");
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("businessId", doc.getString("projectFormalId"));
		reportInfoService.saveReport(params);
		
		return id.toString();
	}
	//根据ID查询更新评审负责人
	public String update(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		//设置添加时间
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
		String  updateDate= dateFormat.format(now);
		bson.append("update_date", updateDate);
		String id=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		bson.put("_id",new ObjectId(id));
		BasicDBObject updateSetValue=new BasicDBObject("$set",bson);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		//更新预评审基本信息到项目表
		updateProjectInfo(id, bson);
		
		IReportInfoService reportInfoService = (IReportInfoService) SpringUtil.getBean("reportInfoService");
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("businessId", bson.getString("projectFormalId"));
		
		reportInfoService.updateReportByBusinessId(params);
		
		return id;
	}
	
	//根据ID更新提交时间
	public void SubmitAndupdate(String id){
		BasicDBObject queryAndWhere =new BasicDBObject();
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
		String  submitDate= dateFormat.format(new Date());
		doc.append("submit_date", submitDate);
		doc.put("state", 1);
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
	}
	
	//更新项目评审报告到项目信息表
	private void updateProjectInfo(String reportId, Document doc){
		//更新评审基本信息到项目表
		Map<String, Object> params=new HashMap<String, Object>();
		ProjectInfo prj = (ProjectInfo) SpringUtil.getBean("rcm.ProjectInfo");
		params.put("reportCreateDate", new Date());
		params.put("controllerVal", doc.getString("controllerVal"));
		params.put("reportId", reportId);
		prj.updateProjectInfo(doc.getString("projectFormalId"),params);
	}
	
	public void delete(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		BasicDBObject queryAndWhere2 =new BasicDBObject();
		Document bson = Document.parse(json);
		String arrid=bson.get("_id").toString();
		String[] array={};
		array=arrid.split(",");
		for (String string : array) {
			queryAndWhere.put("_id", new ObjectId(string));
			Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
			
			queryAndWhere2.put("reportlId", string);
			Document docMeeting =DbUtil.getColl(DocumentNameMeeting).find(queryAndWhere2).first();
			if(null!=docMeeting){
				DbUtil.getColl(DocumentNameMeeting).deleteOne(docMeeting);
			}
			
			String projectFormalId = doc.getString("projectFormalId");
			DbUtil.getColl(DocumentName).deleteOne(doc);
			//置空报告相关字段
			ProjectInfo prj = (ProjectInfo) SpringUtil.getBean("rcm.ProjectInfo");
			prj.updateReportInfo2Blank(projectFormalId);
		}
	}
	
	public Document getByID(String id){
		BasicDBObject queryAndWhere =new BasicDBObject();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		if(null!=doc){
			doc.put("_id", doc.get("_id").toString());
			IBaseMongo baseMongo = (IBaseMongo)SpringUtil.getBean("baseMongo");
			//MongoCollection<Document> projectFormalColl = baseMongo.getCollection(Constants.RCM_PROJECTFORMALREVIEW_INFO);
			//projectFormalColl.find();
			Map<String, Object> queryById = baseMongo.queryById(doc.get("projectFormalId").toString(), Constants.RCM_FORMALASSESSMENT_INFO);
			@SuppressWarnings("unchecked")
			Map<String, Object> apply = (Map<String, Object>) queryById.get("apply");
			doc.put("serviceType", apply.get("serviceType"));
		}
		return doc;
	}
	
	//根据ID查询更新评审负责人
	public String policyDecisionUpdate(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String id=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		bson.put("_id",new ObjectId(id));
		BasicDBObject updateSetValue=new BasicDBObject("$set",bson);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		return result.toString();
	}
	
	public String checkRoprt(String json){
		String flag="true";
		Document bson = Document.parse(json);
		BasicDBObject queryAndWhere =new BasicDBObject();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("controllerVal", bson.get("model"));
		queryAndWhere.put("projectFormalId", bson.get("projectID"));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		if(doc!=null){
			flag="false";
		}
		return flag;
	}
	//导出预评审申请列表
	public Map<String,String> importForAssmessentReport(String uuid) throws IOException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_id", uuid);
		paramMap.put("code", Constants.ROLE_SYSTEM_ADMIN);
		String o=DbUtil.openSession().selectOne("role.hasRole",paramMap);
		if("1".equals(o)){
			paramMap.put("user_id", null);
		}
		paramMap.put("type", Constants.FORMAL_ASSESSMENT);
		List<Object> list=DbUtil.openSession().selectList("projectInfo.selectProjectReviewReport",paramMap);
		
		String[] arr = new String[] {"项目名称","投资经理","评审负责人","项目模式","申报单位","提交时间","预评审报告状态"};
		String[] arrColumn = new String[] {"PROJECT_NAME","INVESTMENT_MANAGER_NAME",
		"REVIEW_LEADER_NAME","PROJECT_MODEL_NAMES","REPORTING_UNIT_NAME","APPLY_TIME","WF_STATE"};
		return ExportExcel.exportExcel("正式评审报告列表","forAssessmentReport", arr,arrColumn,list);
	}
	
	public Map<String,String> getPfrAssessmentWord(String id){
		String path=FormalAssessmentReportUtil.generateReport(id);
		
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		BasicDBObject queryAndWhere =new BasicDBObject();
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		
		//判断之前的报告是不是存在，如果存在，删除
		String oldpath = doc.getString("filePath");
		try {
			if(oldpath != null && oldpath.length() > 0){
				FileUtil.removeFile(oldpath);
			}
		} catch (Exception e) {
		}
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		doc.append("filePath", path);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
		String  submitDate= dateFormat.format(new Date());
		doc.append("submit_date", submitDate);
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		Map<String,String> map =new HashMap<String, String>();
		map.put("filePath", path);
		map.put("fileName", doc.get("projectName").toString());
		return map;
	}
	
	//根据ID增加或者修改决策委员材料人员信息及附件
	@SuppressWarnings("unchecked")
	public void addPolicyDecision(String json){
		Document bjson=Document.parse(json);
		
		IReportInfoService reportInfoService = (IReportInfoService) SpringUtil.getBean("reportInfoService");
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("businessId", bjson.getString("projectFormalId"));
		Document policyDecision = (Document) bjson.get("policyDecision");
		if(Util.isNotEmpty(policyDecision)){
			Document projectType = (Document)policyDecision.get("projectType");
			if(Util.isNotEmpty(projectType)){
				params.put("serviceType", projectType.getString("ITEM_CODE"));
			}
			
			if(Util.isNotEmpty(policyDecision.get("projectSize"))){
				params.put("projectSize", policyDecision.get("projectSize").toString());
			}
			if(Util.isNotEmpty(policyDecision.get("investmentAmount"))){
				params.put("investmentAmount", policyDecision.get("investmentAmount").toString());
			}
			if(Util.isNotEmpty(policyDecision.get("rateOfReturn"))){
				params.put("rateOfReturn", policyDecision.get("rateOfReturn").toString());
				
			}
		}
		reportInfoService.updateReportByBusinessId(params);
		
		//附件
		ArrayList<Document> aaa = (ArrayList<Document>) bjson.get("aaa");
		bjson.remove("aaa");
		
		BasicDBObject queryAndWhere =new BasicDBObject();
		String id=bjson.get("_id").toString();
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		
		Document pd = (Document) bjson.get("policyDecision");
		Object projectSizeStr = pd.get("projectSize");
		Object investmentAmountStr = pd.get("investmentAmount");
		Object rateOfReturn = pd.get("rateOfReturn");
		if(projectSizeStr != null && projectSizeStr instanceof String){
			try {
				pd.put("projectSize", Double.parseDouble(projectSizeStr.toString()));
			} catch (NumberFormatException e) {
				pd.put("projectSize", null);
			}
		}
		if(investmentAmountStr != null && investmentAmountStr instanceof String){
			try {
				pd.put("investmentAmount", Double.parseDouble(investmentAmountStr.toString()));
			} catch (NumberFormatException e) {
				pd.put("investmentAmount", null);
			}
		}
		if(rateOfReturn != null && rateOfReturn instanceof String){
			try {
				pd.put("rateOfReturn", Double.parseDouble(rateOfReturn.toString()));
			} catch (NumberFormatException e) {
				pd.put("rateOfReturn", null);
			}
		}
		
		List<Document> decisionMakingCommitteeStaffFiles = (List<Document>) pd.get("decisionMakingCommitteeStaffFiles");
		
		for (Document item : decisionMakingCommitteeStaffFiles) {
			String version = item.get("version").toString();
			if("".equals(version) || null == version){
				String uuid = item.get("UUID").toString();
				int tmp = 1;
				for(Document a :aaa){
					if(a.get("UUID").toString().equals(uuid)){
						List<Document> filesList = (List<Document>) a.get("files");
						tmp = filesList.size();
					}
				}
				item.put("version", ""+tmp);
			}
			
		}
		
		doc.put("policyDecision", pd);
		//如果需要上会，待审阅状态为1，如果不需要上会，状态为3
		ProjectInfo prj = (ProjectInfo) SpringUtil.getBean("rcm.ProjectInfo");
		Map<String, Object> project = prj.selectByBusinessId(doc.getString("projectFormalId"));
		String isNeedMeeting = (String) project.get("NEED_MEETING");
		if("0".equals(isNeedMeeting)){
			doc.put("status", "1");
		}else if("1".equals(isNeedMeeting)){
			doc.put("status", "3");
		}
		
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		
		
		
		//处理附件
		for (Document document : aaa) {
			List<Document> files = (ArrayList<Document>) document.get("files");
			if(Util.isNotEmpty(files)){
				for (Document document2 : files) {
					document2.remove("newFile");
					document2.remove("newItem");
					document2.remove("ITEM_NAME");
					document2.remove("UUID");
				}
			}
		}
		
		//更新附件
		BasicDBObject queryAndWhereForAttachment =new BasicDBObject();
		queryAndWhereForAttachment.put("_id", new ObjectId(bjson.getString("projectFormalId")));
		
		Document olddoc =DbUtil.getColl(RCM_PROJECTFORMALREVIEW_INFO).find(queryAndWhereForAttachment).first();
		olddoc.put("attachment", aaa);
		BasicDBObject updateAttachment=new BasicDBObject("$set",olddoc);
		DbUtil.getColl(RCM_PROJECTFORMALREVIEW_INFO).updateOne(queryAndWhereForAttachment,updateAttachment);
	}
	
	//业务单位承诺列表
	public String saveBusinessUnitCommitList(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String id=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
		String  submitDate= dateFormat.format(new Date());
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		doc.put("pfrBusinessUnitCommit", bson.get("pfrBusinessUnitCommit"));
		doc.put("pfrBusinessUnitCommitState", bson.get("pfrBusinessUnitCommitState"));
		doc.put("pfrBusinessUnitDate", submitDate);
		doc.put("pfrBusinessCreate_by", bson.get("pfrBusinessCreate_by"));
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		return result.toString();
	}
	
	//获取列表
	public PageMongoDBUtil getbusinessUnitCommitList(String json){
		Document bjson = Document.parse(json);
		Document doc = Document.parse(json);
		Map<String, Object> paramMapRole = new HashMap<String, Object>();
		paramMapRole.put("user_id", bjson.get("user_id"));
		paramMapRole.put("code", Constants.ROLE_SYSTEM_ADMIN+"','"+Constants.ROLE_MEETING_ADMIN);
		//当前页默认0
		Integer page=1,pageSize=10;
		Object o=bjson.get("currentPage");
		if(null != o && !"".equals(o) && !"null".equals(o)){
			page=(Integer) o;
		}	
		//默认显示10
		Object itemsp=bjson.get("itemsPerPage");
		if(null != itemsp && !"".equals(itemsp) && !"null".equals(itemsp)){
			pageSize=(Integer) itemsp;
		}
		
        MongoCollection<Document> userCollection = DbUtil.getColl(DocumentName);
        MongoCursor<Document> limit = null,limitcount=null;  
        
        BasicDBObject queryAndWhere =new BasicDBObject();
        BasicDBObject queryVal =new BasicDBObject();
        BasicDBObject queryVal2 =new BasicDBObject();
        BasicDBObject sortVal =new BasicDBObject();
        sortVal.put("pfrBusinessUnitDate",(Integer) doc.get("ORDERVAL"));
       
        queryVal2.append("$ne", null);
        queryAndWhere.put("pfrBusinessUnitCommit",queryVal2);
        String projectName=null,create_date=null,projectSize=null;
        if(!"".equals(doc.get("projectName")) && null!=doc.get("projectName")){
        	projectName = doc.get("projectName").toString();
        }
        if(!"".equals(doc.get("reportingUnit")) && null!=doc.get("reportingUnit")){
        	projectSize = doc.get("reportingUnit").toString();
        }
        if(!"".equals(doc.get("pfrBusinessUnitDate")) && null!=doc.get("pfrBusinessUnitDate")){
        	create_date = doc.get("pfrBusinessUnitDate").toString();
        }
       
        /*if("0".equals(rolesyscount)){
    		queryAndWhere.put("pfrBusinessCreate_by", bjson.get("user_id"));
    	}*/
        if(null!=projectName){
    		Pattern pattern = Pattern.compile("^.*"+projectName+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("projectName", pattern);
    	}
    	
    	if(null!=create_date){
    		Pattern pattern = Pattern.compile("^.*"+create_date+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("pfrBusinessUnitDate", pattern);
    	}
    	 if(null!=projectSize){
     		Pattern pattern = Pattern.compile("^.*"+projectSize+".*$", Pattern.CASE_INSENSITIVE);
     		queryAndWhere.put("reportingUnit", pattern);
     	}
    	 limit = userCollection.find(queryAndWhere).sort(sortVal).skip((page-1)*pageSize).limit(pageSize).iterator();
     	List<Document>  docList = CrudUtil.convertCursorToList(limit);
		/*获取总条数*/
		limitcount = userCollection.find(queryAndWhere).sort(sortVal).iterator();
		List<Document> docListcount = CrudUtil.convertCursorToList(limitcount);
		long total = docListcount.size();
		
		PageMongoDBUtil pageUtil=new  PageMongoDBUtil(Long.toString(total),pageSize.toString(),page.toString(),null,docList);
		return pageUtil;
		
	}
	//删除业务承诺列表
	public void deleteBusinessUnitCommit(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String arrid=bson.get("_id").toString();
		String[] array={};
		array=arrid.split(",");
		for (String id : array) {
			queryAndWhere.put("_id", new ObjectId(id));
			Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
			doc.put("pfrBusinessUnitCommit", null);
			doc.put("pfrBusinessUnitCommitState", null);
			doc.put("pfrBusinessCreate_by", null);
			BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
			DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		}
	}
	
	//已经停用
	public String updateForPolicyDecision(String json){
		Document bson=Document.parse(json);
		//修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		BasicDBObject where=new BasicDBObject();
		where.put("_id",new ObjectId(bson.get("_id").toString()));
		Document OldDoc = DbUtil.getColl(DocumentName).find(where).first();
		//获取new决策委员信息
		Document mapnew=(Document) bson.get("policyDecision");
		List<Document> newDecisionOpinionList =(List<Document>) mapnew.get("decisionOpinionList");
		//获取old决策委员信息
		Document mapold=(Document) OldDoc.get("policyDecision");
		List<Document> oldDecisionOpinionList =(List<Document>) mapold.get("decisionOpinionList");
		
		boolean isTrue=false;
		if(null!=oldDecisionOpinionList){
			for (Document docum : oldDecisionOpinionList) {
				//取出原始文件名称
				String val=(String) docum.get("userId");
				for (Document document : newDecisionOpinionList) {
					if(document.get("userId").equals(val)){
						isTrue=true;
						docum.put("aagreeOrDisagree", document.get("aagreeOrDisagree"));
						docum.put("particularView", document.get("particularView"));
					}
				}
				
			}
		}
		if(!isTrue){
			for (Document document : newDecisionOpinionList) {
				if(oldDecisionOpinionList==null){
					oldDecisionOpinionList =new ArrayList<Document>();
				}
				oldDecisionOpinionList.add(document);
			}
		}
		mapnew.put("decisionOpinionList", oldDecisionOpinionList);
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		OldDoc.put("policyDecision", mapnew);
		bson.put("_id",new ObjectId(bson.get("_id").toString()));
		BasicDBObject updateSetValue=new BasicDBObject("$set",OldDoc);
		
		UpdateResult doc = DbUtil.getColl(DocumentName).updateOne(where,updateSetValue);
		return doc.toString();
	}
	/**
	 * 导出会议列表
	 * @throws IOException 
	 * */
	public Map<String,String> businessUnitListReport() throws IOException{
		 BasicDBObject queryAndWhere =new BasicDBObject();
		 BasicDBObject queryVal =new BasicDBObject();
        queryVal.append("$ne", null);
        queryAndWhere.put("pfrBusinessUnitCommit",queryVal);
		MongoCursor<Document> limit = DbUtil.getColl(DocumentName).find(queryAndWhere).iterator();
        List<Document> docList = CrudUtil.convertCursorToList(limit);
		List<Object> list=new ArrayList<Object>();
		for (Document document : docList) {
			Map<String,String> map=new HashMap<String,String>();
			map.put("projectName", document.get("projectName").toString());
			map.put("reportingUnit",document.get("reportingUnit").toString());
			map.put("pfrBusinessUnitDate",document.get("pfrBusinessUnitDate")==null?null:document.get("pfrBusinessUnitDate").toString());
			list.add(map);
		}
		String[] arr = new String[] {"项目名称","申报单位","创建日期"};
		String[] arrColumn = new String[] {"projectName","reportingUnit","pfrBusinessUnitDate"};
		return ExportExcel.exportExcel("业务单位承诺列表","businessUnitListReport", arr,arrColumn,list);
	}
}
