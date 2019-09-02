/**
 * 
 */
package rcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;

import org.junit.Test;

import com.google.gson.Gson;

import common.PageAssistant;
import rcm.NoticeInfo;
import util.DbUtil;
import util.Util;

/**
 * @Description: 测试通知及预警操作
 * @Author zhangkewei
 * @Date 2016年9月26日 下午4:47:18
 */
public class NoticeInfoTest {
	
	static{
		DbUtil.init("configuration.xml", "db.properties");
	}
	
	@Test
	public void insertWarningInfo(){
		try{
			NoticeInfo ns = new NoticeInfo();
			ns.insertWarningInfo();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void insert() {
		try {
			for(int i=0;i<10;i++){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", Util.getUUID());
				map.put("infoSubject", "infoSubject"+i);
//				map.put("processInstanceId", "processInstanceId"+i);
				map.put("taskId", "");
				map.put("businessId", "businessId"+i);
				map.put("remark", "remark"+i);
				map.put("formKey", "formKey"+i);
				map.put("createBy", "createBy"+i);
				map.put("reader", "reader"+i);
				map.put("type", "0");
				map.put("state", "1");
				NoticeInfo ns = new NoticeInfo();
				ns.insert(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void update(){
		/*Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", "da418765-006f-4d17-b23f-a764c0e4de63");
		map.put("state", "1");
		NoticeInfo ns = new NoticeInfo();
		ns.update(map);*/
	}
	
	@Test
	public void findByPage(){
		String json = "{currentPage:'1', pageSize:'2',queryObj:{type:'1'}}";
		NoticeInfo ns = new NoticeInfo();
		PageAssistant page = ns.findByPage(json);
		Gson gs = new Gson();
		System.out.println(gs.toJson(page));
	}
	
	@Test
	public void findByPageWithOutRowCount(){
		String json = "{currentPage:'1', pageSize:'10', queryObj:{type:'1'}}";
		NoticeInfo ns = new NoticeInfo();
		List<Map> list = ns.findByPageWithOutRowCount(json);
		Gson gs = new Gson();
		System.out.println(gs.toJson(list));
	}
	
	@Test
	public void findNoticeInfoById(){
		String json = "{id:'4278f720-eaf2-4c7f-a691-73016c04108c'}";
		NoticeInfo ns = new NoticeInfo();
		Map map = ns.findNoticeInfoById(json);
		Gson gs = new Gson();
		System.out.println(gs.toJson(map));
	}
}
