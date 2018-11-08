package com.yk.ext.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;
import com.yk.rcm.bulletin.service.IBulletinInfoService;

import common.Constants;
/**
 * 其他需决策事项阶段数据刷新(stage)
 * @author shaosimin
 *
 */
@Service("refreshBulletinStage")
@Transactional
public class RefreshBulletinStage implements Initable {

	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IBulletinInfoService bulletinInfoService;
	
	@Override
	public void execute() {
		//查询oracle：rcm_bullrtin_info数据并循环取出
		List<Map<String, Object>> bulletinInfoList = this.bulletinInfoService.queryBulletin();
			for (int i = 0; i < bulletinInfoList.size(); i++) {
					Map<String,Object> bulletinInfo = bulletinInfoList.get(i);
					String businessid = (String) bulletinInfo.get("BUSINESSID");
					String auditstatus = (String) bulletinInfo.get("AUDITSTATUS");
					if(auditstatus.equals("5") || auditstatus.equals("4")){
						this.bulletinInfoService.updateAuditStatusByBusinessId(businessid, "2");
					}else if(auditstatus.equals("0") || auditstatus.equals("1") || auditstatus.equals("3")){
						this.bulletinInfoService.updateAuditStageByBusinessId(businessid, "1");
					}else if(auditstatus.equals("2")){
						//查询mongo
						BasicDBObject queryWhere = new BasicDBObject();
						queryWhere.put("_id", businessid);
						Map<String, Object> map = baseMongo.queryByCondition(queryWhere, Constants.RCM_BULLETIN_INFO).get(0);
						if(!map.containsKey("meeting") || map.get("meeting")==null){
							this.bulletinInfoService.updateAuditStageByBusinessId(businessid, "2");
						}else{
							String  meetingTime = (String) map.get("meetingTime");
							if(null == meetingTime){
								this.bulletinInfoService.updateAuditStageByBusinessId(businessid, "4");
								continue;
							}
							Date meetingDate = null;
							meetingDate = Util.parse(meetingTime, "yyyy-MM-dd");
							if(meetingDate.after(new Date())){
								this.bulletinInfoService.updateAuditStageByBusinessId(businessid, "3");
							}else if(meetingDate.before(new Date()) || meetingDate==null){
								this.bulletinInfoService.updateAuditStageByBusinessId(businessid, "4");
							}
						}
					}
				
			}
	}
}
