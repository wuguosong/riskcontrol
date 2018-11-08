package common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @Description: 分页辅助类
 * @Author zhangkewei
 * @Date 2016年8月11日 上午8:02:33  
 */
public class PageAssistant {
	/**查询需要用**/
	private Map<String, Object> paramMap = new HashMap<String, Object>();//页面查询条件
	private Integer offsetRow = 0; //查询起始行
	private Integer pageSize = 10; //每页显示几条
	/**返回需要用**/
	private Integer totalItems = 0;  //总记录数
	private List<?> list;    //结果集
	/**
	 * 排序字符串，例如：orderBy=" create_time desc"
	 */
	private String orderBy;
	
	
	public PageAssistant() {
		super();
	}
	/**
	 * queryJson格式：<br>
	 * {currentPage:xx,pageSize:xx,queryObj:{xx:xx,xx:xx}}
	 */
	@SuppressWarnings("unchecked")
	public PageAssistant(String queryJson) {
		Gson gs = new Gson();
		Map<String, Object> map = gs.fromJson(queryJson, new TypeToken<Map<String, Object>>(){}.getType());
		Object cup = map.get("currentPage");
		Object ps = map.get("pageSize");
		Object itemsPerPage = map.get("itemsPerPage");
		if(ps != null){
			pageSize = Double.valueOf(ps.toString()).intValue();
		}else if(itemsPerPage != null){
			pageSize = Double.valueOf(itemsPerPage.toString()).intValue();
		}
		if(cup != null){
			Integer currentPage  = Double.valueOf(cup.toString()).intValue();
			offsetRow = (currentPage-1)*pageSize;
		}
		paramMap = (Map<String, Object>)map.get("queryObj");
		this.orderBy = (String) map.get("orderBy");
	}
	
	public PageAssistant(String queryJson, String collection){
		
	}
	/**
	 * @return the paramMap
	 */
	public Map<String, Object> getParamMap() {
		return paramMap;
	}
	/**
	 * @param paramMap the paramMap to set
	 */
	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}
	/**
	 * @return the offsetRow
	 */
	public Integer getOffsetRow() {
		return offsetRow;
	}
	/**
	 * @param offsetRow the offsetRow to set
	 */
	public void setOffsetRow(Integer offsetRow) {
		this.offsetRow = offsetRow;
	}
	/**
	 * @return the pageSize
	 */
	public Integer getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	/**
	 * @return the totalItems
	 */
	public Integer getTotalItems() {
		return totalItems;
	}
	/**
	 * @param totalItems the totalItems to set
	 */
	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
	}
	/**
	 * @return the list
	 */
	public List<?> getList() {
		return list;
	}
	/**
	 * @param list the list to set
	 */
	public void setList(List<?> list) {
		this.list = list;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
}
