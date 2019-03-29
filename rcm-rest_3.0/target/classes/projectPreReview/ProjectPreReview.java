package projectPreReview;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.print.Doc;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.javassist.compiler.SyntaxError;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSession;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.QueryOperators;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;

import common.BaseService;
import common.BusinessException;
import common.PageAssistant;
import oracle.jdbc.Const;
import common.Constants;
import common.ExportExcel;
import rcm.ProjectInfo;
import rcm.ProjectRelation;
import report.FormalApplication;
import report.PreApplication;
import report.PreAssessmentReport;
import util.CrudUtil;
import util.DbUtil;
import util.FileUtil;
import util.PageMongoDBUtil;
import util.Util;

public class ProjectPreReview extends BaseService{
	private static final long serialVersionUID = 1L;
	private final String DocumentName=Constants.PREASSESSMENT;	
	private final String DocumentNameMeeting=Constants.FORMAL_MEETING_INFO;
	/**
	 * 管理员过滤，如果是管理员， 那么把传入用户设置为空
	 * @param assistant
	 */
	private void adminFilter(PageAssistant assistant){
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
	}
	/*
	 * 获取预评审列表
	 * currentTimeStamp代表lastId最后一个值
	 * */
	public PageAssistant getAll(String json) {
		PageAssistant assistant = new PageAssistant(json);
		adminFilter(assistant);
		assistant.getParamMap().put("type", Constants.PRE_ASSESSMENT);
		this.selectByPage(assistant, "projectInfo.selectProjectReview");
		return assistant;
	}
	
	public PageAssistant getProjectReviewReadOnlyAll(String json) {
		PageAssistant assistant = new PageAssistant(json);
		Map<String, Object> map=assistant.getParamMap();
		map.put("user_id", null);
		assistant.setParamMap(map);
		assistant.getParamMap().put("type", Constants.PRE_ASSESSMENT);
		this.selectByPage(assistant, "projectInfo.selectProjectReview");
		return assistant;
	}
	/*
	 * 获取预评审/正式评审总条数
	 * currentTimeStamp代表lastId最后一个值
	 * */
	public Map<String,Object> getProjectReviewCount() {
		Map<String,Object> Countmap=new HashMap<String,Object>();
			//预评审总条数
			PageAssistant assistant = new PageAssistant();
			Map<String, Object> map=assistant.getParamMap();
			map.put("user_id", null);
			assistant.setParamMap(map);
			assistant.getParamMap().put("type", Constants.PRE_ASSESSMENT);
			this.selectByPage(assistant, "projectInfo.selectProjectReview");
			Integer ypsCount= assistant.getTotalItems();
			//正式评审总条数
			PageAssistant assistant2 = new PageAssistant();
			assistant2.setParamMap(map);
			assistant2.getParamMap().put("type", Constants.FORMAL_ASSESSMENT);
			this.selectByPage(assistant2, "projectInfo.selectProjectReview");
			Integer zspsCount= assistant2.getTotalItems();
		
			//获取今日上会条数
			BasicDBObject queryAndWhere =new BasicDBObject();
			BasicDBObject queryVal =new BasicDBObject();
			Date now=new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
			String  createDate= dateFormat.format(now);
			 queryVal.append("$eq", createDate);
		     queryAndWhere.put("meetingTime",queryVal);
			
			MongoCursor<Document> cursor= DbUtil.getColl(DocumentNameMeeting).find(queryAndWhere).iterator();
			List<Document> docList = CrudUtil.convertCursorToList(cursor);
			int shCount=docList.size();
			
			Countmap.put("ypsCount", ypsCount);	
			Countmap.put("zspsCount", zspsCount);
			Countmap.put("shCount", shCount);
			return Countmap;
	}
	/*
	 * 获取预评审/正式评审总条数
	 * currentTimeStamp代表lastId最后一个值
	 * */
	public Map<String,Object> getUserCount(String uid) {
		Map<String,Object> Countmap=new HashMap<String,Object>();
			//预评审总条数
			PageAssistant assistant = new PageAssistant();
			Map<String, Object> map=assistant.getParamMap();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("user_id", uid);
			paramMap.put("code", Constants.ROLE_SYSTEM_ADMIN);
			String o=DbUtil.openSession().selectOne("role.hasRole",paramMap);
			if("1".equals(o)){
				map.put("user_id", null);
				assistant.setParamMap(map);
			}else{
				map.put("user_id", uid);
				assistant.setParamMap(map);
			}
			assistant.setParamMap(map);
			assistant.getParamMap().put("type", Constants.PRE_ASSESSMENT);
			this.selectByPage(assistant, "projectInfo.selectProjectReview");
			Integer ypsCount= assistant.getTotalItems();
			//正式评审总条数
			PageAssistant assistant2 = new PageAssistant();
			assistant2.setParamMap(map);
			assistant2.getParamMap().put("type", Constants.FORMAL_ASSESSMENT);
			this.selectByPage(assistant2, "projectInfo.selectProjectReview");
			Integer zspsCount= assistant2.getTotalItems();
			
			Countmap.put("ypsCount", ypsCount);	
			Countmap.put("zspsCount", zspsCount);
			return Countmap;
	}
	public PageAssistant getAllReport(String json) {
		PageAssistant assistant = new PageAssistant(json);
		adminFilter(assistant);
		assistant.getParamMap().put("type", Constants.PRE_ASSESSMENT);
		this.selectByPage(assistant, "projectInfo.selectProjectReviewReport");
		return assistant;
	}
	//导出预评审申请列表
	public Map<String,String> importPreAll(String uuid) throws IOException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_id", uuid);
		paramMap.put("code", Constants.ROLE_SYSTEM_ADMIN);
		String o=DbUtil.openSession().selectOne("role.hasRole",paramMap);
		if("1".equals(o)){
			paramMap.put("user_id", null);
		}
		paramMap.put("type", Constants.PRE_ASSESSMENT);
		List<Object> list=DbUtil.openSession().selectList("projectInfo.selectProjectReview",paramMap);
		
