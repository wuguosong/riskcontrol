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

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;

import common.BaseService;
import common.BusinessException;
import common.Constants;
import common.ExportExcel;
import common.PageAssistant;
import rcm.ProjectInfo;
import rcm.ProjectRelation;
import report.FormalApplication;
import report.FormalAssessmentReportUtil;
import util.CrudUtil;
import util.DbUtil;
import util.FileUtil;
import util.PageMongoDBUtil;
import util.Util;

public class ProjectFormalReview extends BaseService{
	/**
	 * 正式评审管理
	 */
	private static final long serialVersionUID = 1L;
	private final String DocumentName=Constants.RCM_PROJECTFORMALREVIEW_INFO;
	private final String DocumentMeetingName=Constants.FORMAL_MEETING_INFO;
	
	public PageAssistant getAll(String json) {
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
		this.selectByPage(assistant, "projectInfo.selectProjectReview");
		

		List<Map<String, Object>> list = (List<Map<String, Object>>) assistant.getList();
		
		MongoCollection<Document> userCollection = DbUtil.getColl(DocumentMeetingName);
		
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
	public PageAssistant getProjectFormalReviewReadOnlyAll(String json) {
		PageAssistant assistant = new PageAssistant(json);
		Map<String, Object> map=assistant.getParamMap();
		map.put("user_id", null);
		assistant.setParamMap(map);
		assistant.getParamMap().put("type", Constants.FORMAL_ASSESSMENT);
		this.selectByPage(assistant, "projectInfo.selectProjectReview");
		return assistant;
	}
	
	//添加信息
	public String createProjectFormalReview(String json){
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
		ObjectId id= (ObjectId)doc.get("_id");
		String id_ = id.toHexString();
		//插入评审基本信息到项目表
		ProjectInfo prj = new ProjectInfo();
		prj.saveReviewBaseInfo2Oracle(id_,Constants.FORMAL_ASSESSMENT);
		//插入人员项目关系表
		ProjectRelation pr = new ProjectRelation();
		pr.insertWhenStart(id_, Constants.FORMAL_ASSESSMENT);
		return id_;
	}
	
	//更新信息
	public String update(String json){
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
		//更新评审基本信息到项目表
		ProjectInfo prj = new ProjectInfo();
		prj.saveReviewBaseInfo2Oracle(id,Constants.FORMAL_ASSESSMENT);
		//插入人员项目关系表
		ProjectRelation pr = new ProjectRelation();
		pr.insertWhenStart(id, Constants.FORMAL_ASSESSMENT);
		return id;
	}

	//删除
	public void delete(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String arrid=bson.get("_id").toString();
		String[] array={};
		array=arrid.split(",");
		for (String string : array) {
			queryAndWhere.put("_id", new ObjectId(string));
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("business_id", string);
			paramMap.put("type", Constants.FORMAL_ASSESSMENT);
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
		    };
			
		}
	}
	//根据条件查询单个信息
	public Document getProjectFormalReviewByObject(String json){
		Bson bson = BsonDocument.parse(json);
		Document doc = DbUtil.getColl(DocumentName).find(bson).first();
		return doc;
	}
	
