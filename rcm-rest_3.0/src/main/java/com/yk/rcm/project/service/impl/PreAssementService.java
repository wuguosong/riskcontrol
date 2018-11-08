/**
 * 
 */
package com.yk.rcm.project.service.impl;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rcm.ProjectRelation;
import report.Path;
import util.DbUtil;
import util.PropertiesUtil;
import util.Util;
import ws.client.TzClient;
import ws.service.DownloadFileForTZ;

import com.mongodb.BasicDBObject;
import com.yk.bpmn.service.IBpmnService;
import com.yk.common.IBaseMongo;
import com.yk.flow.util.JsonUtil;
import com.yk.power.service.IOrgService;
import com.yk.rcm.project.dao.IProjectMapper;
import com.yk.rcm.project.service.IPreAssementService;
import com.yk.rcm.project.service.IProjectRelationService;

import common.Constants;
import common.Result;

/**
 * @author 80845530
 *
 */
@Service
@Transactional
public class PreAssementService implements IPreAssementService {
	
	private Logger log = LoggerFactory.getLogger(PreAssementService.class);
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IProjectMapper projectMapper;
	@Resource
	private IProjectRelationService projectRelationService;
	@Resource
	private IBpmnService bpmnService;
	@Resource
	private IOrgService orgService;
	@Resource
	private TzClient tzClient;
	
	DownloadFileForTZ downloadFileForTZ=new DownloadFileForTZ();
	
	@Override
	public void deleteById(String businessId) {
		//删除mongo数据
		this.baseMongo.deleteById(businessId, Constants.PREASSESSMENT);
		//删除oracle数据
		this.projectMapper.deleteByBusinessId(businessId);
		//删除人员关系
		this.projectRelationService.deleteByBusinessId(businessId);
		//删除流程数据
		this.bpmnService.stopProcess(Constants.PRE_ASSESSMENT, businessId);
	}
	
	@Override
	public void deleteByIdSyncTz(String businessId) {
		//删除系统数据
		this.deleteById(businessId);
		//调用投资接口
		tzClient.setBusinessId(businessId);
		tzClient.setStatus("5");
		tzClient.setLocation("");
		Thread t = new Thread(tzClient);
		t.start();
	}
	@Override
	public Map<String, Object> queryMongoById(String businessId) {
		return this.baseMongo.queryById(businessId, Constants.PREASSESSMENT);
	}

