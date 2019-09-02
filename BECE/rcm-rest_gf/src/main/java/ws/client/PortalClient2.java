/**
 * 
 */
package ws.client;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import util.PropertiesUtil;
import util.Util;
import ws.client.portal.IUpToDoService;
import ws.client.portal.UpToDoServiceImplService;
import ws.client.portal.dto.PortalClientModel;

import com.yk.common.SpringUtil;
import com.yk.rcm.project.service.IWsCallService;

/**
 * @Description: 推送待办到门户系统
 * @Author yaphet
 * @Date 2017年3月28日 下午13:51:46  
 */
public class PortalClient2 implements Runnable, IWebClient {
	private static Logger logger = Logger.getLogger(PortalClient2.class);
	private static final String sysCode = PropertiesUtil.getProperty("ws.sysCode");
	private static final String preFix = PropertiesUtil.getProperty("domain.allow");
	private static final String wsEanble = PropertiesUtil.getProperty("ws.enable");
	
	private List<PortalClientModel> list = null;
	
	public PortalClient2(List<PortalClientModel> list){
		this.list = list;
	}
	
	@Override
	public boolean resend(String message){
		String ret = "";
		UpToDoServiceImplService service = new UpToDoServiceImplService();
		IUpToDoService port = service.getTodoPort();
		ret = port.todoList(message);
		
		String code = getResultCode(ret);
		if("1".equals(code)){
			//接受失败
			return false;
		}
		return true;
	}
	
	@Override
	public void run() {
		if("false".equals(wsEanble)) return;
		String message="",receive="";
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			message = this.getMessage();
			UpToDoServiceImplService service = new UpToDoServiceImplService();
			IUpToDoService port = service.getTodoPort();
			receive = port.todoList(message);
		} catch (Exception e) {
			receive = Util.parseException(e);
			logger.error(receive);
			success = false;
		}
		String code = getResultCode(receive);
		if("1".equals(code)){
			//接受失败
			success = false;
		}
		data.put("receive", receive);
		data.put("content", message);
		data.put("success", success);
		data.put("type", "PTTS");
		IWsCallService wsCallService = (IWsCallService) SpringUtil.getBean("wsCallService");
		wsCallService.save(data);
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
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return child.getText();
	}
	

	
	/**

	 * 获取报文信息转换成xml
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private String getMessage() throws IllegalArgumentException, IllegalAccessException {
		Document doc = this.createBaseDoc();
		this.addElementsByList(doc, list);
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
		syscode.addContent("");
		syscode.addContent(sysCode);
		header.setContent(syscode);
		return doc;
	}

	/**
	 * 向模板中添加子元素节点
	 * @param doc
	 * @param list
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void addElementsByList(Document doc, List<PortalClientModel> list) throws IllegalArgumentException, IllegalAccessException {
		if(Util.isNotEmpty(list)){
			for(PortalClientModel mod : list){
				Class<? extends PortalClientModel> class1 = mod.getClass();
				Field[] declaredFields = class1.getDeclaredFields();
				
				Element todoInfo = new Element("todo_info");
				doc.getRootElement().addContent(todoInfo);
				
				for (Field field : declaredFields) {
					field.setAccessible(true);
					Element el = new Element(field.getName().toLowerCase());
					todoInfo.addContent(el);
					//need to ask
					if("url".equals(field.getName())){
						String url = preFix + "/html/index.html#/"+mod.getUrl();
						el.addContent(url);
					}else{
						el.addContent((String) field.get(mod));
					}
				}
			}
		}
	}
	
}
