package util;

import java.util.List;
import java.util.Map;

import org.bson.Document;

public class PageUtil {
	public String itemsTotal;  //总条数
	
	public String pageSize;  //每页显示条数
	
	public String currentPage; //当前页
	
	public List<Map> list;    //结果集

	public String getItemsTotal() {
		return itemsTotal;
	}

	public void setItemsTotal(String itemsTotal) {
		this.itemsTotal = itemsTotal;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public List<Map> getList() {
		return list;
	}

	public void setList(List<Map> list) {
		this.list = list;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	public PageUtil() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PageUtil(String itemsTotal, String pageSize, String currentPage, List<Map> list) {
		super();
		this.itemsTotal = itemsTotal;
		this.pageSize = pageSize;
		this.currentPage = currentPage;
		this.list = list;
	}

	@Override
	public String toString() {
		Document doc = new Document();
		doc.put("itemsTotal", this.itemsTotal);
		doc.put("pageSize", this.pageSize);
		doc.put("currentPage", this.currentPage);
		doc.put("list", this.list);
		return doc.toJson();
	}
	
	
}
