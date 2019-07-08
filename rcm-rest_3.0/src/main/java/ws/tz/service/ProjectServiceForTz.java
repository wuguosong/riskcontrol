package ws.tz.service;


import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * 投资系统接口
 * @author wufucan
 *
 */
@WebService
public interface ProjectServiceForTz {
	/**
	 * 推送预评审数据
	 * @param json
	 * @return
	 */
	String createPre(@WebParam(name = "json") String json);
	/**
	 * 推送正式评审数据
	 * @param json
	 * @return
	 */
	String createPfr(@WebParam(name = "json") String json);
	/**
	 * 删除预评审
	 * @param json
	 * 根据业务id来判断是否删除
	 * @return
	 */
	String deletePre(@WebParam(name = "json") String json);
	/**
	 * 删除正式评审
	 * @param json
	 * 根据业务id来判断是否删除
	 * @return
	 */
	String deletePfr(@WebParam(name = "json") String json);


	/**
	 * 预评审投标结果反馈
	 * createPreFeedBack
	 * @return
	 * */
	String createPreFeedBack(@WebParam(name = "json") String json);
	
}
