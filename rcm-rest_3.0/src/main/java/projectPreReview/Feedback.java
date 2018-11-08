package projectPreReview;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.BSONObject;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.QueryOperators;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;

import common.Constants;
import common.ExportExcel;
import util.CrudUtil;
import util.DbUtil;
import util.PageMongoDBUtil;
import util.ThreadLocalUtil;
import util.Util;

public class Feedback {
	
	private static final long serialVersionUID = 1L;
	private final String DocumentName=Constants.RCM_FEEDBACK_INFO;	
	private final String DocumentName1=Constants.PREASSESSMENT;	
	private final String DocumentName2=Constants.RCM_PROJECTFORMALREVIEW_INFO;	
	private final String DocumentName3=Constants.RCM_PROJECTEXPERIENCE_INFO;
	private final String DocumentNameForReport=Constants.RCM_FORMALREPORT_INFO;
	
	public  PageMongoDBUtil listFormaltName(String json){
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
        MongoCollection<Document> userCollection = DbUtil.getColl(DocumentName3);
        MongoCursor<Document> limit = null,limitcount=null;  
        
        BasicDBObject sortVal =new BasicDBObject();
        sortVal.put("create_date",(Integer) doc.get("ORDERVAL"));
        BasicDBObject queryAndWhere =new BasicDBObject();
        String projectName=null,createName=null,createId=null,create_date=null,state=null;
        
        if(!"".equals(doc.get("projectName")) && null!=doc.get("projectName")){
        	projectName = doc.get("projectName").toString();
        }
        if(!"".equals(doc.get("createName")) && null!=doc.get("createName")){
        	createName = doc.get("createName").toString();
        }
        if(!"".equals(doc.get("state")) && null!=doc.get("state")){
        	state = doc.get("state").toString();
        }
        if(!"".equals(doc.get("createId")) && null!=doc.get("createId")){
        	createId = doc.get("createId").toString();
        }
        if(!"".equals(doc.get("create_date")) && null!=doc.get("create_date")){
        	create_date = doc.get("create_date").toString();
        }
       
        if(null!=projectName){
    		Pattern pattern = Pattern.compile("^.*"+projectName+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("projectName", pattern);
    	}
    	
    	if(null!=create_date){
    		Pattern pattern = Pattern.compile("^.*"+create_date+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("create_date", pattern);
    	}
    	
    	if(null!=createName){
    		Pattern pattern = Pattern.compile("^.*"+createName+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("create_Name", pattern);
    	}
    	if(null!=state){
    		queryAndWhere.put("state", state);
    	}
    	
    	if(null!=createId){
    		if(!ThreadLocalUtil.getIsAdmin()){
    			queryAndWhere.put("create_id", createId);
    		}
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
	
	public PageMongoDBUtil getAll(String json){
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
        sortVal.put("projectOpentime",(Integer) doc.get("ORDERVAL"));
        
        BasicDBObject queryAndWhere =new BasicDBObject();
        String projectName=null,projectOpentime=null,tenderResults=null;
        if(!"".equals(doc.get("projectName")) && null!=doc.get("projectName")){
        	projectName = doc.get("projectName").toString();
        }
        if(!"".equals(doc.get("projectOpentime")) && null!=doc.get("projectOpentime")){
        	projectOpentime = doc.get("projectOpentime").toString();
        }
        if(!"".equals(doc.get("tenderResults")) && null!=doc.get("tenderResults")){
        	tenderResults = doc.get("tenderResults").toString();
        }
      
        if(null!=projectName){
    		Pattern pattern = Pattern.compile("^.*"+projectName+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("projectName", pattern);
    	}
    	
    	if(null!=projectOpentime){
    		Pattern pattern = Pattern.compile("^.*"+projectOpentime+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("projectOpentime", pattern);
    	}
    	
    	if(null!=tenderResults){
    		queryAndWhere.put("tenderResults", tenderResults);
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
	
	public PageMongoDBUtil getFormal(String json){
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
        MongoCollection<Document> userCollection = DbUtil.getColl(DocumentName2);
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
	public Document selectName(String id){
		BasicDBObject query =new BasicDBObject();
		query.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName1).find(query).first();
		if(null!=doc){
			doc.put("_id", doc.get("_id").toString());
		}
		return doc;
	}
	public Document listName(String id){
		BasicDBObject query =new BasicDBObject();
		query.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName2).find(query).first();
		if(null!=doc){
			doc.put("_id", doc.get("_id").toString());
		}
		return doc;
	}
	public Document listOne(String id) {
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		Document doc = DbUtil.getColl(DocumentName1).find(query).first();
		if(null!=doc){
			doc.put("_id", doc.get("_id").toString());
		}
		return doc;
	}
	// 根据ID查询
	public Document getFeedbackByID(String id) {
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		Document doc = DbUtil.getColl(DocumentName).find(query).first();
		if(null!=doc){
			doc.put("_id", doc.get("_id").toString());
		}
		return doc;
	}
	public Document getExperienceByID(String id) {
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		Document doc = DbUtil.getColl(DocumentName3).find(query).first();
		if(null!=doc){
			doc.put("_id", doc.get("_id").toString());
		}
		return doc;
	}
	//添加
	public String create(String json){
		Document doc = Document.parse(json);
		//设置添加时间
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
		String  createDate= dateFormat.format(now);
		doc.append("create_date", createDate);
		Format format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		doc.append("currentTimeStamp", format.format(new Date()));
		DbUtil.getColl(DocumentName).insertOne(doc);
		ObjectId id= (ObjectId)doc.get("_id");
		return id.toString();
	}
	public String createExperience(String json){
		Document doc = Document.parse(json);
		//设置添加时间
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
		String  createDate= dateFormat.format(now);
		doc.append("create_date", createDate);
		Format format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		doc.append("currentTimeStamp", format.format(new Date()));
		DbUtil.getColl(DocumentName3).insertOne(doc);
		
		//在报告中添加字段pfrExperience 表示已经创建
		BasicDBObject where=new BasicDBObject();
		where.put("_id",new ObjectId(doc.get("reportId").toString()));
		Document docFormal =DbUtil.getColl(DocumentNameForReport).find(where).first();
		docFormal.put("_id",new ObjectId(docFormal.get("_id").toString()));
		docFormal.put("pfrExperience", "1");
		BasicDBObject updateSetValue=new BasicDBObject("$set",docFormal);
		DbUtil.getColl(DocumentNameForReport).updateOne(where,updateSetValue);
		
		ObjectId id= (ObjectId)doc.get("_id");
		return id.toString();
	}
	//删除
	public String deleteFeedback(String json){
		Document bson = Document.parse(json);
		String id=bson.get("_id").toString();
		
		IBaseMongo baseMongo = (IBaseMongo) SpringUtil.getBean("baseMongo");
		String[] ids = id.split(",");
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		for (String businessid : ids) {
			baseMongo.deleteById(businessid, DocumentName);
		}
		return "";
	}
	public void deleteExperience(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String arrid=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		String[] array={};
		array=arrid.split(",");
		for (String id : array) {
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName3).find(queryAndWhere).first();
		//DeleteResult res = DbUtil.getColl(DocumentName).deleteOne((Bson) doc);
		DbUtil.getColl(DocumentName3).deleteOne((Bson) doc);
		//在报告中添加字段pfrExperience 为null 表示删除
		BasicDBObject where=new BasicDBObject();
		where.put("_id",new ObjectId(doc.get("reportId").toString()));
		Document docFormal =DbUtil.getColl(DocumentNameForReport).find(where).first();
		docFormal.put("_id",new ObjectId(docFormal.get("_id").toString()));
		docFormal.put("pfrExperience", null);
		BasicDBObject updateSetValue=new BasicDBObject("$set",docFormal);
		DbUtil.getColl(DocumentNameForReport).updateOne(where,updateSetValue);
		//return res.toString();
		}
	}
	//更新信息
	public String updateFeedback(String json){
		Document bson=Document.parse(json);
		
		//修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		BasicDBObject where=new BasicDBObject();
		where.put("_id",new ObjectId(bson.get("_id").toString()));
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		bson.put("_id",new ObjectId(bson.get("_id").toString()));
		BasicDBObject updateSetValue=new BasicDBObject("$set",bson);
		
		UpdateResult doc = DbUtil.getColl(DocumentName).updateOne(where,updateSetValue);
		return doc.toString();
	}
	public String updateExperience(String json){
			Document bson=Document.parse(json);
			
			//修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
			BasicDBObject where=new BasicDBObject();
			where.put("_id",new ObjectId(bson.get("_id").toString()));
			
			//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
			bson.put("_id",new ObjectId(bson.get("_id").toString()));
			BasicDBObject updateSetValue=new BasicDBObject("$set",bson);
			
			UpdateResult doc = DbUtil.getColl(DocumentName3).updateOne(where,updateSetValue);
			return doc.toString();
	}
	
	
	/**
	 * 预评审
	 * 导出决策委员会材料列表
	 * @throws IOException 
	 * */
	
	public Map<String,String> feedbackReport() throws IOException{
		MongoCursor<Document> limit = DbUtil.getColl(DocumentName).find().iterator();
        List<Document> docList = CrudUtil.convertCursorToList(limit);
		List<Object> list=new ArrayList<Object>();
		for (Document document : docList) {
			Map<String,String> map=new HashMap<String,String>();
			map.put("projectName",  document.get("projectName").toString());
			map.put("tenderResults",document.get("tenderResults").toString());
			map.put("projectOpentime", document.get("projectOpentime").toString());
			map.put("create_name", document.get("create_name")==null?null:document.get("create_name").toString());
			list.add(map);
		}

		String[] arr = new String[] {"项目名称","投标结果","开标时间","创建者"};
		String[] arrColumn = new String[] {"projectName","tenderResults","projectOpentime","create_name"};
		return ExportExcel.exportExcel("投标结果反馈列表","feedBackReport", arr,arrColumn,list);
		
	}
	/**
	 * 正式申请
	 * 导出决策委员会列表
	 * @throws IOException 
	 * */
	public Map<String,String> feekbackListReport(String json) throws IOException{
		
		BasicDBObject queryAndWhere =new BasicDBObject();
		MongoCursor<Document> limit = null,limitcount=null;
		BasicDBObject sortVal =new BasicDBObject();
		String projectName=null,createName=null,createId=null,create_date=null;
		
		Document doc = Document.parse(json);
        sortVal.put("create_date",(Integer) doc.get("ORDERVAL"));
		
		if(!"".equals(doc.get("projectName")) && null!=doc.get("projectName")){
        	projectName = doc.get("projectName").toString();
        }
        if(!"".equals(doc.get("create_date")) && null!=doc.get("create_date")){
        	create_date = doc.get("create_date").toString();
        }
       
        if(null!=projectName){
    		Pattern pattern = Pattern.compile("^.*"+projectName+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("projectName", pattern);
    	}
		if(!"".equals(doc.get("create_date")) && null!=doc.get("create_date")){
        	create_date = doc.get("create_date").toString();
        }
       
        if(null!=projectName){
    		Pattern pattern = Pattern.compile("^.*"+projectName+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("projectName", pattern);
    	}
    	
    	if(null!=create_date){
    		Pattern pattern = Pattern.compile("^.*"+create_date+".*$", Pattern.CASE_INSENSITIVE);
    		queryAndWhere.put("create_date", pattern);
    	}
    	queryAndWhere.put("state", "2");
		MongoCollection<Document> userCollection = DbUtil.getColl(DocumentName3);
		limit = userCollection.find(queryAndWhere).sort(sortVal).iterator();
    	List<Document>  docList = CrudUtil.convertCursorToList(limit);
    	
		List<Object> list=new ArrayList<Object>();
		for (Document document : docList) {
			Map<String,String> map=new HashMap<String,String>();
			map.put("projectName", document.get("projectName").toString());
			map.put("create_Name",document.get("create_Name").toString());
			map.put("create_date",(String) document.get("create_date"));
			map.put("projectAdvice",(String) document.get("projectAdvice"));
			map.put("projectImprove",(String) document.get("projectImprove"));
			map.put("projectExperience",(String) document.get("projectExperience"));
			map.put("projectMeasure",(String) document.get("projectMeasure"));
			map.put("projectQuality",(String) document.get("projectQuality"));
			list.add(map);
		}
		String[] arr = new String[] {"项目名称","创建人","创建日期","项目评审遇到问题及解决建议","项目评审不足及改进措施","项目评审经验分享","测算能力","协议质量"};
		String[] arrColumn = new String[] {"projectName","create_Name","create_date","projectAdvice","projectImprove","projectExperience","projectMeasure","projectQuality"};
		return ExportExcel.exportExcel("项目经验总结列表","案例库", arr,arrColumn,list);
	}
}
