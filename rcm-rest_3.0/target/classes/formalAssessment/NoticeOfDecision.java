package formalAssessment;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import rcm.NoticeOfDecisionInfo;
import rcm.ProjectRelation;
import report.Path;
import util.CrudUtil;
import util.DbUtil;
import util.PageMongoDBUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import com.yk.common.DocReportUtil;
import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;

import common.BaseService;
import common.Constants;
import common.ExportExcel;

public class NoticeOfDecision extends BaseService {
	/**
	 * 正式评审管理
	 */
	private final String DocumentName=Constants.RCM_NOTICEDECISION_INFO;
	
	@Resource
	private IBaseMongo baseMongo;
	//获取列表
	public PageMongoDBUtil getAllold(String json){
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
        sortVal.put("dateOfMeeting",(Integer) doc.get("ORDERVAL"));
        
        BasicDBObject queryAndWhere =new BasicDBObject();
        String projectName=null,evaluationScale=null,dateOfMeeting=null;
        if(!"".equals(doc.get("projectName")) && null!=doc.get("projectName")){
        	projectName = doc.get("projectName").toString();
        }
        if(!"".equals(doc.get("evaluationScale")) && null!=doc.get("evaluationScale")){
        	evaluationScale = doc.get("evaluationScale").toString();
        }
        if(!"".equals(doc.get("dateOfMeeting")) && null!=doc.get("dateOfMeeting")){
        	dateOfMeeting = doc.get("dateOfMeeting").toString();
        }
         
        if(null!=projectName){
    		Pattern pattern = Pattern.compile("^.*"+projectName+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("projectName", pattern);
    	}
    	if(null!=dateOfMeeting){
    		Pattern pattern = Pattern.compile("^.*"+dateOfMeeting+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("dateOfMeeting", pattern);
    	}
    	if(null!=evaluationScale){
    		Pattern pattern = Pattern.compile("^.*"+evaluationScale+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("evaluationScale", pattern);
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
		doc.append("state", 1);
		DbUtil.getColl(DocumentName).insertOne(doc);
		//修改待审阅状态为3，已审阅   
		DbUtil.getColl(Constants.RCM_FORMALREPORT_INFO)
			.updateOne(new BasicDBObject("projectFormalId", doc.get("projectFormalId")), 
				new BasicDBObject("$set", new BasicDBObject("status", "3")));
		//同时向正式申请插入ID，用来过滤项目
		/*String projectFormalId =doc.get("projectFormalId").toString();
		BasicDBObject queryAndWhereFormal =new BasicDBObject();
		queryAndWhereFormal.put("_id", new ObjectId(projectFormalId));
		Document docFormal =DbUtil.getColl(DocumentNameFormal).find(queryAndWhereFormal).first();
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		docFormal.put("_id",new ObjectId(projectFormalId));
		docFormal.put("NoticeOfDecstion",doc.get("_id"));
		BasicDBObject updateSetValueFormal=new BasicDBObject("$set",docFormal);
		DbUtil.getColl(DocumentNameFormal).updateOne(queryAndWhereFormal,updateSetValueFormal);
		ObjectId id= (ObjectId)doc.get("_id");*/
		String id= doc.get("_id").toString();
		
		//插入评审基本信息到项目表
		NoticeOfDecisionInfo prj = (NoticeOfDecisionInfo) SpringUtil.getBean("rcm.NoticeOfDecisionInfo");
		prj.saveNoticeOfDecisionBaseInfo2Oracle(id);
				//插入人员项目关系表
		ProjectRelation pr = (ProjectRelation) SpringUtil.getBean("rcm.ProjectRelation");
		pr.insertWhenStartNotice(id, Constants.FORMAL_NOTICEOFDECSTION);
		return id.toString();
	}
	//根据ID查询更新评审负责人
	public String update(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String id=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		bson.put("_id",new ObjectId(id));
		BasicDBObject updateSetValue=new BasicDBObject("$set",bson);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		
		/*String projectFormalId =bson.get("projectFormalId").toString();
		BasicDBObject queryAndWhereFormal =new BasicDBObject();
		queryAndWhereFormal.put("_id", new ObjectId(projectFormalId));
		Document docFormal =DbUtil.getColl(DocumentNameFormal).find(queryAndWhereFormal).first();
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		docFormal.put("_id",new ObjectId(projectFormalId));
		docFormal.put("NoticeOfDecstion",id);
		BasicDBObject updateSetValueFormal=new BasicDBObject("$set",docFormal);
		DbUtil.getColl(DocumentNameFormal).updateOne(queryAndWhereFormal,updateSetValueFormal);*/
		//更新评审基本信息到项目表
		NoticeOfDecisionInfo prj = (NoticeOfDecisionInfo) SpringUtil.getBean("rcm.NoticeOfDecisionInfo");
		prj.saveNoticeOfDecisionBaseInfo2Oracle(id);
		//插入人员项目关系表
		ProjectRelation pr = (ProjectRelation) SpringUtil.getBean("rcm.ProjectRelation");
		pr.insertWhenStartNotice(id, Constants.FORMAL_NOTICEOFDECSTION);
				
		return id;
	}
	public void delete(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String arrid=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		String[] array={};
		array=arrid.split(",");
		for (String id : array) {
			
			queryAndWhere.put("_id", new ObjectId(id));
			Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
			//同时向正式申请插入ID，用来过滤项目
			/*String projectFormalId =doc.get("projectFormalId").toString();
			BasicDBObject queryAndWhereFormal =new BasicDBObject();
			queryAndWhereFormal.put("_id", new ObjectId(projectFormalId));
			Document docFormal =DbUtil.getColl(DocumentNameFormal).find(queryAndWhereFormal).first();
			//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
			docFormal.put("_id",new ObjectId(projectFormalId));
			docFormal.put("NoticeOfDecstion",null);
			BasicDBObject updateSetValueFormal=new BasicDBObject("$set",docFormal);
			DbUtil.getColl(DocumentNameFormal).updateOne(queryAndWhereFormal,updateSetValueFormal);*/
			
			DbUtil.getColl(DocumentName).deleteOne((Bson) doc);
			
			//return res.toString();
			//删除项目基本信息表中的数据和项目人员关系表中的数据
			ProjectRelation r = (ProjectRelation) SpringUtil.getBean("rcm.ProjectRelation");
			r.deleteProjectInfoByBusinessId(id);
			NoticeOfDecisionInfo prj = (NoticeOfDecisionInfo) SpringUtil.getBean("rcm.NoticeOfDecisionInfo");
			prj.deleteByBusinessId(id);
		}
	}
	
	public Document getNoticeOfDecstionByID(String id){
		BasicDBObject queryAndWhere =new BasicDBObject();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		doc.put("_id", doc.get("_id").toString());
		return doc;
	}
	//部门工作情况-正式评审-投资决策通知书
	public Document getNoticeOfDecstionByFromalID(String id){
		BasicDBObject queryAndWhere =new BasicDBObject();
		queryAndWhere.put("projectFormalId", id);
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		if(null!=doc && !"".equals(doc)){
			doc.put("_id", doc.get("_id").toString());
		}else{
			doc=null;
		}
		return doc;
	}
	
	/**
	 * 导出会议列表
	 * @throws IOException 
	 * */
	public Map<String,String> noticeOfDecisionReport(String uuid) throws IOException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", uuid);
		paramMap.put("code", Constants.ROLE_SYSTEM_ADMIN);
		String o=DbUtil.openSession().selectOne("role.hasRole",paramMap);
		if("1".equals(o)){
			paramMap.put("userId", null);
		}
		
		List<Object> list=DbUtil.openSession().selectList("noticeOfDecisionInfo.selectAll",paramMap);
		
		String[] arr = new String[] {"项目名称","申报单位","会议日期","申请时间","审批状态"};
		String[] arrColumn = new String[] {"PROJECTNAME","REPORTINGUNIT","DATEOFMEETING","APPLY_DATES","APPROVE_STATE"};
		return ExportExcel.exportExcel("决策通知书列表","noticeOfDecisionReport", arr,arrColumn,list);
		
		
		/*MongoCursor<Document> limit = DbUtil.getColl(DocumentName).find().iterator();
        List<Document> docList = CrudUtil.convertCursorToList(limit);
		List<Object> list=new ArrayList<Object>();
		for (Document document : docList) {
			Map<String,String> map=new HashMap<String,String>();
			map.put("projectName", (String) document.get("projectName"));
			map.put("evaluationScale",(String) document.get("evaluationScale")+"万吨/日");
			map.put("dateOfMeeting",(String) document.get("dateOfMeeting"));
			list.add(map);
		}
		String[] arr = new String[] {"项目名称","合同规模","会议日期"};
		String[] arrColumn = new String[] {"projectName","evaluationScale","dateOfMeeting"};
		return ExportExcel.exportExcel("决策通知书列表","noticeOfDecisionReport", arr,arrColumn,list);*/
	}

	/**
	 * 根据申请单ID查询决策通知书信息
	 * */
	public Document getNoticeOfDecstionByProjectFormalID(String projectNo){
		BasicDBObject queryAndWhere =new BasicDBObject();
		queryAndWhere.put("projectNo", projectNo);
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		if(null!=doc && !"".equals(doc)){
			doc.put("_id", doc.get("_id").toString());
		}else{
			doc=null;
		}
		return doc;
	}
	/**
	 * 生成word文档
	 */
	@SuppressWarnings("unchecked")
	public Map<String,String> getNoticeOfDecisionWord(String id){
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document json = Document.parse(id);
		queryAndWhere.put("projectFormalId", (String)json.get("id"));
		Map<String, Object> data = baseMongo.queryByCondition(queryAndWhere, Constants.RCM_NOTICEDECISION_INFO).get(0);
		Map<String, Object> param = new HashMap<String, Object>();  
		param.put("projectName", data.get("projectName"));
		param.put("contractScale", data.get("contractScale"));
		param.put("evaluationScale", data.get("evaluationScale"));
		param.put("reviewOfTotalInvestment",data.get("reviewOfTotalInvestment"));
		Document reportingUnit = (Document) data.get("reportingUnit");
		param.put("reportingUnit",reportingUnit.get("name"));
		
		String addstr ="";
		if( "1".equals(data.get("additionalReview"))){
			addstr = "是";
		}else if( "2".equals(data.get("additionalReview"))){
			addstr = "否";
		}
		param.put("additionalReview", addstr);
		
		param.put("decisionStage",data.get("decisionStage"));
		param.put("dateOfMeeting",data.get("dateOfMeeting"));
		
		String str = "";
		if( "1".equals(data.get("consentToInvestment"))){
			str = "同意投资";
		}else if( "2".equals(data.get("consentToInvestment"))){
			str = "不同意投资";
		}else if( "3".equals(data.get("consentToInvestment"))){
			str = "同意有条件投资";
		}else if( "4".equals(data.get("consentToInvestment"))){
			str = "择期决议";
		}
		param.put("consentToInvestment",str);
		
		param.put("implementationMatters", data.get("implementationMatters"));
		
		Document subjectOfImplementation =(Document) data.get("subjectOfImplementation");
		param.put("subjectOfImplementation",subjectOfImplementation.get("name"));
		param.put("equityRatio",data.get("equityRatio"));
		
		String register ="";
		if( "1".equals(data.get("register"))){
			register = "是";
		}else if( "2".equals(data.get("register"))){
			register = "否";
		}
		param.put("register", register);
		
		param.put("registeredCapital",data.get("registeredCapital"));
		
		Document responsibilityUnit = (Document) data.get("responsibilityUnit");
		param.put("responsibilityUnit", responsibilityUnit.get("name"));
		
		List<Document> personLiable = (List<Document>) data.get("personLiable");
		StringBuffer perNames = new StringBuffer(); 
		for(int i = 0; i < personLiable.size(); i++){
			Document person = personLiable.get(i);
		   if(person != null && person.get("name")!=null && !"".equals(person.get("name"))){
			   perNames.append(person.get("name"))
			   		.append(",");
		   }
		}
		String perNameStr = "";
		if(perNames.length() > 0){
			perNameStr = perNames.substring(0, perNames.length() - 1);
		}
		param.put("personLiable", perNameStr);
		
		param.put("implementationRequirements", data.get("implementationRequirements"));
		param.put("leadershipIssue", data.get("leadershipIssue"));
		
		String template = Path.NoticeOfDecision;
		String outpath = Path.noticeOfDecisionReportPath();
		String filename = data.get("projectName")+"-决策通知书申请单.docx";
		outpath = outpath+filename;
		
		BasicDBObject aa =new BasicDBObject();
		Document json2 = Document.parse(id);
		aa.put("projectFormalId", (String)json2.get("id"));
		Map<String, Object> data2 = baseMongo.queryByCondition(queryAndWhere, Constants.RCM_NOTICEDECISION_INFO).get(0);
		String oldPath = (String) data2.get("reportFilePath");
		
		if(oldPath!=null && !"".equals(oldPath)){
			File file = new File((String) oldPath);
			if(file.exists()){
				file.delete();
			}
		}
		DocReportUtil.generateWord(param, template, outpath);
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		BasicDBObject newDoc = new BasicDBObject();
		newDoc.put("reportFilePath", outpath);
		BasicDBObject updateSetValue=new BasicDBObject("$set", newDoc);
		DbUtil.getColl(DocumentName).updateOne(queryAndWhere, updateSetValue);
		Map<String,String> map =new HashMap<String, String>();
		map.put("filePath", outpath);
		map.put("fileName", filename);
		return map;
	}
	
}
