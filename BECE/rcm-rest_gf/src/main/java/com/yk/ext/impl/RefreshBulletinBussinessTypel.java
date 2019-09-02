package com.yk.ext.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;
import com.yk.rcm.bulletin.service.IBulletinInfoService;

import common.Constants;

@Service("refreshBulletinBussinessTypel")
@Transactional
public class RefreshBulletinBussinessTypel implements Initable{

	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IBulletinInfoService bulletinInfoService;
	
	@Override
	public void execute() {
		List<Map<String,Object>> bulletinList = bulletinInfoService.queryBulletin();
		for (Map<String, Object> map : bulletinList) {
			String businessid = map.get("BUSINESSID").toString();
			//事项类型的ID
			String typeId = map.get("BULLETINTYPEID").toString();
			
			//合并后的事项类型 战略投资
			String zltzName = "战略投资";
			//合并后的事项类型id战略投资
			String zltzTypeId = "56491D6ED6C679A2E05327140A0A7528";
			//如果事项决策的类型是其他融资事项或者是财务投资人项目审批事项  将BULLETINYPEID 改为战略投资
			if( typeId.equals("7d25141f336d4e398898431cc636d896") || typeId.equals("39e885785f224f3498b4d70e8e5e25dd") ){
				//更新oracle
				bulletinInfoService.updateByBusinessIdWithBulletinypeId(businessid,zltzTypeId);
				//更新mongo
				Document doc1 = new Document();
				Document doc2 = new Document();
				doc1.put("NAME", zltzName);
				doc1.put("VALUE", zltzTypeId);
				doc2.put("bulletinType", doc1);
				baseMongo.updateSetById(businessid, doc2, Constants.RCM_BULLETIN_INFO);
				
			}
			
		}
	}
}
