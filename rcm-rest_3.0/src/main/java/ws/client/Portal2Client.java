/**
 * 
 */
package ws.client;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.PropertiesUtil;
import util.Util;
import ws.client.portal.IUpToDoService;
import ws.client.portal.UpToDoServiceImplService;
import ws.client.portal.dto.PortalClientModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yk.common.SpringUtil;
import com.yk.rcm.project.service.IWsCallService;

/**
 * @author wufucan
 *
 */
public class Portal2Client implements Runnable, IWebClient {
	private static Logger logger = LoggerFactory.getLogger(Portal2Client.class);
	
	private PortalClientModel model;
	
	public Portal2Client() {
	}
	
	public Portal2Client(PortalClientModel model) {
		this.model = model;
	}

	@Override
	public boolean resend(String json) {
		Gson gs = new Gson();
		PortalClientModel model = gs.fromJson(json, new TypeToken<PortalClientModel>(){}.getType());
		String message = this.formatMessage(model);
		try {
			UpToDoServiceImplService service = new UpToDoServiceImplService();
			IUpToDoService port = service.getTodoPort();
			String receive  = port.todoList(message);
			String code = getResultCode(receive);
			if("1".equals(code)){
				return false;
			}
		} catch (Exception e) {
			logger.error(Util.parseException(e));
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		String receive = "";
		String message = this.formatMessage(model);
		try {
			UpToDoServiceImplService service = new UpToDoServiceImplService();
			IUpToDoService port = service.getTodoPort();
			receive  = port.todoList(message);
		} catch (Throwable e) {
			logger.error(Util.parseException(e));
			receive = Util.parseException(e);
			success = false;
		}
		String code = getResultCode(receive);
		if("1".equals(code)){
			//接受失败
			success = false;
		}
		if(receive!=null && receive.length()>2000){
			receive = receive.substring(0,2000);
		}
		data.put("receive", receive);
		data.put("content", message);
		data.put("success", success);
		data.put("type", "PTTS");
		IWsCallService wsCallService = (IWsCallService) SpringUtil.getBean("wsCallService");
		wsCallService.save(data);
	}


	private String formatMessage(PortalClientModel model) {
		Document doc = this.createBaseDoc();
		XMLOutputter XMLOut = new XMLOutputter();  
		return XMLOut.outputString(doc);
	}
	
	/**
	 * 创建带有header的xml基本模板
	 * @return
	 */
	private Document createBaseDoc(){
		Document doc = new Document();
		Element root = new Element("todo_list");
		doc.setRootElement(root);
		Element header = new Element("header");
		root.setContent(header);
		Element syscode = new Element("syscode");
		syscode.addContent(PropertiesUtil.getProperty("ws.sysCode"));
		header.setContent(syscode);
		
		Element todoInfo = new Element("todo_info");
		
		Element me = new Element("uniid");
		me.addContent(model.getUniid());
		todoInfo.addContent(me);
		
		me = new Element("title");
		me.addContent(model.getTitle());
		todoInfo.addContent(me);
		
		me = new Element("sender");
		me.addContent(model.getSender());
		todoInfo.addContent(me);
		
		me = new Element("depart");
		me.addContent(model.getDepart());
		todoInfo.addContent(me);
		
		me = new Element("url");
		String url = PropertiesUtil.getProperty("domain.allow") + "/html/index.html#"+model.getUrl();
		me.addContent(url);
		todoInfo.addContent(me);
		
		me = new Element("mobileUrl");
		me.addContent(url);
		todoInfo.addContent(me);
		
		me = new Element("agree_url");
		me.addContent(model.getAgree_url());
		todoInfo.addContent(me);
		
		me = new Element("deny_url");
		me.addContent(model.getDeny_url());
		todoInfo.addContent(me);
		
		me = new Element("owner");
		me.addContent(model.getOwner());
		todoInfo.addContent(me);
		
		me = new Element("created");
		me.addContent(Util.format(model.getCreated()));
		todoInfo.addContent(me);
		
		me = new Element("type");
		me.addContent(model.getType());
		todoInfo.addContent(me);
		
		me = new Element("status");
		me.addContent(model.getStatus());
		todoInfo.addContent(me);
		
		me = new Element("record_status");
		me.addContent(model.getRecord_status());
		todoInfo.addContent(me);
		
		me = new Element("priority");
		me.addContent(model.getPriority());
		todoInfo.addContent(me);
		
		doc.getRootElement().addContent(todoInfo);
		return doc;
	}
	
	/**
	 * 在result中获取接受结果状态
	 */
	private String getResultCode(String result){
		
		java.io.Reader in = new StringReader(result);     
        Document docu = null;
        Element child = null;
		try {
			docu = (new SAXBuilder()).build(in);
			Element rootElement = docu.getRootElement();
			child = rootElement.getChild("code");
			return child.getText();
		} catch (Exception e) {
			logger.error(Util.parseException(e));
		}
		return "1";
	}
	
	public PortalClientModel getModel() {
		return model;
	}


	public void setModel(PortalClientModel model) {
		this.model = model;
	}

}
