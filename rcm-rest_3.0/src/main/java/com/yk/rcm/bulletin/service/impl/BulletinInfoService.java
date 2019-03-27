/**
 * 
 */
package com.yk.rcm.bulletin.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daxt.service.IDaxtService;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.flow.util.JsonUtil;
import com.yk.power.dao.IRoleMapper;
import com.yk.power.service.IOrgService;
import com.yk.power.service.IUserService;
import com.yk.rcm.bulletin.dao.IBulletinInfoMapper;
import com.yk.rcm.bulletin.service.IBulletinAuditLogService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;

import common.Constants;
import common.PageAssistant;
import common.Result;
import rcm.DataOption;
import util.FileUtil;
import util.ThreadLocalUtil;
import util.Util;

/**
 * 
 * @author wufucan
 *
 */
@Service
@Transactional
public class BulletinInfoService implements IBulletinInfoService {
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IBulletinInfoMapper bulletinInfoMapper;
	@Resource
	private IUserService userService;
	@Resource
	private IBulletinAuditLogService bulletinAuditLogService;
	@Resource
	private IOrgService orgService;
	@Resource
	private IDaxtService daxtService;
	@Resource
	private IRoleMapper roleMapper;
	
	/* (non-Javadoc)
	 * @see com.yk.bulletin.service.IBulletinInfoService#save(java.lang.String)
	 */
	@Override
	public String save(Map<String, Object> doc) {
		String businessId = Util.getUUID();
		doc.put("_id", businessId);
		this.baseMongo.save((Document)doc, Constants.RCM_BULLETIN_INFO);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("bulletinName", doc.get("bulletinName"));
		Document bulletinType = (Document) doc.get("bulletinType");
		data.put("bulletinTypeId", bulletinType.get("VALUE"));
		data.put("businessId", businessId);
		Document applyUnit = (Document) doc.get("applyUnit");
		data.put("applyUnitId", applyUnit.get("VALUE"));
		//根据申报单位初始化大区ID
		Map<String, Object> pertainAreaDocument = orgService.queryPertainArea(applyUnit.get("VALUE").toString());
		data.put("pertainareaId", pertainAreaDocument.get("ORGPKVALUE"));
		Document unitPerson = (Document) doc.get("unitPerson");
		data.put("unitPersonId", unitPerson.get("VALUE"));
		data.put("createBy", ThreadLocalUtil.getUserId());
		data.put("createDate", doc.get("createTime").toString());
		data.put("auditStatus", "0");
		data.put("status", "0");
		data.put("stage", "1");
		this.bulletinInfoMapper.save(data);
		this.saveDefaultProRole(data);
		System.out.println((String)data.get("businessId"));
		return (String)data.get("businessId");
	}
	
	/**
	 * 新建项目时创建默认项目角色
	 * author Sunny Qi
	 * 2019-03-14
	 * */
	private void saveDefaultProRole(Map<String, Object> doc){
		HashMap<String, Object> params = new HashMap<String,Object>();
		
		HashMap<String, Object> params1 = new HashMap<String,Object>();
		String pertainAreaId = (String)doc.get("pertainareaId");
		params1.put("orgId", pertainAreaId);
		List<Map<String, Object>> roleIds = roleMapper.queryRoleIdByOrgId(params1);
		
		for(int i=0; i < roleIds.size(); i++){
			params.put("id", Util.getUUID());
			params.put("businessId", doc.get("businessId").toString());
			params.put("roleId", roleIds.get(i).get("ROLE_ID"));
			params.put("createBy", doc.get("createBy"));
			params.put("create_date", doc.get("createDate"));
			params.put("projectType", "bulletin");
			
			this.roleMapper.insertProRole(params);
		}
	}

	/* (non-Javadoc)
	 * @see com.yk.bulletin.service.IBulletinInfoService#deleteByBusinessId(java.lang.String)
	 */
	@Override
	public void deleteByBusinessId(String businessId) {
		this.bulletinInfoMapper.deleteByBusinessId(businessId);
		this.baseMongo.deleteById(businessId, Constants.RCM_BULLETIN_INFO);
	}

