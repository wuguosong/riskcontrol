/**
 * 
 */
package com.yk.rcm.formalAssessment.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.print.Doc;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.DbUtil;
import util.ThreadLocalUtil;
import util.Util;

import com.yk.common.IBaseMongo;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.formalAssessment.dao.IProfessionMapper;
import com.yk.rcm.formalAssessment.service.IProfessionService;

import common.Constants;
import common.PageAssistant;
import common.Result;

/**
 * @author wufucan
 *
 */
@Service
@Transactional
public class ProfessionService implements IProfessionService {
	@Resource
	private IProfessionMapper professionMapper;
	@Resource
	private IBaseMongo baseMongo;
	/* (non-Javadoc)
	 * @see com.yk.rcm.formalAssessment.service.IProfessionService#addTeam(java.util.Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Result addTeam(String json) {
		Result result = new Result();
		
		Document pro = Document.parse(json);
//		Map<String, Object> pro = JsonUtil.fromJson(json, Map.class);
		Document proTeam = (Document) pro.get("team");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String teamId = Util.getUUID();
		paramMap.put("uuid", teamId);
		@SuppressWarnings("unchecked")
		Map<String, Object> review_leader  = (Map<String, Object>) pro.get("review_leader");
		paramMap.put("review_leader", review_leader.get("NAME"));
		paramMap.put("review_leader_id", review_leader.get("VALUE"));
		String reviewType=(String) proTeam.get("REVIEW_TYPE");
		paramMap.put("review_type", reviewType);
		String status=(String) proTeam.get("STATUS");
		paramMap.put("status", status);
		Integer ordernum = proTeam.getInteger("ORDERNUM");
		paramMap.put("ordernum", ordernum);
			this.professionMapper.insertProfessionTeam(paramMap);
		List<Map<String, Object>> review_memberName  = (List<Map<String, Object>>) pro.get("review_team_membername");
		for (int i = 0; i < review_memberName.size(); i++) {
			Map<String,Object> memberName = review_memberName.get(i);
			String membername = (String) memberName.get("NAME");
			String memberId = (String) memberName.get("VALUE");
	    if(!"".equals(memberId) || null!=memberId ){
				Map<String, Object> paramMap2 = new HashMap<String, Object>();
				paramMap2.put("uuid", Util.getUUID());
				paramMap2.put("review_team_id", teamId);
				paramMap2.put("review_type", reviewType);
				paramMap2.put("review_team_membername", membername);
				paramMap2.put("review_team_memberid", memberId);
				paramMap2.put("status", status);
				paramMap2.put("ordernum", ordernum);
				this.professionMapper.insertAll(paramMap2);
	    }
	}
		return result;
	}
	/* (non-Javadoc)
	 * @see com.yk.rcm.formalAssessment.service.IProfessionService#queryAllTeams()
	 */

	@Override
	public void queryAllTeams(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("userId", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = this.professionMapper.queryAllTeams(params);
		page.setList(list);
	}

	@Override
	public List<Map<String, Object>> queryMembersByTeamId(String teamId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", teamId);
		List<Map<String, Object>> map = this.professionMapper.queryMembersByTeamId(paramMap);
		return map;
	}

	@Override
	public Map<String, Object> getTeamByID(String teamId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("uuid", teamId);
		Map<String, Object> teamMap = this.professionMapper.selectOneTeam(paramMap);
		List<Map<String, Object>> teamItemMap = this.professionMapper.selectTeamItem(paramMap);
		map.put("team", teamMap);
		map.put("teamItem", teamItemMap);
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result updateTeam(String json) {
		Result result = new Result();
		Document pro = Document.parse(json);
		Document proTeam = (Document) pro.get("team");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String teamId = (String) proTeam.get("UUID");
		paramMap.put("uuid", teamId);
		Map<String, Object> review_leader  = (Map<String, Object>) pro.get("review_leader");
		paramMap.put("review_leader", review_leader.get("NAME"));
		paramMap.put("review_leader_id", review_leader.get("VALUE"));
		String reviewType=(String) proTeam.get("REVIEW_TYPE");
		paramMap.put("review_type", reviewType);
		String status=(String) proTeam.get("STATUS");
		paramMap.put("status", status);
		Integer ordernum= proTeam.getInteger("ORDERNUM");
		paramMap.put("ordernum", ordernum);
		this.professionMapper.updateTeam(paramMap);
		List<Map<String, Object>> review_memberName  = (List<Map<String, Object>>) pro.get("review_team_membername");
		this.professionMapper.deleteItem(paramMap);
		this.professionMapper.deleteTeam(paramMap);
		
		this.professionMapper.insertProfessionTeam(paramMap);
		
		for (int i = 0; i < review_memberName.size(); i++) {
			Map<String,Object> memberName = review_memberName.get(i);
			String membername = (String) memberName.get("NAME");
			String memberId = (String) memberName.get("VALUE");
			Map<String, Object> paramMap2 = new HashMap<String, Object>();
			paramMap2.put("uuid", Util.getUUID());
			paramMap2.put("review_team_id", teamId);
			paramMap2.put("review_type", reviewType);
			paramMap2.put("review_team_membername", membername);
			paramMap2.put("review_team_memberid", memberId);
			paramMap2.put("status", status);
			paramMap2.put("ordernum",ordernum);
			this.professionMapper.insertAll(paramMap2);
		}
		return result;
	}

	@Override
	public void updateTeamSatusById(String teamId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uuid", teamId);
		params.put("status", "2");
		this.professionMapper.updateTeamSatusById(params);
	}

	@Override
	public List<Map<String, Object>> queryProfessionReview() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", "1");
		return this.professionMapper.queryProfessionReview(params);
	}


	

}
