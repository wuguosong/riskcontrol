/**
 * 
 */
package com.yk.rcm.noticeofdecision.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.rcm.noticeofdecision.dao.INoticeOfDecisionMapper;
import com.yk.rcm.noticeofdecision.service.INoticeOfDecisionService;
import common.Constants;

/**
 * @author 80845530
 *
 */
@Service
@Transactional
public class NoticeOfDecisionService implements INoticeOfDecisionService {
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private INoticeOfDecisionMapper noticeOfDecisionMapper;
	
	@Override
	public Map<String, Object> queryMongoByFormalId(String businessId) {
		BasicDBObject queryWhere = new BasicDBObject();
		queryWhere.put("projectFormalId", businessId);
		List<Map<String, Object>> list = this.baseMongo.queryByCondition(queryWhere, Constants.RCM_NOTICEDECISION_INFO);
		if(list != null && list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public Map<String, Object> queryOracleByFormalId(String formalId) {
		return this.noticeOfDecisionMapper.queryOracleByFormalId(formalId);
	}

	@Override
	public Map<String, Object> queryOracleById(String businessId) {
		return this.noticeOfDecisionMapper.queryOracleById(businessId);
	}
	
}
