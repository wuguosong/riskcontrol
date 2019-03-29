/**
 * 
 */
package com.yk.rcm.bulletin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rcm.DataOption;

import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.rcm.bulletin.dao.IBulletinReviewMapper;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import com.yk.rcm.bulletin.service.IBulletinReviewService;
import common.Constants;
import common.PageAssistant;

/**
 * @author wufucan
 *
 */
@Service
@Transactional
public class BulletinReviewService implements IBulletinReviewService {
	@Resource
	private IBulletinInfoService bulletinInfoService;
	@Resource
	private IBulletinReviewMapper bulletinReviewMapper;
	@Resource
	private IBaseMongo baseMongo;
	
	
	@Override
	public void queryWaitList(PageAssistant page) {
		//查询待办的sql
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		/*if(ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("isAdmin", "1");
		}*/
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " applytime desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.bulletinReviewMapper.queryWaitByPage(params);
		for(int i = 0; i < list.size(); i++){
			Map<String, Object> oracleData = list.get(i);
			String businessId = (String) oracleData.get("BUSINESSID");
			Map<String, Object> mongo = this.baseMongo.queryById(businessId, Constants.RCM_BULLETIN_INFO);
			oracleData.put("MONGO", mongo);
		}
		page.setList(list);
		
	}

	@Override
	public void queryAuditedList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		/*if(ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			assignUserId = null;
		}*/
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " applytime desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.bulletinReviewMapper.queryApplyedByPage(params);
		page.setList(list);
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
	public Map<String, Object> queryViewDefaultInfo(String businessId) {
		Map<String, Object> map = this.bulletinInfoService.queryViewDefaultInfo(businessId);
		return map;
	}
}
