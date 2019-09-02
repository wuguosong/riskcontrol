package rcm;

import common.BaseService;
import common.BusinessException;
import common.PageAssistant;
import org.bson.Document;
import util.DbUtil;
import util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Pteam extends BaseService{
	public PageAssistant getAll(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "team.selectTeam");
		return assistant;
	}
	public List<Map> viewAll(String id){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", id);
		List<Map> map = DbUtil.openSession().selectList("team.selectViewAll", paramMap);
		return map;
	}
	
	public String createTeam(String json){
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String teamId = Util.getUUID();
		paramMap.put("uuid", teamId);
		String teamName=bjson.get("TEAM_NAME").toString();
		paramMap.put("team_name", teamName);
		paramMap.put("type", bjson.get("TYPE"));
		Map<String,Object> leader = (Map<String,Object>)bjson.get("leader");
		if(leader != null){
			paramMap.put("team_leader", leader.get("NAME"));
			paramMap.put("team_leaderID", leader.get("VALUE"));
		}
		paramMap.put("orderNum", bjson.get("ORDERNUM"));
		String count = DbUtil.openSession().selectOne("team.selectCountName", paramMap);
		DbUtil.close();
		
		String team_memberName=bjson.get("TEAM_MEMBER_NAME").toString();
		String team_memberId=bjson.get("TEAM_MEMBER_ID").toString();
	    if("0".equals(count)){
		    Integer o=	DbUtil.openSession().insert("team.insertTeam", paramMap);
		    DbUtil.close();
		    
		    if(!"".equals(team_memberId) && null!=team_memberId && !"null".equals(team_memberId)){
			    String[] arrayID={},arrayNAME={};
			    arrayID=team_memberId.split(",");
				arrayNAME=team_memberName.split(",");
				for(int i=0;i<arrayID.length;i++){
					Map<String, String> paramMap2 = new HashMap<String, String>();
					paramMap2.put("UUID", Util.getUUID());
					paramMap2.put("TEAM_ID", teamId);
					paramMap2.put("TEAM_NAME", teamName);
					paramMap2.put("TEAM_MEMBER_NAME", arrayNAME[i]);
					paramMap2.put("TEAM_MEMBER_ID", arrayID[i]);
					Integer ee = DbUtil.openSession().insert("team.insertAll", paramMap2);
				}
		    }
		    return o.toString();
	    }else{
	    	throw new BusinessException("name不能重复");
	    }
	}
	
	public String updateTeam(String json){
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String uuid=bjson.get("UUID").toString();
		paramMap.put("uuid", uuid);
		String teamName=bjson.get("TEAM_NAME").toString();
		paramMap.put("team_name", teamName);
		paramMap.put("type", bjson.get("TYPE"));
		Map<String,Object> leader = (Map<String,Object>)bjson.get("leader");
		if(leader != null){
			paramMap.put("team_leader", leader.get("NAME"));
			paramMap.put("team_leaderID", leader.get("VALUE"));
		}
		paramMap.put("orderNum", bjson.get("ORDERNUM"));
		String count = DbUtil.openSession().selectOne("team.selectCountName", paramMap);
		DbUtil.close();
		if("0".equals(count)){
		    Integer o=	DbUtil.openSession().update("team.updateTeam", paramMap);
		    DbUtil.close();
		    String team_memberName=bjson.get("TEAM_MEMBER_NAME").toString();
			String team_memberId=bjson.get("TEAM_MEMBER_ID").toString();
		    if(!"".equals(team_memberId) && null!=team_memberId && !"null".equals(team_memberId)){
		    	DbUtil.openSession().delete("team.deleteTeamItem", paramMap);
				DbUtil.close();
			    String[] arrayID={},arrayNAME={};
			    arrayID=team_memberId.split(",");
				arrayNAME=team_memberName.split(",");
				for(int i=0;i<arrayID.length;i++){
					Map<String, String> paramMap2 = new HashMap<String, String>();
					paramMap2.put("UUID", Util.getUUID());
					paramMap2.put("TEAM_ID", uuid);
					paramMap2.put("TEAM_NAME", teamName);
					paramMap2.put("TEAM_MEMBER_NAME", arrayNAME[i]);
					paramMap2.put("TEAM_MEMBER_ID", arrayID[i]);
					Integer ee = DbUtil.openSession().insert("team.insertAll", paramMap2);
				}
		    }
		    
		    return o.toString();
		}else{
			throw new BusinessException("评审小组已存在");
		}
	}
	
	public Map<String, Object> getTeamByID(String id){
		    Map<String, Object> map = new HashMap<String, Object>();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("uuid", id);
			Map team = DbUtil.openSession().selectOne("team.selectOneTeam", paramMap);
			DbUtil.close();
			
			List<Map> teamItem = DbUtil.openSession().selectList("team.selectTeamItem", paramMap);
			DbUtil.close();
		map.put("team", team);
		map.put("teamItem", teamItem);
		return map;
	}
	
	public String deleteTeamByID(String id){
		String[] array={};
		array=id.split(",");
		for(String ids :array){
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("uuid", ids);
			DbUtil.openSession().delete("team.deleteTeam", paramMap);
			DbUtil.close();
//			DbUtil.openSession().delete("team.deleteTeamItem", paramMap);
//			DbUtil.close();
		}
		return "";
	}
	

	public String insertAll(String json) {
		Document bjson = Document.parse(json);
		UUID uuid = UUID.randomUUID();
		Map<String, Object> team_item = new HashMap<String, Object>();
		team_item.put("uuid", uuid.toString());
		team_item.put("team_name_i", bjson.get("TEAM_NAME").toString());
		team_item.put("team_id_i", bjson.get("TEAM_ID").toString());
		team_item.put("team_by_name", bjson.get("TEAM_PEOPLE").toString());
		team_item.put("team_by_id", bjson.get("TEAM_MAN").toString());
		
		Integer o = DbUtil.openSession().insert("team.insertAll", team_item);
		DbUtil.close();
		return o.toString();

	}
	/**
	 * 根据组员ID查询组长信息
	 * @param reviewLeaderId type:1评审小组 2法律小组
	 * @return {team_leader:'', team_leader_id:''}
	 */
	public Map<String, String> selectTeamLeaderByMemberId(String reviewLeaderId, String type){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("teamMemberId", reviewLeaderId);
		params.put("type", type);
		List<Map<String, String>> list = DbUtil.openSession().selectList("team.selectTeamLeaderByMemberId", params);
		Map<String, String> retMap = new HashMap<String, String>();
		if(Util.isNotEmpty(list)){
			retMap = list.get(0);
		}
		return retMap;
	}
	//参数同上
	public Map<String, String> getTeamLeaderByMemberId(String json){
		Map<String, Object> params = Util.parseJson2Map(json);
		List<Map<String, String>> list = DbUtil.openSession().selectList("team.selectTeamLeaderByMemberId", params);
		Map<String, String> retMap = new HashMap<String, String>();
		if(Util.isNotEmpty(list)){
			Map<String, String> result = list.get(0);
			retMap.put("NAME", result.get("TEAM_LEADER"));
			retMap.put("VALUE", result.get("TEAM_LEADER_ID"));
		}
		return retMap;
	}
}