	/* (non-Javadoc)
	 * @see com.yk.bulletin.service.IBulletinInfoService#updateByBusinessId(java.util.Map)
	 */
	@Override
	public void updateByBusinessId(Map<String, Object> doc) {
		String businessId = (String) doc.get("_id");
		this.baseMongo.updateSetById(businessId, doc, Constants.RCM_BULLETIN_INFO);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("bulletinName", doc.get("bulletinName"));
		Document bulletinType = (Document) doc.get("bulletinType");
		data.put("bulletinTypeId", bulletinType.get("VALUE"));
		data.put("businessId", businessId);
		Document applyUnit = (Document) doc.get("applyUnit");
		data.put("applyUnitId", applyUnit.get("VALUE"));
		//根据申报单位初始化大区ID
		Map<String, Object> pertainAreaDocument = orgService.queryPertainArea(applyUnit.get("VALUE").toString());
		data.put("pertainareaId", pertainAreaDocument.get("ORGPKVALUE"));
		Document unitPerson = (Document) doc.get("unitPerson");
		data.put("unitPersonId", unitPerson.get("VALUE"));
		this.bulletinInfoMapper.updateByBusinessId(data);
	}

	@Override
	public void queryListByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		String needCreateBy = (String) page.getParamMap().get("needCreateBy");
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			if(!"0".equals(needCreateBy)){
				params.put("createBy", ThreadLocalUtil.getUserId());
			}
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " createtime desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.bulletinInfoMapper.queryListByPage(params);
		page.setList(list);
	}
	/* (non-Javadoc)
	 * @see com.yk.bulletin.service.IBulletinInfoService#queryApplyByPage(common.PageAssistant)
	 */
	@Override
	public void queryApplyByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " createtime desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.bulletinInfoMapper.queryApplyByPage(params);
		page.setList(list);
	}


	/* (non-Javadoc)
	 * @see com.yk.bulletin.service.IBulletinInfoService#queryApplyedByPage(common.PageAssistant)
	 */
	@Override
	public void queryApplyedByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " createtime desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.bulletinInfoMapper.queryApplyedByPage(params);
		page.setList(list);
	}
	@Override
	public Result queryCreateDefaultInfo(String userId){
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> user =  this.userService.queryById(userId);
		Map<String, Object> data = this.orgService.queryGroupUserInfo(userId);
		if(data == null){
			return result.setSuccess(false).setResult_name("请完善人员组织信息！");
		}
		map.putAll(data);
		map.put("APPLYUSER_ID", userId);
		map.put("APPLYUSER_NAME", user.get("NAME"));
		
		DataOption dictService = (DataOption) SpringUtil.getBean("rcm.DataOption");
		List<Map<String, Object>> tbsxType = dictService.queryItemsByPcode(Constants.TBSX_TYPE);
		//通报事项类型
		map.put("tbsxType", tbsxType);
		//业务负责人列表
		List<Map<String, Object>> businessUsers = this.bulletinInfoMapper.queryTbsxUserRelations();
		map.put("businessUsers", businessUsers);
		result.setResult_data(map);
		return result;
	}

	@Override
	public Map<String, Object> queryUpdateDefaultInfo(String businessId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> bulletinMongo = this.baseMongo.queryById(businessId, Constants.RCM_BULLETIN_INFO);
		map.put("bulletinMongo", bulletinMongo);
		DataOption dictService = (DataOption) SpringUtil.getBean("rcm.DataOption");
		List<Map<String, Object>> tbsxType = dictService.queryItemsByPcode(Constants.TBSX_TYPE);
		//通报事项类型
		map.put("tbsxType", tbsxType);
		//业务负责人列表
		List<Map<String, Object>> businessUsers = this.bulletinInfoMapper.queryTbsxUserRelations();
		//查出创建人的id，再查出所在的单位，返回给也页面回显
		Document string2 = (Document) bulletinMongo.get("applyUser");
		String userId = (String) string2.get("VALUE");
		Map<String, Object> user =  this.userService.queryById(userId);
		Map<String, Object> data = this.orgService.queryGroupUserInfo(userId);
		map.putAll(data);
		map.put("APPLYUSER_ID", userId);
		map.put("APPLYUSER_NAME", user.get("NAME"));
		map.put("businessUsers", businessUsers);
		return map;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void deleteByIds(String[] businessIds) {
		//1、删除(真实删除)oracle
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", businessIds);
		this.bulletinInfoMapper.deleteByIds(params);
		//2、删除文件系统中的附件
		MongoCollection<Document> collection = this.baseMongo.getCollection(Constants.RCM_BULLETIN_INFO);
		BasicDBList ids = new BasicDBList();
		for(int i = 0; i < businessIds.length; i++){
			ids.add(businessIds[i]);
		}
		BasicDBObject queryFilter = new BasicDBObject();
		queryFilter.put("_id", new BasicDBObject("$in", ids));
		FindIterable<Document> fit = collection.find(queryFilter);
		MongoCursor<Document> cursor = fit.iterator();
		while(cursor.hasNext()){
			Document doc = cursor.next();
			List<Document> fileList = (List<Document>) doc.get("fileList");
			if(fileList == null || fileList.size() == 0){
				continue;
			}
			for(int i = 0; i < fileList.size(); i++){
				Document fileDoc = fileList.get(i);
				if(fileDoc.get("files")!=null && ((Document)fileDoc.get("files")).get("filePath")!=null){
					String filepath = (String) ((Document)fileDoc.get("files")).get("filePath");
					FileUtil.removeFile(filepath);
				}
			}
		}
		//3、删除mongo数据，真实删除
		collection.deleteMany(queryFilter);
	}

	@Override
	public Map<String, Object> queryListDefaultInfo() {
		Map<String, Object> map = new HashMap<String, Object>();
		DataOption dictService = (DataOption) SpringUtil.getBean("rcm.DataOption");
		List<Map<String, Object>> tbsxType = dictService.queryItemsByPcode(Constants.TBSX_TYPE);
		//通报事项类型
		map.put("tbsxType", tbsxType);
		return map;
	}

	@Override
	public Map<String, Object> queryByBusinessId(String businessId) {
		return this.bulletinInfoMapper.queryByBusinessId(businessId);
	}

	@Override
	public Map<String, Object> queryViewDefaultInfo(String businessId) {
		Map<String, Object> map = new HashMap<String, Object>();
		//oracle数据
		Map<String, Object> bulletinOracle = this.queryByBusinessId(businessId);
		//mongo数据
		Map<String, Object> bulletinMongo = this.baseMongo.queryById(businessId, Constants.RCM_BULLETIN_INFO);
		//审核日志
		List<Map<String, Object>> logs = this.bulletinAuditLogService.queryAuditLogs(businessId);
		
		map.put("bulletinMongo", bulletinMongo);
		map.put("bulletinOracle", bulletinOracle);
		map.put("logs", logs);
		return map;
	}

	@Override
	public void updateAuditStatusByBusinessId(String businessId,
			String auditStatus) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		map.put("auditStatus", auditStatus);
		this.bulletinInfoMapper.updateAuditStatusByBusinessId(map);
	}

	@Override
	public void updateAfterStartflow(String businessId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		map.put("applyTime", Util.now());
		map.put("auditStatus", "1");
		this.bulletinInfoMapper.updateAfterStartflow(map);
	}

	@Override
	public void updateAuditStageByBusinessId(String businessId,
			String stage) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		map.put("stage", stage);
		bulletinInfoMapper.updateAuditStageByBusinessId(map);
	}
	
	@Override
	public void quaryMettingSummary(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " createtime desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.bulletinInfoMapper.quaryMettingSummary(params);
		page.setList(list);
	}
	
	@Override
	public void queryMettingSummaryed(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看到所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() !=null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = "createtime desc";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.bulletinInfoMapper.queryMettingSummaryed(params);
		page.setList(list);
	}
	
	@Override
	public void saveMettingSummary(String businessId, String mettingSummaryInfo) {
		//1.保存会议纪要
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("mettingSummary", mettingSummaryInfo);
		//2.修改stage状态='5'
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		map.put("mettingSummaryTime", Util.now());
		map.put("stage", "5");
		this.baseMongo.updateSetById(businessId, data, Constants.RCM_BULLETIN_INFO);
		this.bulletinInfoMapper.updateStage(map);
		//归档
		this.daxtService.bulletinStart(businessId);
	}
	
	@Override
	public List<Map<String, Object>> queryBulletin() {
		return this.bulletinInfoMapper.queryBulletin();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveLegalLeaderAttachment(String businessId, String legalLeaderAttachment, String opinion) {
		Map<String, Object> map = new HashMap<String,Object>();
		if(Util.isNotEmpty(legalLeaderAttachment)){
			List<Document> fromJson = JsonUtil.fromJson(legalLeaderAttachment, ArrayList.class);
			map.put("legalLeaderAttachment", fromJson);
		}
		if(Util.isNotEmpty(opinion)){
			map.put("legalLeaderOpinion", opinion);
		}
		if(Util.isNotEmpty(map)){
			this.baseMongo.updateSetById(businessId, map, Constants.RCM_BULLETIN_INFO);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveReviewLeaderAttachment(String businessId, String reviewLeaderAttachment, String opinion) {
		Map<String, Object> map = new HashMap<String,Object>();
		
		if(Util.isNotEmpty(reviewLeaderAttachment)){
			List<Document> fromJson = JsonUtil.fromJson(reviewLeaderAttachment, ArrayList.class);
			map.put("reviewLeaderAttachment", fromJson);
		}
		if(Util.isNotEmpty(opinion)){
			map.put("reviewLeaderOpinion", opinion);
		}
		if(Util.isNotEmpty(map)){
			this.baseMongo.updateSetById(businessId, map, Constants.RCM_BULLETIN_INFO);
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public void updateBaseFile(String businessId, String attachment) {
		Map<String, Object> map = new HashMap<String,Object>();
		if(Util.isNotEmpty(attachment)){
			List<Document> fromJson = JsonUtil.fromJson(attachment, ArrayList.class);
			map.put("fileList", fromJson);
		}
		if(Util.isNotEmpty(map)){
			this.baseMongo.updateSetById(businessId, map, Constants.RCM_BULLETIN_INFO);
		}
	}

	@Override
	public void saveTaskPerson(String businessId, String json) {
		Document doc = Document.parse(json);
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("taskallocation", doc);
		this.baseMongo.updateSetById(businessId, map, Constants.RCM_BULLETIN_INFO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveRiskLeaderAttachment(String businessId, String riskLeaderAttachment, String opinion) {
		Map<String, Object> map = new HashMap<String,Object>();
		
		if(Util.isNotEmpty(riskLeaderAttachment)){
			List<Document> fromJson = JsonUtil.fromJson(riskLeaderAttachment, ArrayList.class);
			map.put("riskLeaderAttachment", fromJson);
		}
		if(Util.isNotEmpty(opinion)){
			map.put("riskLeaderOpinion", opinion);
		}
		if(Util.isNotEmpty(map)){
			this.baseMongo.updateSetById(businessId, map, Constants.RCM_BULLETIN_INFO);
		}
	}

	@Override
	public void updateByBusinessIdWithBulletinypeId(String businessId, String bulletinypeId) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		map.put("bulletinTypeId", bulletinypeId);
		this.bulletinInfoMapper.updateByBusinessIdWithBulletinypeId(map);
	}

	/**
	 * 统计所有评审项目
	 */
	public int countAll(){
		
		return bulletinInfoMapper.countAll();
	}

	@Override
	public void updatePerson(HashMap<String, Object> data) {
		bulletinInfoMapper.updatePerson(data);
	}

	@Override
	public List<Map<String, Object>> queryByStageAndstate(String stage,String state) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("stage", stage);
		map.put("state", state);
		return this.bulletinInfoMapper.queryByStageAndstate(map);
	}
	
	@Override
	public void startPigeonholeByBusinessId(String businessId,Date pigeonholeTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("pigeonholeStatus", "1");
		params.put("pigeonholeTime", pigeonholeTime);
		bulletinInfoMapper.startPigeonholeByBusinessId(params);
	}
	
	@Override
	public void updatePigeStatByBusiId(String businessId, String status){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("pigeonholeStatus",status);
		bulletinInfoMapper.updatePigeStatByBusiId(params);
	}
	
	@Override
	public void cancelPigeonholeByBusinessId(String businessId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("pigeonholeStatus", "");
		params.put("pigeonholeTime",null);
		bulletinInfoMapper.cancelPigeonholeByBusinessId(params);
	}

	@Override
	public void updatePertainAreaId(String id, String pertainAreaId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("pertainAreaId", pertainAreaId);
		bulletinInfoMapper.updatePertainAreaId(params);
	}
	
	@Override
	public List<Map<String, Object>> queryBulletinCount(String wf_state,
			String stage, String pertainAreaId, String serviceTypeId,String year) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("wf_state", wf_state);
		map.put("stage", stage);
		if(Util.isEmpty(year)){
			int y = Calendar.getInstance().get(Calendar.YEAR);
			map.put("year", y);
		}else{
			map.put("year", year);
		}
		
		if(Util.isNotEmpty(pertainAreaId)){
			//处理大区id
			String[] ids = pertainAreaId.split(",");
			pertainAreaId = "";
			for (String string : ids) {
				pertainAreaId +=",'"+string + "'";
			}
			pertainAreaId = pertainAreaId.substring(1);
		}
		map.put("pertainAreaId", pertainAreaId);
		//处理serviceTypeId
		if(Util.isNotEmpty(serviceTypeId)){
			String[] sids = serviceTypeId.split(",");
			serviceTypeId = "";
			for (String string : sids) {
				serviceTypeId +=",'"+string + "'";
			}
			serviceTypeId = serviceTypeId.substring(1);
		}
		
		map.put("serviceTypeId", serviceTypeId);
		return bulletinInfoMapper.queryBulletinCount(map);
	}

	@Override
	public List<Map<String, Object>> queryBulletinPertainArea() {
		List<Map<String, Object>> list = bulletinInfoMapper.queryBulletin();
		List<String> areaList = new ArrayList<String>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : list) {
			
			String pertainAreaId = (String) map.get("PERTAINAREAID");
			
			if(!areaList.contains(pertainAreaId)){
				
				areaList.add(pertainAreaId);
				
			}
			
		}
		
		for (String pertainAreaId : areaList) {
			
			Map<String, Object> org = this.orgService.queryByPkvalue(pertainAreaId);
			
			result.add(org);
		
		}
			
		return result;
	}

	@Override
	public void queryAllInfoByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String,Object>> list = bulletinInfoMapper.queryAllInfoByPage(params);
		page.setList(list);		
	}
	
	@Override
	public List<Map<String, Object>> queryAllByDaxt() {
		return bulletinInfoMapper.queryAllByDaxt();
	}
	
	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public void addNewAttachment(String json) {
		
		Document doc = Document.parse(json);
		
		String businessId = (String) doc.get("businessId");
		String oldFileName = (String) doc.get("oldFileName");
		Document item = (Document) doc.get("item");
		String fileId = (String) item.get("fileId");
		Document type = (Document) item.get("type");
		Document itemType = (Document) item.get("itemType");
		String fileName = (String) item.get("fileName");
		//最後提交人信息
		Document lastUpdateBy = (Document) item.get("lastUpdateBy");
		String lastUpdateData = (String) item.get("lastUpdateData"); 
		
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_BULLETIN_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachmentList");
		
		if (attachmentList == null) {
			attachmentList = new ArrayList<Map<String, Object>>();
			Map<String, Object> file = new HashMap<String,Object>();
			file.put("fileId", fileId);
			file.put("fileName", fileName);
			file.put("oldFileName", oldFileName);
			file.put("type", type);
			file.put("itemType", itemType);
			file.put("lastUpdateBy", lastUpdateBy);
			file.put("lastUpdateData", lastUpdateData);
			attachmentList.add(file);
		} else {
			Map<String, Object> file = new HashMap<String,Object>();
			file.put("fileId", fileId);
			file.put("fileName", fileName);
			file.put("oldFileName", oldFileName);
			file.put("type", type);
			file.put("itemType", itemType);
			file.put("lastUpdateBy", lastUpdateBy);
			file.put("lastUpdateData", lastUpdateData);
			attachmentList.add(file);
		}

		
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachmentList", attachmentList);
		baseMongo.updateSetById(businessId, data, Constants.RCM_BULLETIN_INFO);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void deleteAttachment(String json) {
		Document doc = Document.parse(json);
		String businessId = (String) doc.get("businessId");
		String fileId = doc.get("fileId") + "";
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_BULLETIN_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachmentList");
		System.out.println("fileId ====" + fileId);
		for (int i = 0; i < attachmentList.size(); i++) {
			Document attachment = (Document) attachmentList.get(i);
			System.out.println("attachment=           " + attachment);
			System.out.println(attachment.get("fileId"));
			System.out.println(attachment.get("fileId") .equals(fileId));
			if (attachment.get("fileId") .equals(fileId)) {
				attachmentList.remove(i);
				break;
			}
		}
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachmentList", attachmentList);
		baseMongo.updateSetById(businessId, data, Constants.RCM_BULLETIN_INFO);
	}

	/*@SuppressWarnings("unchecked")
	@Override
	public void updateAttachment(String json) {
		Document doc = Document.parse(json);
		String uuid = (String) doc.get("UUID");
		String version = doc.get("version").toString();
		String businessId = (String) doc.get("businessId");
		String fileName = (String) doc.get("fileName");
		String filePath = (String) doc.get("filePath");
		
		Document programmed = (Document) doc.get("programmed");
		
		Document approved = (Document) doc.get("approved");
		
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_BULLETIN_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachment");
		
		for (Map<String, Object> attachment : attachmentList) {
			if(attachment.get("UUID").toString().equals(uuid)){
				List<Map<String, Object>> filesList = (List<Map<String, Object>>) attachment.get("files");
				if(Util.isNotEmpty(filesList)){
					for (Map<String, Object> files : filesList) {
						if(files.get("version").toString().equals(version)){
							files.put("fileName", fileName);
							files.put("filePath", filePath);
							files.put("approved", approved);
							files.put("programmed", programmed);
						}
					}
				}
			}
		}
		
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachment", attachmentList);
		baseMongo.updateSetById(businessId, data, Constants.RCM_BULLETIN_INFO);
	}*/
}