	//根据ID查询
	public Document getProjectFormalReviewByID(String id){
		BasicDBObject query =new BasicDBObject();
		query.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(query).first();
		List<Document> attachmentList = (List<Document>) doc.get("attachment");
		
		List<Document> attach = new ArrayList<Document>();
		if(Util.isNotEmpty(attachmentList)){
			for (Document attachment : attachmentList) {
				if(Util.isNotEmpty(attachment)){
					Document document = new Document();
					document.put("ITEM_NAME", attachment.get("ITEM_NAME").toString());
					document.put("UUID", attachment.get("UUID").toString());
					attach.add(document);
				}
			}
		}
		doc.put("attach", attach);
		doc.put("_id", doc.get("_id").toString());
		return doc;
	}
	//判断是否已经存在
	public Document ExistCode(String json){
		Bson bson = BsonDocument.parse(json);
		Document doc =DbUtil.getColl(DocumentName).find(bson).first();
		return doc;
	}
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
		params.put("fuctionType", "正式评审");
		List<Map> list = DbUtil.openSession().selectList("attachment.findAttachmentList", params);
		return list;
	}
	//根据ID查询更新评审负责人
	public String saveProjectFormalReviewByID(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String id=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		Document taskAllocation = (Document) bson.get("taskallocation");
		taskAllocation.put("dataCompleteTime", Util.format(Util.now(), "yyyy-MM-dd"));
		BasicDBObject updateInfo = new BasicDBObject("taskallocation", taskAllocation);
		BasicDBObject updateSetValue=new BasicDBObject("$set", updateInfo);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		return result.toString();
	}
	//根据ID查询 更新初步评审
	public String saveProjectFormalComentsByID(String json){
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
					if(null!=oldFile &&  !oldFile.isEmpty() && !"".equals(oldFile) && null!=val && !"".equals(val) && val.equals(doo.get("ITEM_NAME"))){
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
										/*if(null!=document.get("programmed")){
											OldDocum.append("programmed", document.get("programmed"));
										}
										if(null!=document.get("approved")){
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
								/*if(null!=document.get("programmed")){
									OldDocum.append("programmed", document.get("programmed"));
								}
								if(null!=document.get("approved")){
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
							/*if(null!=document.get("programmed")){
								OldDocum.append("programmed", document.get("programmed"));
							}
							if(null!=document.get("approved")){
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
	}
	
	//根据ID查询 更新初步评审
	public String saveProjectFormalLegarComentsByID(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String id=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		doc.put("approveLegalAttachment", bson.get("approveLegalAttachment"));
		
		//获取最开始版本中所有文件信息
		List<Document> attachment =(List<Document>) doc.get("attachment");
		//获取初步评审意见上传的文件
		Document map=(Document)bson.get("approveLegalAttachment");
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
					if(null!=oldFile &&  !oldFile.isEmpty() && !"".equals(oldFile) &&  null!=val && !"".equals(val) && val.equals(doo.get("ITEM_NAME"))){
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
										/*if(null!=document.get("programmed")){
											OldDocum.append("programmed", document.get("programmed"));
										}
										if(null!=document.get("approved")){
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
								/*if(null!=document.get("programmed")){
									OldDocum.append("programmed", document.get("programmed"));
								}
								if(null!=document.get("approved")){
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
							/*if(null!=document.get("programmed")){
								OldDocum.append("programmed", document.get("programmed"));
							}
							if(null!=document.get("approved")){
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
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		return result.toString();
	}
	
	//根据ID查询更新评审小组专业意见
	public String saveProjectFormalByID(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String id=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		doc.put("pfrReviewComments", bson.get("pfrReviewComments"));
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		return result.toString();
	}
	
	//业务单位承诺列表
	public String saveBusinessUnitCommitList(String json){
		BasicDBObject queryAndWhere =new BasicDBObject();
		Document bson = Document.parse(json);
		String id=bson.get("_id").toString();
		//查询或者修改数据前先将where条件的id转为 ObjecId类型  {"_id",ObjectId("adfa-sf51-5181-811s-85ad")}
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc =DbUtil.getColl(DocumentName).find(queryAndWhere).first();
		
		//修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id",new ObjectId(id));
		doc.put("pfrBusinessUnitCommit", bson.get("pfrBusinessUnitCommit"));
		doc.put("pfrBusinessUnitCommitState", bson.get("pfrBusinessUnitCommitState"));
		BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
		UpdateResult result = DbUtil.getColl(DocumentName).updateOne(queryAndWhere,updateSetValue);
		return result.toString();
	}
	
	
	//导出正式申请列表
	public Map<String,String> importFormalAssessmentReport(String uuid) throws IOException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_id", uuid);
		paramMap.put("code", Constants.ROLE_SYSTEM_ADMIN);
		String o=DbUtil.openSession().selectOne("role.hasRole",paramMap);
		if("1".equals(o)){
			paramMap.put("user_id", null);
		}
		paramMap.put("type", Constants.FORMAL_ASSESSMENT);
		List<Object> list=DbUtil.openSession().selectList("projectInfo.selectProjectReview",paramMap);
		
		String[] arr = new String[] {"申报单位","项目名称","项目类型","投资模式","投资经理","评审负责人","法律负责人","申请时间","评审状态"};
		String[] arrColumn = new String[] {"REPORTING_UNIT_NAME","PROJECT_NAME","PROJECT_TYPE_NAMES",
				"PROJECT_MODEL_NAMES","INVESTMENT_MANAGER_NAME","COMPANY_HEADER_NAME","LEGALREVIEWLEADER_NAME","CREATE_TIME","WF_STATE"};
		return ExportExcel.exportExcel("正式评审信息列表","forAssessment", arr,arrColumn,list);
	}
	
	public Map<String,String> getFormalApplication(String id){
		String path=new FormalApplication().generateReport(id);
		
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
