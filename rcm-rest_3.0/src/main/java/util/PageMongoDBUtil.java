package util;

import java.util.List;
import java.util.Map;

import javax.print.Doc;

import org.bson.Document;

public class PageMongoDBUtil {
	public String totalItem;  //总条数
	
	public String pageSize;  //每页显示条数
	
	public String page; //当前页
	
	public String lastId; //当前页最后一个ID
	
	public List<Document> list;    //结果集

	public Map<String,Object> map;
	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getTotalItem() {
		return totalItem;
	}

	public void setTotalItem(String totalItem) {
		this.totalItem = totalItem;
	}

	public List<Document> getList() {
		return list;
	}

	public void setList(List<Document> list) {
		this.list = list;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public PageMongoDBUtil() {
		super();
	}

	public String getLastId() {
		return lastId;
	}

	public void setLastId(String lastId) {
		this.lastId = lastId;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	/*public PageMongoDBUtil(String totalItem, String pageSize, String page, String lastId,List<Document> list) {
		super();
		this.totalItem = totalItem;
		this.pageSize = pageSize;
		this.page = page;
		this.lastId = lastId;
		this.list = list;
	}*/
	public PageMongoDBUtil(String totalItem, String pageSize, String page, Map<String ,Object> map,List<Document> list) {
		super();
		this.totalItem = totalItem;
		this.pageSize = pageSize;
		this.page = page;
		this.map = map;
		this.list = list;
	}
	@Override
	public String toString() {
		return "PageMongoDBUtil [totalItem=" + totalItem + ", pageSize=" + pageSize + ", page=" + page + ", lastId="
				+ lastId + ", list=" + list + "]";
	}
	
	
}
