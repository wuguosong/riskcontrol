/**
 * 
 */
package common;

/**
 * @Description: TODO
 * @Author zhangkewei
 * @Date 2016年8月10日 下午6:58:50  
 */
public interface IFndService {
	
	PageAssistant list(String json);
	
	void insert(String json);
	
	void update(String json);
	
	void delete(String json);
}