		String[] arr = new String[] {"申报单位","项目名称","项目编号","项目类型","投资模式","投资经理","评审负责人","申请时间","评审状态"};
		String[] arrColumn = new String[] {"REPORTING_UNIT_NAME","PROJECT_NAME","PROJECT_NO","PROJECT_TYPE_NAMES",
				"PROJECT_MODEL_NAMES","INVESTMENT_MANAGER_NAME","COMPANY_HEADER_NAME","CREATE_TIME","WF_STATE"};
		return ExportExcel.exportExcel("预评审信息列表","preAssessment", arr,arrColumn,list);
	}
	//导出预评审报告列表
	public Map<String,String> importPreReportAll(String uuid) throws IOException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_id", uuid);
		paramMap.put("code", Constants.ROLE_SYSTEM_ADMIN);
		String o=DbUtil.openSession().selectOne("role.hasRole",paramMap);
		if("1".equals(o)){
			paramMap.put("user_id", null);
		}
		paramMap.put("type", Constants.PRE_ASSESSMENT);
		List<Object> list=DbUtil.openSession().selectList("projectInfo.selectProjectReviewReport",paramMap);
		
		String[] arr = new String[] {"项目名称","投资经理","评审负责人","申报单位","提交时间","预评审报告状态"};
		String[] arrColumn = new String[] {"PROJECT_NAME","INVESTMENT_MANAGER_NAME","REVIEW_LEADER_NAME",
				"REPORTING_UNIT_NAME","APPLY_TIME","WF_STATE"};
		
		return ExportExcel.exportExcel("预评审报告列表","preAssessmentReport", arr,arrColumn,list);
	}
	//导出预评审申请列表
	public Map<String,String> importReviewListAll(String uuid) throws IOException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_id", uuid);
		paramMap.put("code", Constants.ROLE_SYSTEM_ADMIN);
		String o=DbUtil.openSession().selectOne("role.hasRole",paramMap);
		if("1".equals(o)){
			paramMap.put("user_id", null);
		}
		paramMap.put("type", Constants.PRE_ASSESSMENT);
		List<Object> list=DbUtil.openSession().selectList("projectInfo.selectProjectReview",paramMap);
		
		String[] arr = new String[] {"项目名称","投资经理","申报单位","材料提交日期"};
		String[] arrColumn = new String[] {"PROJECT_NAME","INVESTMENT_MANAGER_NAME","REPORTING_UNIT_NAME","APPLY_TIME"};
		return ExportExcel.exportExcel("决策委员会审阅列表","preReview", arr,arrColumn,list);
	}
	
	/*
	 * 获取预评审列表
	 * currentTimeStamp代表lastId最后一个值
	 * */
	public PageMongoDBUtil getReportList(String json){
		Document doc = Document.parse(json);
		//当前页默认0
		Integer page=1,pageSize=10,lastId=0;
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
        sortVal.put("currentTimeStamp", 1);
        BasicDBObject queryAndWhere =new BasicDBObject();
        BasicDBObject queryReport =new BasicDBObject();
        queryAndWhere.put("state",1);
        queryReport.append("$ne", null);
        queryAndWhere.put("reviewReport",queryReport);
        limit = userCollection.find(queryAndWhere).sort(sortVal).limit(pageSize).iterator();
		List<Document> docList = CrudUtil.convertCursorToList(limit);
		/*获取总条数*/
		limitcount = userCollection.find(queryAndWhere).sort(sortVal).iterator();
		List<Document> docListcount = CrudUtil.convertCursorToList(limitcount);
		long total = docListcount.size();
		PageMongoDBUtil pageUtil=new  PageMongoDBUtil(Long.toString(total),pageSize.toString(),page.toString(),null,docList);
		return pageUtil;
	}
	
	//添加预评审，加载字典表
	public Map<String, List<Map>> SelectType(String json) {
		Map<String, List<Map>> param = new HashMap<String, List<Map>>();
		String[] array={};
		array=json.split(",");
		for(String code :array){
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("code", code);
			List<Map> obj=DbUtil.openSession().selectList("common.selecFunctionType", paramMap);
			DbUtil.close();
			/*
			 * 单位类别：05，项目类型 01，项目模式,02
			 * */
			if(code.equals("01")){
				param.put("projectType", obj);
			}else if(code.equals("05")){
				param.put("companyCategorys", obj);
			}else if(code.equals("02")){
				param.put("projectModel", obj);
			}
		}
		return param;
	}
	
	/**
	 * 根据项目模式查询预评审列表
	 * @param json
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> findAttachmentList(String json){
		Map params = Util.parseJson2Map(json);
		params.put("fuctionType", "预评审");
		List<Map> list = DbUtil.openSession().selectList("attachment.findAttachmentList", params);
		return list;
	}
	
	//保存预评审表单
	public String create(String json) throws Exception{
		Document doc = Document.parse(json);
		if(null!=doc.get("_id") && !"0".equals(doc.get("_id"))){
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
			String  updateDate= dateFormat.format(now);
			
			String id=doc.get("_id").toString();
			BasicDBObject queryAndWhere=new BasicDBObject();
			queryAndWhere.put("_id", new ObjectId(id));
			Document updateDoc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
			
			//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
			updateDoc.put("_id",new ObjectId(id));
			updateDoc.put("apply", doc.get("apply"));
			updateDoc.put("attachment", doc.get("attachment"));
			updateDoc.append("update_date", updateDate);
			BasicDBObject updateSetValue=new BasicDBObject("$set",updateDoc);
			DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
//			DbUtil.close();
			//更新预评审基本信息到项目表
			ProjectInfo prj = new ProjectInfo();
			prj.saveReviewBaseInfo2Oracle(id,Constants.PRE_ASSESSMENT);
			//插入人员项目关系表
			ProjectRelation pr = new ProjectRelation();
			pr.insertWhenStart(id, Constants.PRE_ASSESSMENT);
			return id;
		}else{
			//设置添加时间
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
			String  createDate= dateFormat.format(now);
			doc.append("create_date", createDate);
			doc.append("state", 1);
			Format format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			doc.append("currentTimeStamp", format.format(new Date()));
			DbUtil.getColl(DocumentName).insertOne(doc);
//			DbUtil.close();
			ObjectId id= (ObjectId)doc.get("_id");
			String objectId = id.toHexString();
			//插入预评审基本信息到项目表
			ProjectInfo prj = new ProjectInfo();
			prj.saveReviewBaseInfo2Oracle(objectId,Constants.PRE_ASSESSMENT);
			//插入人员项目关系表
			ProjectRelation pr = new ProjectRelation();
			pr.insertWhenStart(objectId, Constants.PRE_ASSESSMENT);
			return objectId;
		}
	}
	
	//更新信息
	public String updateProjectPreReview(String json) throws Exception{
		Document bson=Document.parse(json);
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
		String  updateDate= dateFormat.format(now);
		
		String id=bson.get("_id").toString();
		//修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		BasicDBObject queryAndWhere=new BasicDBObject();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		doc.put("apply", bson.get("apply"));
		doc.put("attachment", bson.get("attachment"));
		doc.append("update_date", updateDate);
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		
		UpdateResult res = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		
		//更新预评审基本信息到项目表
		ProjectInfo prj = new ProjectInfo();
		prj.saveReviewBaseInfo2Oracle(id,Constants.PRE_ASSESSMENT);
		//插入人员项目关系表
		ProjectRelation pr = new ProjectRelation();
		pr.insertWhenStart(id, Constants.PRE_ASSESSMENT);
		return id;
	}
	
	//删除
	public void deleteProjectPreReview(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String arrid=bson.get("_id").toString();
		String[] array={};
		array=arrid.split(",");
		for (String string : array) {
			queryAndWhere.put("_id", new ObjectId(string));
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("business_id", string);
			paramMap.put("type", Constants.PRE_ASSESSMENT);
			String reportid = DbUtil.openSession().selectOne("projectInfo.selectReportCountByType", paramMap);
			DbUtil.close();
		    if(null==reportid || "".equals(reportid) || "null".equals(reportid)){
		    	Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
				DbUtil.getColl(DocumentName).deleteOne((Bson) doc);
				
				//删除项目基本信息表中的数据和项目人员关系表中的数据
				ProjectRelation r = new ProjectRelation();
				r.deleteProjectInfoByBusinessId(string);
				ProjectInfo prj = new ProjectInfo();
				prj.deleteProjectInfoByBusinessId(string);
		    }else{
		    	throw new BusinessException("报告未删除！");
		    }
			
		}
	
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		/*queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		DeleteResult res = DbUtil.getColl(DocumentName).deleteOne((Bson) doc);*/
		
	}
	//删除预评审报告
	public void deleteProjectPreReport(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String arrid=bson.get("_id").toString();
		String[] array={};
		array=arrid.split(",");
		Map<String, Object> params=new HashMap<String, Object>();
		for (String string : array) {
			queryAndWhere.put("_id", new ObjectId(string));
			Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
			doc.put("reviewReport", null);
			BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
			DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
			//更新预评审基本信息到项目表
			ProjectInfo prj = new ProjectInfo();
			params.put("reportCreateDate", null);
			prj.updateProjectInfo(string,params);
			
		}
		
	}
	//根据ID查询
	public Document getProjectPreReviewByID(String id){
		BasicDBObject query =new BasicDBObject();
		query.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(Constants.RCM_PRE_INFO).find(query).first();
		if(null!=doc){
			doc.put("_id", doc.get("_id").toString());
		}
		return doc;
	}
	//判断是否已经存在
	public Document ExistCode(String json){
		Bson bson = BsonDocument.parse(json);
		Document doc =DbUtil.getColl(DocumentName).find(bson).first();
		return doc;
	}

	//更新决策委员会材料人员
	public String saveProjectPreReviewperple(String json){
		Document bson=Document.parse(json);
		String id=bson.get("_id").toString();
		//修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		BasicDBObject queryAndWhere=new BasicDBObject();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		doc.put("decisionMakingCommitteeStaff", bson.get("decisionMakingCommitteeStaff"));
		doc.put("decisionMakingCommitteeStaffFiles", bson.get("decisionMakingCommitteeStaffFiles"));
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		UpdateResult res = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		return id;
	}
	//根据ID查询更新评审小组专业意见
	public String saveProjectPreReviewByID(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String id=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		doc.put("proReviewComments", bson.get("proReviewComments"));
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		return result.toString();
	}
	
	
	//根据ID查询更新评审负责人
	public String saveProReviewCommentsByID(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String id=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		doc.put("taskallocation", bson.get("taskallocation"));
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		return result.toString();
	}
	//根据ID查询 更新初步评审
	public String saveProjectPreComentsByID(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String id=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		doc.put("approveAttachment", bson.get("approveAttachment"));
		//获取最开始版本中所有文件信息
		List<Document> attachment =(List<Document>) doc.get("attachment");
		//获取初步评审意见上传的文件
		Document map=(Document)bson.get("approveAttachment");
		List<Document> attachmentNew =(List<Document>) map.get("attachmentNew");
		//遍历最新提交的文件寻找匹配文件名称保存到原始文件files[]中
		for (Document document : attachmentNew) {
			//取出最新上传要对应的文件名称
			Document doo= (Document) document.get("attachmentUList");
			if(null!=doo){
				//遍历原始文件信息
				for (Document OldDocum : attachment) {
					//取出原始文件名称
					String val=(String) OldDocum.get("ITEM_NAME");
					//获取原始所有文件
					List<Object> oldFile=(List<Object>) OldDocum.get("files");
				    //找到匹配的文件名称取出文件添加到原始文件files中
					if(null!=oldFile && !oldFile.isEmpty() && !"".equals(oldFile) && null!=val && !"".equals(val) && val.equals(doo.get("ITEM_NAME"))){
						if(Util.isNotEmpty(document.get("attachment_new"))){
							Document newsFile=(Document) document.get("attachment_new"); //取出需要更新的附件
							String vs=newsFile.get("version").toString();
							Boolean bl=true;
							for(int j=0;j<oldFile.size();j++){
								if(!"".equals(oldFile.get(j))){
									Document sss=(Document) oldFile.get(j);
									if(vs.equals(sss.get("version"))){
										Document d=new Document();
										d.put("fileName", newsFile.get("fileName"));
										d.put("filePath", newsFile.get("filePath"));
										d.put("version", vs);
										d.put("upload_date", newsFile.get("upload_date"));
										d.put("programmed", newsFile.get("programmed"));
										d.put("approved", newsFile.get("approved"));
										oldFile.set(j, d); 
										/*if(null!=document.get("programmed") && null==OldDocum.get("programmed")){
											OldDocum.append("programmed", document.get("programmed"));
										}
										if(null!=document.get("approved") && null==OldDocum.get("approved")){
											OldDocum.append("approved", document.get("approved"));
										}*/
										bl=false;
										break;
									}
								}
							}
							if(bl){
								Document d=new Document();
								d.put("fileName", newsFile.get("fileName"));
								d.put("filePath", newsFile.get("filePath"));
								d.put("version", vs);
								d.put("upload_date", newsFile.get("upload_date"));
								d.put("programmed", newsFile.get("programmed"));
								d.put("approved", newsFile.get("approved"));
								oldFile.add(d);
								/*if(null!=document.get("programmed") && null==OldDocum.get("programmed")){
									OldDocum.append("programmed", document.get("programmed"));
								}
								if(null!=document.get("approved") && null==OldDocum.get("approved")){
									OldDocum.append("approved", document.get("approved"));
								}*/
							}
							break;
						}
					}else if(null!=val && !"".equals(val) && val.equals(doo.get("ITEM_NAME"))){
						if(null!=document.get("attachment_new") && !"".equals(document.get("attachment_new"))){
							List<Object> oos=new ArrayList<Object>();
							oos.add(document.get("attachment_new"));
							oldFile=oos;
							OldDocum.append("files", oldFile);
							/*if(null!=document.get("programmed") && null==OldDocum.get("programmed")){
								OldDocum.append("programmed", document.get("programmed"));
							}
							if(null!=document.get("approved") && null==OldDocum.get("approved")){
								OldDocum.append("approved", document.get("approved"));
							}*/
							break;
						}
					}
				}
			}
		}
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		return result.toString();
		/*//获取最开始版本中所有文件信息
		List<Document> attachment =(List<Document>) doc.get("attachment");
		//获取初步评审意见上传的文件
		Document map=(Document)bson.get("approveAttachment");
		List<Document> attachmentNew =(List<Document>) map.get("attachmentNew");
		//遍历原始文件信息
		for (Document docum : attachment) {
			//取出原始文件名称
			String val=(String) docum.get("ITEM_NAME");
			//获取原始所有文件
			List<Object> oo=(List<Object>) docum.get("files");
			if(oo==null){
				oo=new ArrayList<Object>();
			}
			//移除第一个版本之外的所有文件
			if(null!=oo){
				if(oo.size()>0){
					for(int i=0;i<oo.size();i++)
					{
						if(i!=0){
						 oo.remove(i); 
						 i--;
						}
						
					}
				}
			}
			//遍历最新提交的文件寻找匹配文件名称保存到原始文件files[]中
			for (Document document : attachmentNew) {
				//取出最新上传要对应的文件名称
				Document doo= (Document) document.get("attachmentUList");
				
				//找到匹配的文件名称取出文件添加到原始文件files中
				if(doo!=null){
					if(val.equals(doo.get("ITEM_NAME"))){
						if(Util.isNotEmpty(document.get("attachment_new"))){
							oo.add(document.get("attachment_new"));
						}
					}
				}
			}
			docum.append("files", oo);
		}
		*/
		
	}
	//根据ID查询更新评审报告
	public String saveReviewReportById(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String id=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		doc.put("reviewReport", bson.get("reviewReport"));
		Document map=(Document)bson.get("reviewReport");
		if(null!=map.get("models")){
			String models=(String) map.get("models");
			if(models.equals("other")){
				if(null!=map.get("filePath")){
					String filePath=(String) map.get("filePath");
					doc.append("filePath", filePath);
				}
			}
		}
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		//更新预评审基本信息到项目表
		Map<String, Object> params=new HashMap<String, Object>();
		ProjectInfo prj = new ProjectInfo();
		params.put("reportCreateDate", new Date());
		prj.updateProjectInfo(id,params);
		return result.toString();
	}
	
	public Map<String,String> getPreAssessmentReport(String id){
		PreAssessmentReport test = new PreAssessmentReport();
		String path=test.generateReport(id);
		
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
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		Map<String,String> map =new HashMap<String, String>();
		Document docs=(Document) doc.get("apply");
		map.put("filePath", path);
		map.put("fileName", docs.get("projectName").toString());
		return map;
	}
	
	public Map<String,String> getPreApplication(String id){
		String path=new PreApplication().generateReport(id);
		
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		BasicDBObject queryAndWhere =new BasicDBObject();
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =(Document) DbUtil.getColl(DocumentName).find(queryAndWhere).first().get("apply");
		
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
}
