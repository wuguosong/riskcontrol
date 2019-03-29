/**
 * 
 */
package ws.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import rcm.NoticeInfo;
import rcm.WebServiceLog;
import util.DbUtil;
import util.PropertiesUtil;
import util.Util;
import ws.client.portal.IUpToDoService;
import ws.client.portal.UpToDoServiceImplService;

/**
 * @Description: 同步待办已办，未读通知，已读通知到门户系统
 * @Author zhangkewei
 * @Date 2016年11月21日 下午12:33:46  
 */
public class PortalClient extends Thread {
	private static Logger logger = Logger.getLogger(PortalClient.class);
	private static final String sysCode = PropertiesUtil.getProperty("ws.sysCode");
	private static final String preFix = PropertiesUtil.getProperty("domain.allow");
	private static final String wsEanble = PropertiesUtil.getProperty("ws.enable");
	
	private String preTaskId;
	private List<String> currentTaskIdList;
	private String type;
	private String businessId;
	private String noticeId;
	private List<Map<String, Object>> list = null;
	
	public PortalClient(){
		
	}
	//通知未读
	public PortalClient(String taskId, String businessId){
		this.preTaskId = taskId;
		this.businessId = businessId;
		this.type="notice_toread";
	}
	//通知已读
	public PortalClient(String noticeId){
		this.noticeId = noticeId;
		this.type="notice_read";
	}
	
	//构造待办已办消息参数
	public PortalClient(String preTaskId, List<String> currentTaskIdList){
		this.preTaskId = preTaskId;
		this.currentTaskIdList = currentTaskIdList;
		this.type="task";
	}
	
	@Override
	public void run() {
		if("false".equals(wsEanble)) return;
		String message="",ret="", error="";
		long time = System.currentTimeMillis();
		try {
			message = this.getMessage(); 
			UpToDoServiceImplService service = new UpToDoServiceImplService();
			IUpToDoService port = service.getTodoPort();
			ret = port.todoList(message);
		} catch (Exception e) {
			logger.error(Util.parseException(e));
			error = e.getMessage();
		}finally{
			long useTime = System.currentTimeMillis()-time;
			WebServiceLog.portalInsert(message, ret, useTime, error);
		}
	}
	
	/**
	 * 获取报文信息
	 * @return
	 */
	private String getMessage() {
		if("task".equals(type)){//任务-待办或已办
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("preTaskId", preTaskId);
			params.put("currentTaskIds", StringUtils.join(currentTaskIdList.toArray(),","));
			SqlSession session = DbUtil.openSession();
			list = session.selectList("workflow.selectForPortal", params);
			DbUtil.close();
		}else if("notice_toread".equals(type)){
			NoticeInfo notice = new NoticeInfo();
			list = notice.findForPortalToRead(preTaskId, businessId);
		}else if("notice_read".equals(type)){
			NoticeInfo notice = new NoticeInfo();
			list = notice.findForPortalRead(noticeId);
		}
		Document doc = this.createBaseDoc();
		this.addElementsByList(doc, list);
		XMLOutputter XMLOut = new XMLOutputter();  
		return XMLOut.outputString(doc);
	}
	
	
	
	
	private Document createBaseDoc(){
		Document doc = new Document();
		Element root = new Element("todo_list");
		doc.setRootElement(root);
		Element header = new Element("header");
		root.setContent(header);
		Element syscode = new Element("syscode");
		syscode.addContent(sysCode);
		header.setContent(syscode);
		return doc;
	}

	/**
	 * @param doc
	 * @param list
	 */
	private void addElementsByList(Document doc, List<Map<String, Object>> list) {
		if(Util.isNotEmpty(list)){
			for(Map<String, Object> map : list){
				Element todoInfo = new Element("todo_info");
				doc.getRootElement().addContent(todoInfo);
				for(String key : map.keySet()){
					Element el = new Element(key.toLowerCase());
					todoInfo.addContent(el);
					if("URL".equals(key)){
						String url = preFix +"/html/index.html#"+(String)map.get(key);
						el.addContent(url);
						//添加手机端url
						Element me = new Element("mobileUrl");
						me.addContent(url);
						todoInfo.addContent(me);
					}else{
						el.addContent((String)map.get(key));
					}
				}
			}
		}
	}
	
	

	
	
}
