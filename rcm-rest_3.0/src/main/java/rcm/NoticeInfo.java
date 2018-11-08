/**
 * 
 */
package rcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import util.DbUtil;
import util.Util;
import ws.client.PortalClient;

import common.BaseService;
import common.PageAssistant;

/**
 * @Description: 通知预警信息表管理
 * @Author zhangkewei
 * @Date 2016年9月26日 下午4:14:15  
 */
public class NoticeInfo extends BaseService {
	
	private String[] keys = {"infoSubject","processInstanceId","taskId","businessId",
			"remark","formKey","createBy","reader","readerName","type", 
			"custText01", "custText02", "custText03", "custText04"}; 
	
	public void insertWarningInfo(){
		SqlSession session = DbUtil.openSession();
		
		List<Map<String,Object>> warningInfos = session.selectList("noticeInfo.selectWarningInfo");
		
		for(Map<String,Object> warningInfo : warningInfos){
			insert(warningInfo);
		}
		DbUtil.close();
	}
	
	public void insert(Map<String, Object> map){
		SqlSession session = DbUtil.openSession();
		for(String key : keys){
			if(map.containsKey(key.toUpperCase())){
				map.put(key, map.get(key.toUpperCase()));
				map.remove(key.toUpperCase());
			}else if(map.get(key) == null){
				map.put(key, "");
			}
		}
		map.put("id", Util.getUUID());
		map.put("state", "1");
		session.insert("noticeInfo.insertNoticeInfo", map);
		DbUtil.close();
	}

	public void batchInsert(List<Map<String, Object>> list){
		SqlSession session = DbUtil.openSession(false);
		try {
			if (Util.isNotEmpty(list)) {
				for (Map<String, Object> map : list) {
					for (String key : keys) {
						if (map.containsKey(key.toUpperCase())) {
							map.put(key, map.get(key.toUpperCase()));
							map.remove(key.toUpperCase());
						} else if (map.get(key) == null) {
							map.put(key, "");
						}
					}
					map.put("id", Util.getUUID());
					map.put("state", "1");
					session.insert("noticeInfo.insertNoticeInfo", map);
				}
			}
			DbUtil.commit();
		}catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			DbUtil.close();
		}
	}
	//未读变已读
	public void update(String json) {
		Map<String, Object> map = Util.parseJson2Map(json);
		SqlSession session = DbUtil.openSession();
		session.update("noticeInfo.updateNoticeInfo", map);
		DbUtil.close();
		//将已读的通知同步到门户系统
		//PortalClient pc = new PortalClient((String)map.get("id"));
		//pc.start();
	}
	
	
	//查询分页列表
	public PageAssistant findByPage(String json){
		PageAssistant page = new PageAssistant(json);
		this.selectByPage(page, "noticeInfo.selectNoticeInfo");
		return page;
	}
	
	//查询固定条数记录
	public List<Map> findByPageWithOutRowCount(String json){
		PageAssistant page = new PageAssistant(json);
		RowBounds bounds = new RowBounds(page.getOffsetRow(), page.getPageSize());
		List<Map> list = DbUtil.openSession().selectList("noticeInfo.selectNoticeInfo", page.getParamMap(), bounds);
		DbUtil.close();
		return list;
	}
	
	//查询单条记录
	public Map findNoticeInfoById(String json){
		Map params = Util.parseJson2Map(json);
		Map map = DbUtil.openSession().selectOne("noticeInfo.selectNoticeInfo", params);
		DbUtil.close();
		return map;
	}
	
	//发送通知
	public void sendNotice(String json){
		Map<String, Object> notice = Util.parseJson2Map(json);
		if(notice != null){
			NoticeInfo ns = new NoticeInfo();
			List<Map> readers = (List<Map>)notice.get("reader");
			if(Util.isEmpty(readers)) return;
			for(Map rd : readers){
				notice.put("reader",rd.get("value"));
				notice.put("readerName",rd.get("name"));
				ns.insert(notice);
			}
		}
	}
	
	//查询通知,未读，用于同步到门户系统
	public List<Map<String, Object>> findForPortalToRead(String taskId, String businessId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("taskId", taskId);
		params.put("businessId", businessId);
		SqlSession session = DbUtil.openSession();
		List<Map<String, Object>> list = session.selectList("noticeInfo.selectForPortalToRead", params);
		DbUtil.close();
		return list;
	}
	
	//查询通知,已读，用于同步到门户系统
	public List<Map<String, Object>> findForPortalRead(String noticeId){
		SqlSession session = DbUtil.openSession();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", noticeId);
		List<Map<String, Object>> list = session.selectList("noticeInfo.selectForPortalRead", noticeId);
		DbUtil.close();
		return list;
	}
}
