package rcm;



import common.BaseService;
import common.PageAssistant;

public class BusinessModel extends BaseService{
	public PageAssistant getAll(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "business.selectBusiness");
		return assistant;
	}
}
