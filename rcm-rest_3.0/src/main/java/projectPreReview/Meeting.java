package projectPreReview;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rcm.ProjectInfo;
import rcm.ProjectRelation;
import util.CrudUtil;
import util.DbUtil;
import util.PageMongoDBUtil;
import util.Util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.rcm.project.service.IFormalAssesmentService;

import common.Constants;
import common.ExportExcel;

public class Meeting {
	
	private static Logger logger = LoggerFactory.getLogger(Meeting.class);
	private final String DocumentName=Constants.FORMAL_MEETING_INFO;	
	private final String DocumentName1=Constants.FORMAL_MEETING_NOTICE;	
	private final String DocumentNameFormal=Constants.RCM_PROJECTFORMALREVIEW_INFO;	
	private final String DocumentNameReport=Constants.RCM_FORMALREPORT_INFO;
	
	public PageMongoDBUtil getAll(String json){
		Document bjosn = Document.parse(json);
		//当前页默认0
		Integer page=1,pageSize=10;
		Object o=bjosn.get("currentPage");
		if(null != o && !"".equals(o) && !"null".equals(o)){
			page=(Integer) o;
		}	
		//默认显示10
		Object itemsp=bjosn.get("itemsPerPage");
		if(null != itemsp && !"".equals(itemsp) && !"null".equals(itemsp)){
			pageSize=(Integer) itemsp;
		}
        MongoCollection<Document> userCollection = DbUtil.getColl(DocumentName);
        MongoCursor<Document> limit = null,limitcount=null;  
        BasicDBObject sortVal =new BasicDBObject();
        sortVal.put("create_date",(Integer) bjosn.get("ORDERVAL"));
        BasicDBObject queryAndWhere =new BasicDBObject();
        BasicDBObject queryVal2 =new BasicDBObject();
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_id", bjosn.get("user_id").toString());
		paramMap.put("code", Constants.ROLE_MEETING_ADMIN);
		String o1=DbUtil.openSession().selectOne("role.hasRole",paramMap);
		if("0".equals(o1)){
			queryAndWhere.put("user_id", bjosn.get("user_id").toString());
		}
        String projectName=null,projectForm=null,state=null;
        if(!"".equals(bjosn.get("projectName")) && null!=bjosn.get("projectName")){
        	projectName = bjosn.get("projectName").toString();
        }
        if(!"".equals(bjosn.get("projectForm")) && null!=bjosn.get("projectForm")){
        	projectForm = bjosn.get("projectForm").toString();
        }
        if(!"".equals(bjosn.get("state")) && null!=bjosn.get("state")){
        	state = bjosn.get("state").toString();
        }
        queryVal2.put("$eq", null);
        queryAndWhere.put("noticeId", queryVal2);
    	if(null!=projectName){
    		Pattern pattern = Pattern.compile("^.*"+projectName+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("projectName", pattern);
    	}
    	if(null!=state){
    		queryAndWhere.put("state", Integer.parseInt(state));
    	}
    	if(null!=projectForm){
    		queryAndWhere.put("projectForm", projectForm);
    	}
    	limit = userCollection.find(queryAndWhere).sort(sortVal).skip((page-1)*pageSize).limit(pageSize).iterator();
    	List<Document>  docList = CrudUtil.convertCursorToList(limit);
        
		limitcount = userCollection.find(queryAndWhere).sort(sortVal).iterator();
		List<Document> docListcount = CrudUtil.convertCursorToList(limitcount);
		long total = docListcount.size();
		
		PageMongoDBUtil pageUtil=new  PageMongoDBUtil(Long.toString(total),pageSize.toString(),page.toString(),null,docList);
		return pageUtil;
	}
	public PageMongoDBUtil getMeetingAll(String json){
		Document doc = Document.parse(json);
		//当前页默认0
		Integer page=1,pageSize=10;
		Object o=doc.get("currentPage");
		if(null != o && !"".equals(o) && !"null".equals(o)){
			page=(Integer) o;
		}	
		//默认显示10
		Object itemsp=doc.get("itemsPerPage");
		if(null != itemsp && !"".equals(itemsp) && !"null".equals(itemsp)){
			pageSize=(Integer) itemsp;
		}
		
        MongoCollection<Document> userCollection = DbUtil.getColl(DocumentName);
        MongoCursor<Document> limit = null,limitcount=null;  
        BasicDBObject sortVal =new BasicDBObject();
        sortVal.put("meetingTime",(Integer) doc.get("ORDERVAL"));
        BasicDBObject queryAndWhere =new BasicDBObject();
        BasicDBObject queryAndWherecount =new BasicDBObject();
        BasicDBObject queryVal2 =new BasicDBObject();
        queryVal2.put("$ne", null);
    	queryAndWhere.put("noticeId", queryVal2);
        
        String projectName=null,meetingTime=null;
        if(!"".equals(doc.get("projectName")) && null!=doc.get("projectName")){
        	projectName = doc.get("projectName").toString();
        }
        if(!"".equals(doc.get("meetingTime")) && null!=doc.get("meetingTime")){
        	meetingTime = doc.get("meetingTime").toString();
        }
        if(null!=projectName){
    		Pattern pattern = Pattern.compile("^.*"+projectName+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("projectName", pattern);
    	}
    	if(null!=meetingTime){
    		queryAndWhere.put("meetingTime", meetingTime);
    	} 
    	limit = userCollection.find(queryAndWhere).sort(sortVal).skip((page-1)*pageSize).limit(pageSize).iterator();
    	List<Document>  docList = CrudUtil.convertCursorToList(limit);
    	
		queryVal2.put("$ne", null);
		queryAndWherecount.put("noticeId", queryVal2);
		limitcount = userCollection.find(queryAndWherecount).sort(sortVal).iterator();
		List<Document> docListcount = CrudUtil.convertCursorToList(limitcount);
		long total = docListcount.size();
		PageMongoDBUtil pageUtil=new  PageMongoDBUtil(Long.toString(total),pageSize.toString(),page.toString(),null,docList);
		return pageUtil;
	}
	/**
	 * 已决策列表
	 * @param json
	 * @return
	 */
	public PageMongoDBUtil getPolicyDecisionAll(String json){
		Document doc = Document.parse(json);
		//当前页默认0
		Integer page=1,pageSize=10;
		Object o=doc.get("currentPage");
		if(null != o && !"".equals(o) && !"null".equals(o)){
			page=(Integer) o;
		}	
		//默认显示10
		Object itemsp=doc.get("itemsPerPage");
		if(null != itemsp && !"".equals(itemsp) && !"null".equals(itemsp)){
			pageSize=(Integer) itemsp;
		}
        MongoCollection<Document> userCollection = DbUtil.getColl(DocumentNameReport);
        MongoCursor<Document> limit = null,limitcount=null;
        BasicDBObject sortVal =new BasicDBObject();
        String ordert=(String) doc.get("ORDERT");
        if(null!=ordert){
        	sortVal.put("policyDecision.submitDate",Integer.valueOf(ordert));
        }
        String orderg=(String) doc.get("ORDERG");
        if(null!=orderg){
        	sortVal.put("policyDecision.projectSize",Integer.valueOf(orderg));
        }
        String orderj=(String) doc.get("ORDERJ");
        if(null!=orderj){
        	sortVal.put("policyDecision.investmentAmount",Integer.valueOf(orderj));
        }
        if(null==ordert && null==orderg && null==orderj){
        	sortVal.put("policyDecision.submitDate",-1);
        }
        BasicDBObject queryAndWhere =new BasicDBObject();
        String projectName=null,projectType=null,reportingUnit=null;
        Double projectSizelt=null;
        Double projectSizegt=null;
        BasicDBObject queryVal3 =new BasicDBObject();
        queryVal3.put("$ne",null);
    	queryAndWhere.put("policyDecision", queryVal3);
        if(!"".equals(doc.get("projectName")) && null!=doc.get("projectName")){
        	projectName = doc.get("projectName").toString();
        }
        if(!"".equals(doc.get("projectType")) && null!=doc.get("projectType")){
        	projectType = doc.get("projectType").toString();
        }
        if(!"".equals(doc.get("reportingUnit")) && null!=doc.get("reportingUnit")){
        	reportingUnit = doc.get("reportingUnit").toString();
        }
        
        //规模
        if(!"".equals(doc.get("projectSizelt")) && null!=doc.get("projectSizelt")){
        	projectSizelt = Double.parseDouble((String) doc.get("projectSizelt")) ;
        }
        //规模
        if(!"".equals(doc.get("projectSizegt")) && null!=doc.get("projectSizegt")){
        	projectSizegt = Double.parseDouble((String) doc.get("projectSizegt")) ;
        }
        
        if(null!=projectName){
    		Pattern pattern = Pattern.compile("^.*"+projectName+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("projectName", pattern);
    	} 
        if(null!=projectType){
    		Pattern pattern = Pattern.compile("^.*"+projectType+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("policyDecision.projectType.ITEM_NAME", pattern);
    	} 
        if(null!=reportingUnit){
    		Pattern pattern = Pattern.compile("^.*"+reportingUnit+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("reportingUnit", pattern);
    	}
        
        BasicDBObject projectSizeFilter = new BasicDBObject();
        if(null!=projectSizelt){
        	projectSizeFilter.put("$gte",projectSizelt);
    	}
        if(null!=projectSizegt){
        	projectSizeFilter.put("$lte",projectSizegt);
        }
        if(projectSizeFilter.size()>0){
        	queryAndWhere.put("policyDecision.projectSize", projectSizeFilter);
        }
        
        queryAndWhere.put("status", new BasicDBObject("$in", new Object[]{"3"}));
        limit = userCollection.find(queryAndWhere).sort(sortVal).skip((page-1)*pageSize).limit(pageSize).iterator();
    	List<Document>  docList = CrudUtil.convertCursorToList(limit);
    	for(int i=0;i<docList.size();i++){
    		String reportlId=docList.get(i).get("_id").toString();
    		BasicDBObject query = new BasicDBObject();
    		query.put("reportlId",reportlId);
    		Document docMeetig = DbUtil.getColl(DocumentName).find(query).first();
    		if(docList!=null && null!=docMeetig){
    			docList.get(i).append("meetingTime", docMeetig.get("meetingTime"));
    			docList.get(i).append("projectRating", docMeetig.get("projectRating"));
    			docList.get(i).append("isUrgent",docMeetig.get("isUrgent"));
    		}
    		//查所属大区
    		IFormalAssesmentService formalAssessment = (IFormalAssesmentService) SpringUtil.getBean("formalAssesmentService");
    		String projectFormalId = (String) docList.get(i).get("projectFormalId");
    		Map<String, Object> formalInfo = formalAssessment.queryMongoById(projectFormalId);
    		docList.get(i).append("formalInfo", formalInfo);
    	}
    	
		/*获取总条数*/
		limitcount = userCollection.find(queryAndWhere).sort(sortVal).iterator();
		List<Document> docListcount = CrudUtil.convertCursorToList(limitcount);
		Map<String, Object> totalMap = this.queryTotalProjectSize();
		long total = docListcount.size();
		PageMongoDBUtil pageUtil=new  PageMongoDBUtil(Long.toString(total),pageSize.toString(),page.toString(),totalMap,docList);
		return pageUtil;
	}
	
	/**
	 * 待决策
	 * */
	public PageMongoDBUtil getPolicyDecisionTwo(String json){
		Document doc = Document.parse(json);
		//当前页默认0
		Integer page=1,pageSize=10;
		Object o=doc.get("currentPage");
		if(null != o && !"".equals(o) && !"null".equals(o)){
			page=(Integer) o;
		}	
		//默认显示10
		Object itemsp=doc.get("itemsPerPage");
		if(null != itemsp && !"".equals(itemsp) && !"null".equals(itemsp)){
			pageSize=(Integer) itemsp;
		}
        MongoCollection<Document> userCollection = DbUtil.getColl(DocumentNameReport);
        MongoCursor<Document> limit = null,limitcount=null;  
        BasicDBObject sortVal =new BasicDBObject();
        String ordert=(String) doc.get("ORDERT");
        if(null!=ordert){
        	sortVal.put("policyDecision.submitDate",Integer.valueOf(ordert));
        }
        String orderg=(String) doc.get("ORDERG");
        if(null!=orderg){
        	sortVal.put("policyDecision.projectSize",Integer.valueOf(orderg));
        }
        String orderj=(String) doc.get("ORDERJ");
        if(null!=orderj){
        	sortVal.put("policyDecision.investmentAmount",Integer.valueOf(orderj));
        }
        if(null==ordert && null==orderg && null==orderj){
        	sortVal.put("policyDecision.submitDate",-1);
        }
        BasicDBObject queryAndWhere =new BasicDBObject();
        String projectName=null,projectType=null,reportingUnit=null,user_id=null;
        Double projectSizelt=null;
        Double projectSizegt=null;
        BasicDBObject queryVal3 =new BasicDBObject();
        queryVal3.put("$ne",null);
    	queryAndWhere.put("policyDecision", queryVal3);
        if(!"".equals(doc.get("projectName")) && null!=doc.get("projectName")){
        	projectName = doc.get("projectName").toString();
        }
        
        if(!"".equals(doc.get("projectType")) && null!=doc.get("projectType")){
        	projectType = doc.get("projectType").toString();
        }
        
        
        if(!"".equals(doc.get("reportingUnit")) && null!=doc.get("reportingUnit")){
        	reportingUnit = doc.get("reportingUnit").toString();
        }
        
        //规模
        if(!"".equals(doc.get("projectSizelt")) && null!=doc.get("projectSizelt")){
        	projectSizelt = Double.parseDouble((String) doc.get("projectSizelt")) ;
        }
        //规模
        if(!"".equals(doc.get("projectSizegt")) && null!=doc.get("projectSizegt")){
        	projectSizegt = Double.parseDouble((String) doc.get("projectSizegt")) ;
        }
        
        //大区预留
        /*if(!"".equals(doc.get("reportingUnit")) && null!=doc.get("reportingUnit")){
        	reportingUnit = doc.get("reportingUnit").toString();
        }*/
        
        if(!"".equals(doc.get("user_id")) && null!=doc.get("user_id")){
        	user_id = doc.get("user_id").toString();
        }
        if(null!=projectName){
    		Pattern pattern = Pattern.compile("^.*"+projectName+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("projectName", pattern);
    	} 
        if(null!=projectType){
    		Pattern pattern = Pattern.compile("^.*"+projectType+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("policyDecision.projectType.ITEM_NAME", pattern);
    	}
        BasicDBObject projectSizeFilter = new BasicDBObject();
        if(null!=projectSizelt){
        	projectSizeFilter.put("$gte",projectSizelt);
    	}
        if(null!=projectSizegt){
        	projectSizeFilter.put("$lte",projectSizegt);
        }
        
        
        if(projectSizeFilter.size()>0){
        	queryAndWhere.put("policyDecision.projectSize", projectSizeFilter);
        }
        if(null!=reportingUnit){
    		Pattern pattern = Pattern.compile("^.*"+reportingUnit+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("reportingUnit", pattern);
    	} 
    	queryAndWhere.put("status", new BasicDBObject("$in", new String[]{"1", "2"}));
    	
        limit = userCollection.find(queryAndWhere).sort(sortVal).skip((page-1)*pageSize).limit(pageSize).iterator();
    	List<Document>  docList = CrudUtil.convertCursorToList(limit);
    	
    	for(int i=0;i<docList.size();i++){
    		String reportlId=docList.get(i).get("_id").toString();
    		BasicDBObject query = new BasicDBObject();
    		query.put("reportlId",reportlId);
    		Document docMeetig = DbUtil.getColl(DocumentName).find(query).first();
    		if(null!=docMeetig){
    			docList.get(i).append("projectRating", docMeetig.get("projectRating"));
    			docList.get(i).append("meetingTime", docMeetig.get("meetingTime"));
    			docList.get(i).append("isUrgent", docMeetig.get("isUrgent")==null?"0":docMeetig.get("isUrgent").toString());
    			docList.get(i).append("decisionOpinionList", docMeetig.get("decisionOpinionList"));
    		}
    		//查所属大区
    		IFormalAssesmentService formalAssessment = (IFormalAssesmentService) SpringUtil.getBean("formalAssesmentService");
    		String projectFormalId = (String) docList.get(i).get("projectFormalId");
    		Map<String, Object> formalInfo = formalAssessment.queryMongoById(projectFormalId);
    		docList.get(i).append("formalInfo", formalInfo);
    	}
		/*获取总条数*/
		limitcount = userCollection.find(queryAndWhere).sort(sortVal).iterator();
		List<Document> docListcount = CrudUtil.convertCursorToList(limitcount);
		Map<String, Object> totalMap = this.queryTotalProjectSize();
		long total = docListcount.size();
		PageMongoDBUtil pageUtil=new  PageMongoDBUtil(Long.toString(total),pageSize.toString(),page.toString(),totalMap,docList);
		return pageUtil;
	
	}
	public List<Document> getPolicyDecisionTodayAll(){
//		Document doc = Document.parse(json);
        MongoCollection<Document> userCollection = DbUtil.getColl(DocumentName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
		String  createDate= dateFormat.format(new Date());
        BasicDBObject queryAndWhere =new BasicDBObject();
    	queryAndWhere.put("meetingTime", createDate);
    	MongoCursor<Document> limit = userCollection.find(queryAndWhere).iterator();
		List<Document> docList = CrudUtil.convertCursorToList(limit);
		return docList;
	}
	
	//根据ID查询更新评审负责人
	@SuppressWarnings("unchecked")
	public Map<String, Object> findFormalAndReport(String id){
		Map<String, Object> param = new HashMap<String, Object>();
		BasicDBObject queryAndWhere =new BasicDBObject();
		BasicDBObject queryAndWhere2 =new BasicDBObject();
		BasicDBObject queryAndWhere3 =new BasicDBObject();
		//String reportID=doc.get("reportlId").toString();
		
		//1.正式评审报告ID 2.上会ID，3.正式申请
		queryAndWhere.put("_id",new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentNameReport).find(queryAndWhere).first();
		doc.put("_id", doc.get("_id").toString());
		
		String formID=doc.get("projectFormalId").toString();
		queryAndWhere2.put("formalId", formID);
		Document doc2 =DbUtil.getColl(DocumentName).find(queryAndWhere2).first();
		if(null!=doc2){
			doc2.put("_id", doc2.get("_id").toString());
		}
		//String meetID=doc2.get("_id").toString();
		queryAndWhere3.put("_id", new ObjectId(formID));
		Document doc3 =DbUtil.getColl(DocumentNameFormal).find(queryAndWhere3).first();
		doc3.put("_id", doc3.get("_id").toString());
		
		//获取附件类型
		List<Document> attachment = (List<Document>) doc3.get("attachment");
		List<Document> attach = new ArrayList<Document>();
		for (Document document : attachment) {
			Document aa = new Document();
			String item_name = document.getString("ITEM_NAME");
			String uuid = document.getString("UUID");
			aa.put("ITEM_NAME", item_name);
			aa.put("UUID", uuid);
			attach.add(aa);
		}
		param.put("Report", doc);
		param.put("Meeting", doc2);
		param.put("Formal", doc3);
		param.put("attach", attach);
		return param;
	}
	public  Document getMeetingByIDNotice(String id) {
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		Document doc = DbUtil.getColl(DocumentName).find(query).first();
		doc.put("_id", doc.get("_id").toString());
		return doc;
	}
	public Document listOne(String id) {
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		Document doc = DbUtil.getColl(DocumentName).find(query).first();
		doc.put("_id", doc.get("_id").toString());
		
		
		IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
		BasicDBObject filter = new BasicDBObject();
		filter.put("projectFormalId", doc.getString("formalId"));
		
		List<Map<String, Object>> reports = baseMongo.queryByCondition(filter, Constants.RCM_FORMALREPORT_INFO);
		if(reports != null && reports.size() > 0){
			Map<String, Object> report = reports.get(0);
			doc.put("fkPsResult", report.get("fkPsResult"));
			doc.put("fkRiskTip", report.get("fkRiskTip"));
		}
		return doc;
	}
	//判断是否已经上会如果没有则新建
	public Document findFormalPrassesmentByFormalId(String id) {
		BasicDBObject query = new BasicDBObject();
		query.put("reportlId", id);
		Document doc = DbUtil.getColl(DocumentName).find(query).first();
		if(null!=doc){
			doc.put("_id", doc.get("_id").toString());
			IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
			BasicDBObject filter = new BasicDBObject();
			filter.put("projectFormalId", doc.getString("formalId"));
			
			List<Map<String, Object>> reports = baseMongo.queryByCondition(filter, Constants.RCM_FORMALREPORT_INFO);
			if(reports != null && reports.size() > 0){
				Map<String, Object> report = reports.get(0);
				doc.put("fkPsResult", report.get("fkPsResult"));
				doc.put("fkRiskTip", report.get("fkRiskTip"));
			}
		}
		return doc;
	}
	//添加信息
	public String create(String json){
		Document doc = Document.parse(json);
		//风控中心评审结论，风控重点风险提示，保存到report集合中
		String fkPsResult = (String) doc.get("fkPsResult");
		String fkRiskTip = (String) doc.get("fkRiskTip");
		
		IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
		BasicDBObject filter = new BasicDBObject();
		filter.put("projectFormalId", doc.getString("formalId"));
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("fkPsResult", fkPsResult);
		data.put("fkRiskTip", fkRiskTip);
		baseMongo.updateSetByFilter(filter , data, Constants.RCM_FORMALREPORT_INFO);
		
		doc.remove("fkPsResult");
		doc.remove("fkRiskTip");
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
		//更新项目信息，设置need_meeting字段
		ProjectInfo prj = (ProjectInfo) SpringUtil.getBean("rcm.ProjectInfo");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("needMeeting", "0");
		params.put("isUrgent", doc.get("isUrgent"));
		prj.updateProjectInfo(doc.getString("formalId"), params);
		return id.toString();
	}
		
	public String updateMeeting(String json){
		Document bson=Document.parse(json);
		
		//风控中心评审结论，风控重点风险提示，保存到report集合中
		String fkPsResult = (String) bson.get("fkPsResult");
		String fkRiskTip = (String) bson.get("fkRiskTip");
		
		IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
		BasicDBObject filter = new BasicDBObject();
		filter.put("projectFormalId", bson.getString("formalId"));
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("fkPsResult", fkPsResult);
		data.put("fkRiskTip", fkRiskTip);
		baseMongo.updateSetByFilter(filter , data, Constants.RCM_FORMALREPORT_INFO);
		
		bson.remove("fkPsResult");
		bson.remove("fkRiskTip");
		
		//修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		BasicDBObject where=new BasicDBObject();
		where.put("_id",new ObjectId(bson.get("_id").toString()));
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		
		bson.put("_id",new ObjectId(bson.get("_id").toString()));
		BasicDBObject updateSetValue=new BasicDBObject("$set",bson);
		
		UpdateResult doc = DbUtil.getColl(DocumentName).updateOne(where,updateSetValue);
		return doc.toString();
	}
	@SuppressWarnings("unchecked")
	public String updateMeetingForPolicyDecision(String json){
		Document bson=Document.parse(json);
		//修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		//BasicDBObject where=new BasicDBObject();
		//where.put("_id",new ObjectId(bson.get("_id").toString()));
		//Document OldDoc = DbUtil.getColl(DocumentName).find(where).first();
//		List<Document> oldDecisionOpinionList =(List<Document>) OldDoc.get("decisionOpinionList");
		/*boolean isTrue=false;
		if(null!=oldDecisionOpinionList){
			for (Document docum : oldDecisionOpinionList) {
				//取出原始文件名称
				String val=(String) docum.get("userId");
				
				List<Document> newDoc= (List<Document>) bson.get("decisionOpinionList");
				for (Document document : newDoc) {
					if(document.get("userId").equals(val)){
						isTrue=true;
						docum.put("aagreeOrDisagree", document.get("aagreeOrDisagree"));
						docum.put("particularView", document.get("particularView"));
					}
				}
				
			}
		}
		if(!isTrue){
			List<Document> newDoc= (List<Document>) bson.get("decisionOpinionList");
			for (Document document : newDoc) {
				if(oldDecisionOpinionList==null){
					oldDecisionOpinionList =new ArrayList<Document>();
				}
				oldDecisionOpinionList.add(document);
			}
		}*/
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		//OldDoc.put("decisionOpinionList", bson.get("decisionOpinionList"));
		List<Document> options = (List<Document>) bson.get("decisionOpinionList");
		BasicDBObject each = new BasicDBObject("$each",options);
		BasicDBObject field = new BasicDBObject("decisionOpinionList",each);
		BasicDBObject addToSet = new BasicDBObject("$addToSet",field);
		BasicDBObject idWhere = new BasicDBObject("_id",new ObjectId(bson.get("_id").toString()));
		//bson.put("_id",new ObjectId(bson.get("_id").toString()));
		//BasicDBObject updateSetValue=new BasicDBObject("$set",OldDoc);
		UpdateResult doc = DbUtil.getColl(DocumentName).updateOne(idWhere,addToSet);
		return doc.toString();
	}

	public String saveMeetingByID(String json) throws ParseException {
		BasicDBObject queryAndWhere = new BasicDBObject();
		Document bson = Document.parse(json);
		String id = bson.get("_id").toString();
		List<Document> docList = (List<Document>)bson.get("children");
		for (Document d : docList) {
			// 查询或者修改数据前先将where条件的id转为 ObjecId类型
			queryAndWhere.put("_id", new ObjectId(d.getString("_id")));
			Document doc = DbUtil.getColl(DocumentName).find(queryAndWhere).first();
			// 修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
			doc.put("projectRating", d.get("projectRating"));
			doc.put("projectType1", d.get("projectType1"));
			doc.put("projectType2", d.get("projectType2"));
			doc.put("projectType3", d.get("projectType3"));
			doc.put("ratingReason", d.get("ratingReason"));
			doc.put("division", d.get("division"));
			doc.put("investment", d.get("investment"));
			doc.put("contacts", d.get("contacts"));
			doc.put("telephone", d.get("telephone"));
			doc.put("mark", d.get("mark"));
			doc.put("agenda", d.get("agenda"));
			doc.put("state", 1);
		    doc.put("minute", d.get("minute"));
		    doc.put("noticeId", bson.get("noticeId"));
			doc.put("meetingTime", bson.get("meetingTime"));
			doc.put("submitDate", bson.get("submitDate"));
			doc.put("startTime", d.get("startTime"));
			doc.put("endTime", d.get("endTime"));
			doc.put("huiYiQiCi", bson.get("huiYiQiCi"));
			doc.put("jueCeHuiYiZhuXiId", bson.get("jueCeHuiYiZhuXiId"));
			doc.put("decisionMakingCommitteeStaffNum", bson.get("decisionMakingCommitteeStaffNum"));
			doc.put("decisionMakingCommitteeStaff", bson.get("decisionMakingCommitteeStaff"));
			doc.put("openMeetingPerson", bson.get("openMeetingPerson"));
			BasicDBObject updateSetValue = new BasicDBObject("$set", doc);
			DbUtil.getColl(DocumentName).updateOne(queryAndWhere, updateSetValue);
			
			String projectId=d.getString("_id");
			BasicDBObject queryAndWhere1 =new BasicDBObject();
			queryAndWhere1.put("_id",new ObjectId(projectId));
			Document docMeetingInfo =DbUtil.getColl(DocumentName).find(queryAndWhere1).first();
			String businessId = docMeetingInfo.getString("formalId");
			Map<String, Object> params = new HashMap<String, Object>();
			//保存上会时间到oracle rcm_project_info
			String startTime = d.getString("startTime");
			String meetingDate = bson.getString("meetingTime");
			Date md = concatDateWithTime(meetingDate, startTime);
			params.put("meetingDate", md);
			ProjectInfo prj = (ProjectInfo) SpringUtil.getBean("rcm.ProjectInfo");
			prj.updateProjectInfo(businessId,params);
			
			//修改报告的审阅状态为2
			DbUtil.getColl(Constants.RCM_FORMALREPORT_INFO)
				.updateOne(new BasicDBObject("projectFormalId", businessId), 
						new BasicDBObject("$set", new BasicDBObject("status", "2")));
			
			/**********给报告表回填上会日期************/
			/*BasicDBObject queryAndWhere2 =new BasicDBObject();
			String reportId = docMeetingInfo.getString("reportlId");
			queryAndWhere2.put("_id", new ObjectId(reportId));
			Document docreport = DbUtil.getColl(DocumentNameReport).find(queryAndWhere2).first();
			docreport.put("meetingTime",meetingDate);
			BasicDBObject updateSetValuereport = new BasicDBObject("$set", docreport);
			DbUtil.getColl(DocumentNameReport).updateOne(queryAndWhere2, updateSetValuereport);*/
			
			//发送会议通知
//			sendNotice(docMeetingInfo);
		}
		//TODO 添加中间表
		String noticeId = bson.getString("noticeId");
		String meetingTime=bson.getString("meetingTime");
		Document doc1 = new Document();
		doc1.put("noticeId", noticeId);
		doc1.put("meetingTime", meetingTime);
		doc1.put("meetinginfoId", id);
		
		BasicDBObject query1 =new BasicDBObject();
		query1.put("noticeId", noticeId);
//		query1.put("meetingTime", meetingTime);
		Document doc2 =DbUtil.getColl(DocumentName1).find(query1).first();
		if(null==doc2){
			DbUtil.getColl(DocumentName1).insertOne(doc1);
		}
		return id.toString();
	}
	
	/**
	 * 发送会议通知
	 */
	/*private void sendNotice(Document doc) {
		String projectName = doc.getString("projectName");
		Map<String, Object> noticeTemplate = new HashMap<String, Object>();
		noticeTemplate.put("infoSubject", projectName+"上会通知");
		noticeTemplate.put("businessId", doc.getString("formalId"));
		noticeTemplate.put("formKey", "MeetingApplyNotice/"+doc.get("_id").toString());
//		noticeTemplate.put("createBy", doc.getString("user_id"));
		noticeTemplate.put("type", "1");
		noticeTemplate.put("custText01", "上会通知");
		Map<String, String> readers = getNoticeReaders(doc);
		List<Map<String, Object>> noticeList = new ArrayList<Map<String, Object>>();
		if(Util.isNotEmpty(readers)){
			for(String key : readers.keySet()){
				Map<String, Object> notice = new HashMap<String, Object>();
				notice.putAll(noticeTemplate);
				notice.put("reader", key);
				notice.put("readerName", readers.get(key));
				noticeList.add(notice);
			}
			NoticeInfo nt = new NoticeInfo();
			nt.batchInsert(noticeList);
			
			//同步给门户系统
			PortalClient pc = new PortalClient("", (String)noticeTemplate.get("businessId"));
			pc.start();
		}
	}*/
	
	/**
	 * 查询会议通知接收人
	 * @param doc
	 * @return
	 */
	private Map<String, String> getNoticeReaders(Document doc) {
		Map<String, String> readers = new HashMap<String, String>();
		//查找表单上的人
		//其他参会人区域事业部人员
		List<Map<String, String>> divisions = (ArrayList<Map<String, String>>)doc.get("division");
		addReader(readers, divisions);
		//投资中心人员
		List<Map<String, String>> investment = (ArrayList<Map<String, String>>)doc.get("investment");
		addReader(readers, investment);
		//联系人
		Document contacts = (Document)doc.get("contacts");
		if(Util.isNotEmpty(contacts)){
			if(null!=contacts && !"".equals(contacts)){
				readers.put(contacts.getString("value"), contacts.getString("name"));
			}
		}
		//汇报人
		List<Map<String, Object>> agenda = (ArrayList<Map<String, Object>>)doc.get("agenda");
		if(Util.isNotEmpty(agenda)){
			for(Map<String, Object> map : agenda){
				if(null!=map && !"".equals(map)){
					Object o = map.get("agendaName");
					if(o instanceof Map){
						Map<String, String> ag = (Map<String, String>)o;
						readers.put(ag.get("value"), ag.get("name"));
					}
				}
			}
		}
		//决策委员会委员
		List<Map<String, String>> decisionMakingCommitteeStaff = (ArrayList<Map<String, String>>)doc.get("decisionMakingCommitteeStaff");
		if(Util.isNotEmpty(decisionMakingCommitteeStaff)){
			for(Map<String, String> map : decisionMakingCommitteeStaff){
				if(null!=map && !"".equals(map)){
					readers.put(map.get("VALUE"), map.get("NAME"));
				}
			}
		}
		//查询正式评审单上的所有相关人
		String formalId = doc.getString("formalId");
		if(Util.isNotEmpty(formalId)){
			ProjectRelation pr = new ProjectRelation();
			String json = "{businessId:'"+formalId+"'}";
			List<Map<String, Object>> list = pr.findRelationUserByBusinessId(json);
			if(Util.isNotEmpty(list)){
				for(Map<String, Object> map : list){
					if(null!=map && !"".equals(map)){
						readers.put((String)map.get("VALUE"), (String)map.get("NAME"));
					}
				}
			}
		}
		return readers;
	}
	/**
	 * @param readers
	 * @param divisions
	 */
	private void addReader(Map<String, String> readers, List<Map<String, String>> users) {
		if(Util.isNotEmpty(users)){
			for(Map<String, String> map : users){
				readers.put(map.get("value"), map.get("name"));
			}
		}
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
	
	public ArrayList getMeetingByID(String id) {
		String[] arrayID = {};
		arrayID = id.split(",");
		ArrayList list = new ArrayList();
		for (int i = 0; i < arrayID.length; i++) {
			BasicDBObject query = new BasicDBObject();
			query.put("_id", new ObjectId(arrayID[i]));
			Document doc = DbUtil.getColl(DocumentName).find(query).first();
			doc.put("_id", doc.get("_id").toString());
			list.add(doc);
		}
		return list;
	}
	
	public Document getMeetingByIDMeetingtime(String id) {
		BasicDBObject query = new BasicDBObject();
		query.put("meetinginfoId", id);
		Document doc = DbUtil.getColl(DocumentName1).find(query).first();
		return doc;
		}
	
	public boolean getMeetingByIDReportID(String id) {
		boolean istrue=true;
		BasicDBObject query = new BasicDBObject();
		query.put("reportlId", id);
		Document doc = DbUtil.getColl(DocumentName).find(query).first();
		if(null==doc){
			istrue=false;
		}
		return istrue;
	}
	
	public String saveOneMeeting(String json) {
		BasicDBObject queryAndWhere = new BasicDBObject();
		Document bson = Document.parse(json);
		String id = bson.get("_id").toString();
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc = DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		// 修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id", new ObjectId(id));
		doc.put("startTime", bson.get("startTime"));
		doc.put("meetingTime", bson.get("meetingTime"));
		doc.put("endTime", bson.get("endTime"));
		doc.put("minute", bson.get("minute"));
		doc.put("decisionMakingCommitteeStaff", bson.get("decisionMakingCommitteeStaff"));
		doc.put("decisionMakingCommitteeStaffNum", bson.get("decisionMakingCommitteeStaffNum"));
		BasicDBObject updateSetValue = new BasicDBObject("$set", doc);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere, updateSetValue);
		return result.toString();
	}
	/**
	 * 导出会议列表
	 * @throws IOException 
	 * */
	public Map<String,String> meetingListReport() throws IOException{
		MongoCursor<Document> limit = DbUtil.getColl(DocumentName).find().iterator();
        List<Document> docList = CrudUtil.convertCursorToList(limit);
		List<Object> list=new ArrayList<Object>();
		for (Document document : docList) {
			Document d= (Document) document.get("projectRating");
			String RatingName=null;
			if(null!=d){
				if(null!=d.get("name")){
					RatingName=d.get("name").toString();
				}
			}
			Map<String,String> map=new HashMap<String,String>();
			map.put("projectName", (String) document.get("projectName"));
			map.put("projectForm",(String) document.get("projectForm"));
			map.put("projectRatingName",RatingName);
			map.put("submitDate",document.get("submitDate")!=null?document.get("submitDate").toString():null);
			map.put("state",document.get("state").toString());
			list.add(map);
		}
		String[] arr = new String[] {"项目名称","业务类型","项目评级","参会信息提交时间","状态"};
		String[] arrColumn = new String[] {"projectName","projectForm","projectRatingName","create_date","state"};
		return ExportExcel.exportExcel("会议管理列表","meetingListReport", arr,arrColumn,list);
	}
	
	/**
	 * 导出决策委员会审阅列表
	 * @throws IOException 
	 * */
	public Map<String,String> formalReviewListReport() throws IOException{
		/*MongoCursor<Document> limit = DbUtil.getColl(DocumentName).find().iterator();
        List<Document> docList = CrudUtil.convertCursorToList(limit);
		List<Object> list=new ArrayList<Object>();
		for (Document document : docList) {
			Document d= (Document) document.get("contacts");
			String RatingName=null;
			if(null!=d){
				if(null!=d.get("name")){
					RatingName=d.get("name").toString();
				}
			}
			Map<String,String> map=new HashMap<String,String>();
			map.put("projectName", (String) document.get("projectName"));
			map.put("contacts",RatingName);
			map.put("meetingTime",(String) document.get("meetingTime"));
			list.add(map);
		}
		String[] arr = new String[] {"项目名称","联系人","上会日期"};
		String[] arrColumn = new String[] {"projectName","contacts","meetingTime"};*/
	    MongoCollection<Document> userCollection = DbUtil.getColl(DocumentNameReport);
	    BasicDBObject queryAndWhere =new BasicDBObject();
	    BasicDBObject queryVal3 =new BasicDBObject();
	    queryVal3.put("$ne",null);
		queryAndWhere.put("policyDecision", queryVal3);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
		String  nowDate= dateFormat.format(new Date());
		queryAndWhere.append(  
	            QueryOperators.OR,  
	            new BasicDBObject[] { new BasicDBObject("meetingTime", new BasicDBObject("$lt",nowDate)),  
	                    new BasicDBObject("meetingTime", new BasicDBObject("$eq",null)) });
		
		MongoCursor<Document> limit = userCollection.find(queryAndWhere).iterator();
		List<Document>  docList = CrudUtil.convertCursorToList(limit);
		
		for(int i=0;i<docList.size();i++){
			String reportlId=docList.get(i).get("_id").toString();
			BasicDBObject query = new BasicDBObject();
			query.put("reportlId",reportlId);
			Document docMeetig = DbUtil.getColl(DocumentName).find(query).first();
			if(null!=docMeetig){
				docList.get(i).append("projectRating", docMeetig.get("projectRating"));
				docList.get(i).append("isUrgent", docMeetig.get("isUrgent"));
			}
		}
		List<Object> list=new ArrayList<Object>();
		for (Document document : docList) {
			Document d= (Document) document.get("projectRating");
			String RatingName=null,projectTypeName=null;
			if(null!=d){
				if(null!=d.get("name")){
					RatingName=d.get("name").toString();
				}
			}
			Document policyDecision= (Document) document.get("policyDecision");
			String projectSize=null,investmentAmount=null,submitDate=null,reportingUnit=null,meetingTime=null;
			String isUrgent="否";
			if(null!=policyDecision){
				if(null!=policyDecision.get("projectType")){
					Document projectType= (Document) policyDecision.get("projectType");
					if(null!=projectType){
						if(null!=projectType.get("ITEM_NAME")){
							projectTypeName=projectType.get("ITEM_NAME").toString();
						}
					}
				}
				
				if(null!=document.get("isUrgent")){
					if(document.get("isUrgent").equals("1")){
						isUrgent="是";
					}
				}
				projectSize=policyDecision.get("projectSize")!=null?policyDecision.get("projectSize").toString():"0";
				investmentAmount=policyDecision.get("investmentAmount")!=null?policyDecision.get("investmentAmount").toString():"0";
				submitDate=policyDecision.get("submitDate")!=null?policyDecision.get("submitDate").toString():null;
				reportingUnit=document.get("reportingUnit")!=null?document.get("reportingUnit").toString():null;
				meetingTime=document.get("meetingTime")!=null?document.get("meetingTime").toString():null;
			}
			Map<String,String> map=new HashMap<String,String>();
			map.put("projectName",document.get("projectName")!=null?document.get("projectName").toString():null);
			map.put("projectSize",projectSize+" 万吨");
			map.put("investmentAmount",investmentAmount+" 万");
			map.put("submitDate",submitDate);
			map.put("projectType",projectTypeName);
			map.put("reportingUnit",reportingUnit);
			map.put("submitDate",submitDate);
			map.put("projectRating",RatingName);
			map.put("isUrgent",isUrgent);
			map.put("meetingTime",meetingTime);
			list.add(map);
		}
		String[] arr = new String[] {"项目名称","项目规模","投资金额","上报评审时间","项目类型","申报单位","是否紧急上会","项目评级","提交日期","上会日期"};
		String[] arrColumn = new String[] {"projectName","projectSize","investmentAmount","submitDate","projectType","reportingUnit","isUrgent","projectRating","submitDate","meetingTime"};
		return ExportExcel.exportExcel("决策委员会审阅列表","formalReviewListReport", arr,arrColumn,list);
	}
	/**
	 * 根据工单号查上会信息
	 * @param businessId
	 * @return
	 */
	
	/**
	 * 导出决策委员会审阅列表
	 * @throws IOException 
	 * */
	public Map<String,String> formalReviewListReporttwo() throws IOException{
	    MongoCollection<Document> userCollection = DbUtil.getColl(DocumentNameReport);
	    BasicDBObject queryAndWhere =new BasicDBObject();
	    BasicDBObject queryVal3 =new BasicDBObject();
	    queryVal3.put("$ne",null);
		queryAndWhere.put("policyDecision", queryVal3);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
		String  nowDate= dateFormat.format(new Date());
		queryAndWhere.append(  
	            QueryOperators.AND,  
	            new BasicDBObject[] { new BasicDBObject("meetingTime", new BasicDBObject("$gte",nowDate)),  
	                    new BasicDBObject("meetingTime", new BasicDBObject("$ne",null))});
		MongoCursor<Document> limit = userCollection.find(queryAndWhere).iterator();
		List<Document>  docList = CrudUtil.convertCursorToList(limit);
		
		for(int i=0;i<docList.size();i++){
			String reportlId=docList.get(i).get("_id").toString();
			BasicDBObject query = new BasicDBObject();
			query.put("reportlId",reportlId);
			Document docMeetig = DbUtil.getColl(DocumentName).find(query).first();
			if(null!=docMeetig){
				docList.get(i).append("projectRating", docMeetig.get("projectRating"));
				docList.get(i).append("isUrgent", docMeetig.get("isUrgent"));
			}
		}
		List<Object> list=new ArrayList<Object>();
		for (Document document : docList) {
			Document d= (Document) document.get("projectRating");
			String RatingName=null,projectTypeName=null;
			if(null!=d){
				if(null!=d.get("name")){
					RatingName=d.get("name").toString();
				}
			}
			Document policyDecision= (Document) document.get("policyDecision");
			String projectSize=null,investmentAmount=null,submitDate=null,reportingUnit=null,meetingTime=null;
			String isUrgent="否";
			if(null!=policyDecision){
				if(null!=policyDecision.get("projectType")){
					Document projectType= (Document) policyDecision.get("projectType");
					if(null!=projectType){
						if(null!=projectType.get("ITEM_NAME")){
							projectTypeName=projectType.get("ITEM_NAME").toString();
						}
					}
				}
				if(null!=document.get("isUrgent")){
					if(document.get("isUrgent").equals("1")){
						isUrgent="是";
					}
				}
				projectSize=policyDecision.get("projectSize")!=null?policyDecision.get("projectSize").toString():"0";
				investmentAmount=policyDecision.get("investmentAmount")!=null?policyDecision.get("investmentAmount").toString():"0";
				submitDate=policyDecision.get("submitDate")!=null?policyDecision.get("submitDate").toString():null;
				reportingUnit=document.get("reportingUnit")!=null?document.get("reportingUnit").toString():null;
				meetingTime=document.get("meetingTime")!=null?document.get("meetingTime").toString():null;
			}
			Map<String,String> map=new HashMap<String,String>();
			map.put("projectName",document.get("projectName")!=null?document.get("projectName").toString():null);
			map.put("projectSize",projectSize+" 万吨");
			map.put("investmentAmount",investmentAmount+" 万");
			map.put("submitDate",submitDate);
			map.put("projectType",projectTypeName);
			map.put("reportingUnit",reportingUnit);
			map.put("submitDate",submitDate);
			map.put("projectRating",RatingName);
			map.put("isUrgent",isUrgent);
			map.put("meetingTime",meetingTime);
			list.add(map);
		}
		String[] arr = new String[] {"项目名称","项目规模","投资金额","上报评审时间","项目类型","申报单位","是否紧急上会","项目评级","提交日期","上会日期"};
		String[] arrColumn = new String[] {"projectName","projectSize","investmentAmount","submitDate","projectType","reportingUnit","isUrgent","projectRating","submitDate","meetingTime"};
		return ExportExcel.exportExcel("决策委员会审阅列表","formalReviewListReport", arr,arrColumn,list);
	}
	
	public Map<String, Object> queryByBusinessId(String businessId){
		BasicDBObject queryAndWhere =new BasicDBObject();
		queryAndWhere.put("formalId", businessId);
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		return doc;
	}
	/**
	 * @param type
	 * @return
	 */
	private Map<String, Object> queryTotalProjectSize(){
		Map<String, Object> map=new HashMap<String,Object>();
		IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
		
		List<BasicDBObject> list = new ArrayList<BasicDBObject>();
		
		BasicDBObject match = new BasicDBObject("$match", new BasicDBObject("status", "3"));
		
		DBObject fields = new BasicDBObject("projectNo", 1); 
		fields.put("policyDecision.projectSize", 1);
		fields.put("policyDecision.investmentAmount", 1);
		  
		BasicDBObject project = new BasicDBObject("$project", fields);  
		
		// 利用$group进行分组  
//		DBObject _group = new BasicDBObject("projectNo", "$projectNo");  
//		_group.put("projectSizes", "$policyDecision.projectSize");  
//		_group.put("investmentAmounts", "$policyDecision.investmentAmount");  
//		_group.put("optCode", "$optCode");  
		
		DBObject groupFields = new BasicDBObject("_id", "nummm");                    
		//总数  
		groupFields.put("count", new BasicDBObject("$sum", 1));  
		//求和      
		groupFields.put("sumProjectSize", new BasicDBObject("$sum", "$policyDecision.projectSize"));
		groupFields.put("sumInvestmentAmount", new BasicDBObject("$sum", "$policyDecision.investmentAmount"));
		BasicDBObject group = new BasicDBObject("$group", groupFields);
		           
		list.add(match);
		list.add(project);
		list.add(group);
		AggregateIterable<Document> output = baseMongo.aggregate(Constants.RCM_FORMALREPORT_INFO, list);
		Document doc = output.first(); 
		Double projectSizeTotalDbl = 0d, sumInvestmentAmountDbl = 0d;
		if(doc != null){
			try {
				projectSizeTotalDbl = Double.parseDouble(doc.get("sumProjectSize").toString());
			} catch (NumberFormatException e) {
				logger.error(Util.parseException(e));
			}
			try {
				sumInvestmentAmountDbl = Double.parseDouble(doc.get("sumInvestmentAmount").toString());
			} catch (NumberFormatException e) {
				logger.error(Util.parseException(e));
			}
		}
		//金额
		BigDecimal investmentAmountTotal = new BigDecimal(sumInvestmentAmountDbl);
		//规模
		BigDecimal projectSizeTotal = new BigDecimal(projectSizeTotalDbl);
		BigDecimal tenThousand = new BigDecimal(10000);
		if(investmentAmountTotal.compareTo(tenThousand) == 1){
			investmentAmountTotal = investmentAmountTotal.divide(tenThousand);
			map.put("yiyuan", true);
		}
		
		map.put("projectSizeTotal", projectSizeTotal.setScale(2, BigDecimal.ROUND_UP));
		map.put("investmentAmountTotal", investmentAmountTotal.setScale(2, BigDecimal.ROUND_UP));
		
		MongoCollection<Document> collection = baseMongo.getCollection(Constants.RCM_FORMALREPORT_INFO);
		BasicDBObject filter = new BasicDBObject();
		filter.put("policyDecision.projectType.ITEM_CODE", "1404");
		filter.put("status", "3");
		long huanweiNum = collection.count(filter);
		map.put("huanweiNum", huanweiNum);
		return map;
	}
}