	@Override
	public Map<String, Object> queryOracleById(String businessId) {
		return this.projectMapper.queryById(businessId);
	}
	@Override
	public Result save(Document data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result update(Document data) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Result saveOrUpdate(Document oldDoc) {
		boolean isSuccess = true;
		String  date= Util.getTime();
		String id=null;
		Map<String, Object> map = new HashMap<String, Object>();
		Document apply=(Document) oldDoc.get("apply");
		String businessId = oldDoc.getString("businessid");
		
		//处理serviceType
		Document doc = this.updateServiceType(oldDoc);
		//修改附件路径\\为/
		//无论新增与修执行此方法
		this.updateAttachmentPath(doc);
		//存储投资系统的附件
		doc.put("oldAttachment", oldDoc.get("attachment"));
		
		Document newApply=(Document) doc.get("apply");
		Document reportingUnit = (Document) newApply.get("reportingUnit");
		//处理大区pertainArea
		String orgpkvalue = reportingUnit.getString("value");
		Document pertainArea =  this.getPertainArea(orgpkvalue);
		newApply.put("pertainArea", pertainArea);
		String filepan= Path.preAttachmentPath()+apply.get("projectNo")+"/";
		//判断新增活修改
		if("".equals(businessId) || null == businessId){
			//数据不存在，可以新建数据
			doc.append("create_date", date);
			Format format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			doc.append("currentTimeStamp", format.format(new Date()));
			doc.append("state", "1");
			//为附件生成UUID
			generateAttachmentUUID(doc);
			//新增业务没有businessid
			String oid=  new ObjectId().toString();
			id = oid;
			//oracle新增
			this.saveReviewBaseInfo2Oracle(oid, Constants.PRE_ASSESSMENT, doc);
			//mongo新增
			doc.put("_id", new ObjectId(oid));
			DbUtil.getColl(Constants.PREASSESSMENT).insertOne(doc);
			
			map.put("result_wf_state", "0");
			//文件处理
			try {
				downloadFileForTZ.downloadFile("pre", oid, filepan);
			} catch (Exception e) {
				StringBuffer sb = new StringBuffer();
				log.error(sb.append("***********************[")
					.append(Util.getTime()).append("]:").append("touzi system call out interface createPre, create pre")
					.append(System.getProperty("line.separator")).toString());
				String error = Util.parseException(e);
				log.error(error);
			}
		}else{
			//修改	判断流程状态	根据wf_state
			Map<String, Object> proInfo = projectMapper.queryById(businessId);
			String wf_state = proInfo.get("WF_STATE").toString();
			id = businessId;
			if("0".equals(wf_state) || "3".equals(wf_state)){
				//可以修改
				BasicDBObject queryAndWhere=new BasicDBObject();
				queryAndWhere.put("_id", new ObjectId(businessId));
				Document docOld =DbUtil.getColl(Constants.PREASSESSMENT).find(queryAndWhere).first();
				docOld.put("_id",new ObjectId(docOld.get("_id").toString()));
				docOld.put("apply", doc.get("apply"));
				//为附件生成UUID
				generateAttachmentUUID(doc);
				docOld.put("attachment", doc.get("attachment"));
				doc.append("update_date", date);
				BasicDBObject updateSetValue=new BasicDBObject("$set",doc);
				//修改oracle
				this.saveReviewBaseInfo2Oracle(businessId, Constants.PRE_ASSESSMENT, docOld);
				//修改mongo
//				DbUtil.getColl(Constants.PREASSESSMENT).deleteOne(queryAndWhere);
//				docOld.put("_id", new ObjectId(businessId));
//				DbUtil.getColl(Constants.PREASSESSMENT).insertOne(docOld);
				DbUtil.getColl(Constants.PREASSESSMENT).updateOne(queryAndWhere,updateSetValue);
				
				map.put("result_wf_state", "0");
				try {
					downloadFileForTZ.downloadFile("pre", id, filepan);
				} catch (Exception e) {
					StringBuffer sb = new StringBuffer();
					log.error(sb.append("***********************[")
						.append(Util.getTime()).append("]:").append("touzi system call out interface createPre, create pre")
						.append(System.getProperty("line.separator")).toString());
					String error = Util.parseException(e);
					log.error(error);
				}
			}else{
				//流程已提交不可修改
				//数据已存在，并且流程已开始审批，不可以修改数据
				Map<String, Object> docs = this.projectMapper.queryById(businessId);
				String WF_STATE=docs.get("WF_STATE").toString();
				
				BasicDBObject queryAndWhere=new BasicDBObject();
				queryAndWhere.put("apply.projectNo", apply.get("projectNo").toString());
				Document docOld =DbUtil.getColl(Constants.PREASSESSMENT).find(queryAndWhere).first();
				map.put("projectReportURL", docOld.get("filePath"));
				map.put("result_wf_state", WF_STATE);
				isSuccess = false;
			}
		}
		
		//插入人员项目关系表
		ProjectRelation pr = new ProjectRelation();
		pr.insertWhenStart(id, Constants.PRE_ASSESSMENT);
		map.put("result_status", isSuccess);
		map.put("id", id);
		String prefix = PropertiesUtil.getProperty("domain.allow");
		map.put("URL", prefix+"/html/index.html#/ProjectPreReviewView/"+id);
		Result result = new Result();
		result.setResult_data(map);
		return result;
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
 
	@SuppressWarnings("unchecked")
	private Document updateServiceType(Document doc) {
		
		
		String docJson = JsonUtil.toJson(doc);
		Document copyDoc = Document.parse(docJson);
		
		Document apply=(Document) copyDoc.get("apply");
		
		//存旧的类型到mongo
		apply.put("oldServiceType", apply.get("serviceType"));
		List<Document> serviceTypeList = (List<Document>) apply.get("serviceType");
		List<Document> NewServiceTypeList = new ArrayList<Document>();
		/**
		 * 1--1401
		 * 2--1402
		 * 4--1401
		 * 5--1404
		 * 6--1403
		 * 7--1405
		 */
		HashSet<String> serviceTypeIdSet = new HashSet<String>();
		for(int i = 0; i < serviceTypeList.size(); i++){
			Document serviceType = serviceTypeList.get(i);
			if(!Util.isEmpty(serviceType)){
				String serviceTypeKey = serviceType.getString("KEY");
				if(!Util.isEmpty(serviceTypeKey)){
					if(serviceTypeKey.equals("1") || serviceTypeKey.equals("4")){
						serviceTypeKey = "1401";
					}
					if(serviceTypeKey.equals("2")){
						serviceTypeKey = "1402";
					}
					if(serviceTypeKey.equals("5")){
						serviceTypeKey = "1404";
					}
					if(serviceTypeKey.equals("6")){
						serviceTypeKey = "1403";
					}
					if(serviceTypeKey.equals("7")){
						serviceTypeKey = "1405";
					}
					serviceTypeIdSet.add(serviceTypeKey);//利用set去重
				}
			}
		}
		for (String key : serviceTypeIdSet) {
			String value = "";
			if(key.equals("1401")){
				value  = "传统水务";
			}
			if(key.equals("1402")){
				value  = "水环境";
			}
			if(key.equals("1403")){
				value  = "固废";
			}
			if(key.equals("1404")){
				value  = "环卫";
			}
			if(key.equals("1405")){
				value  = "其他";
			}
			Document serviceType = new Document();
			serviceType.put("KEY", key);
			serviceType.put("VALUE", value);
			NewServiceTypeList.add(serviceType);
		}
		apply.put("serviceType",NewServiceTypeList);
		return copyDoc;
	}

	@SuppressWarnings("unchecked")
	private void updateAttachmentPath(Document doc){
		if(!doc.containsKey("attachment")){
			return;
		}
		List<Document> attachmentList =(List<Document>) doc.get("attachment");
		if(attachmentList == null || attachmentList.isEmpty()){
			return;
		}
		for(int i = 0; i < attachmentList.size(); i++){
			Document attachment = attachmentList.get(i);
			if(attachment != null && attachment.containsKey("files") && attachment.get("files")!=null){
				List<Document> files = (List<Document>) attachment.get("files");
				if(files.isEmpty()){
					continue;
				}
				for(int m = 0; m < files.size(); m++){
					Document file = files.get(m);
					if(file==null || !file.containsKey("filePath") || file.getString("filePath")==null){
						continue;
					}
					String filePath = file.getString("filePath");
					file.put("filePath", filePath.replaceAll("\\\\", "/"));
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void generateAttachmentUUID(Document doc){
		if(!doc.containsKey("attachment")){
			return;
		}
		List<Document> attachmentList =(List<Document>) doc.get("attachment");
		if(attachmentList == null || attachmentList.isEmpty()){
			return;
		}
		for(int i = 0; i < attachmentList.size(); i++){
			Document attachment = attachmentList.get(i);
			if(attachment != null && attachment.containsKey("UUID")){
				attachment.put("UUID", Util.getUUID());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void saveReviewBaseInfo2Oracle(String objectId, String processType,Document doc){
		Document apply = doc.get("apply", Document.class);
		//业务ID
//		String idOfDoc = doc.get("_id").toString();
		String businessId = objectId;
//		if("".equals(idOfDoc) || null == idOfDoc){
//			businessId = objectId;
//		}else{
//			businessId = idOfDoc;
//		}
		
		//申报单位
		Document reportingUnit = apply.get("reportingUnit", Document.class);
		String reportingUnitName = reportingUnit.getString("name");
		String reportingUnitId = reportingUnit.getString("value");
		//项目名称
		String projectName = apply.getString("projectName");
		//项目编码
		String projectNo = apply.getString("projectNo");
		//业务类型
		List<Document> serviceType = apply.get("serviceType", ArrayList.class);
		String serviceTypeId = getStringFromListDoc(serviceType, "KEY");
		String serviceTypeName = getStringFromListDoc(serviceType, "VALUE");
		//二级业务类型
		List<Document> projectTypeList = apply.get("projectType", ArrayList.class);
		String projectTypeIds = getStringFromListDoc(projectTypeList, "KEY");
		String projectTypNames = getStringFromListDoc(projectTypeList, "VALUE");
		//投资模式
		Boolean investmentModel = apply.getBoolean("investmentModel");//PPP和非PPP
		List<Document> projectModelList = apply.get("projectModel", ArrayList.class);
		String projectModelIds = getStringFromListDoc(projectModelList, "KEY");
		String projectModelNames = getStringFromListDoc(projectModelList, "VALUE");
		String is_supplement_review ="0";
		if(null!=doc.get("is_supplement_review") && !"".equals(doc.get("is_supplement_review"))){
			is_supplement_review=doc.get("is_supplement_review").toString();
		}
		String istz ="1";
		if(null!=doc.get("istz") && !"".equals(doc.get("istz"))){
			istz=doc.get("istz").toString();
		}
		//执行保存操作
		Map<String, Object> insertMap = new HashMap<String, Object>();
		insertMap.put("id", Util.getUUID());
		insertMap.put("businessId", businessId);
		insertMap.put("projectName", projectName);
		insertMap.put("projectNo", projectNo);
		insertMap.put("reportingUnitName", reportingUnitName);
		insertMap.put("reportingUnitId", reportingUnitId);
		insertMap.put("serviceTypeId", serviceTypeId);
		insertMap.put("serviceTypeName", serviceTypeName);
		insertMap.put("projectTypeIds", projectTypeIds);
		insertMap.put("projectTypNames", projectTypNames);
		insertMap.put("investmentModel", investmentModel==null ? Boolean.FALSE:investmentModel);
		insertMap.put("projectModelIds", projectModelIds);
		insertMap.put("projectModelNames", projectModelNames);
		insertMap.put("approveState", "");
		insertMap.put("type", processType);
		insertMap.put("wfState", "0");
		insertMap.put("is_supplement_review", is_supplement_review);
		insertMap.put("istz", istz);
		Document pertainArea = (Document) apply.get("pertainArea");
		if(pertainArea!=null && pertainArea.get("KEY")!=null){
			insertMap.put("pertainareaId", pertainArea.get("KEY"));
		}
		Map<String, Object> oldPrj = this.projectMapper.queryById(businessId);
		if(Util.isNotEmpty(oldPrj)){
			insertMap.put("wfState", oldPrj.get("WF_STATE"));
		}
		this.projectMapper.deleteByBusinessId(businessId);
		this.projectMapper.insert(insertMap);
	}
	
	//获取List<Document>中的
	private String getStringFromListDoc(List<Document> listDoc, String key){
		String str = "";
		if(Util.isNotEmpty(listDoc)){
			for(Document doc : listDoc){
				str += doc.getString(key)+",";
			}
		}
		str = StringUtils.removeEnd(str, ",");
		return str;
	}

}
