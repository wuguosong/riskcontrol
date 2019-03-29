/**
 * 
 */
package common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import util.DbUtil;

/**
 * @Author zhangkewei
 * @Date 2016年8月11日 上午7:51:32  
 */
public class BaseService {

	/*
	 * 分页查询
	 * 注意：使用该方法的时候myBatis sql中只能使用${}占位，不能使用#{}
	 */
	@SuppressWarnings("rawtypes")
	protected void selectByPage(PageAssistant assistant, String sqlId) {
		/*SqlSession session = DbUtil.openSession();
		//查询list
		RowBounds bounds = new RowBounds(assistant.getOffsetRow(), assistant.getPageSize());
		List<Map> list = session.selectList(sqlId, assistant.getParamMap(), bounds);
		//查询总条数
		MappedStatement statement = session.getConfiguration().getMappedStatement(sqlId);
		BoundSql boundSql = statement.getBoundSql(assistant.getParamMap());
		String countSql = "select count(1) as row_count from (" + boundSql.getSql() + ")";
		Map<String, String> paramMap = new HashMap<String, String>();
		Pattern p = Pattern.compile("\t|\r|\n");
	    Matcher m = p.matcher(countSql);
	    countSql = m.replaceAll("");
		paramMap.put("sql", countSql);
		Integer rowCount = session.selectOne("sysFun.getRowCount", paramMap);
		DbUtil.close();
		assistant.setTotalItems(rowCount);
		assistant.setList(list);*/
		
		SqlSession session = DbUtil.openSession();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", assistant);
		if(assistant.getParamMap()!=null){
			params.putAll(assistant.getParamMap());
		}
		List list = session.selectList(sqlId, params);
		assistant.setList(list);
	}
}
