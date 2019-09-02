package projectPreReview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import common.BaseService;
import common.PageAssistant;
import util.DbUtil;

public class ListNotice extends BaseService{
	public PageAssistant listNotice(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "listNotice.listNotice");
		return assistant;
	}
	public PageAssistant listWarning(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "listNotice.listWarning");
		return assistant;
	}
	public PageAssistant listUnreadNotice(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "listNotice.listUnreadNotice");
		return assistant;
	}
	public PageAssistant listUnreadWarning(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "listNotice.listUnreadWarning");
		return assistant;
	}
	public PageAssistant listWaringConfig(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "taskWarningConf.listWaringConfig");
		return assistant;
	}
	public PageAssistant listFormalWaringConfig(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "taskWarningConf.listFormalWaringConfig");
		return assistant;
	}
public String updateWaringConfig(String json){
		Document bjson = Document.parse(json);
		List<Document> list1 = (List<Document>)bjson.get("nodeList1");
		for(Document d : list1){
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("due_days", d.get("DUE_DAYS"));
			paramMap.put("form_key", d.get("FORM_KEY"));
			paramMap.put("state", d.get("STATE"));
			paramMap.put("id", d.get("ID"));
			Integer o=	DbUtil.openSession().update("taskWarningConf.updateWaringConfig", paramMap);
		}
	    DbUtil.close();
		return "";
	}

}
