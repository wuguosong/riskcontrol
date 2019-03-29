package com.yk.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import com.yk.exception.rcm.PageException;
import com.yk.util.ReflectUtil;

import common.PageAssistant;

/**
 * 分页处理的plugin
 * @author 80845530
 *
 */
@Intercepts({
	@Signature(type=StatementHandler.class, method="prepare", args={Connection.class})
})
public class PagePlugin implements Interceptor {
	private String dialect = "oracle";
	
	@SuppressWarnings("unchecked")
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if(!(invocation.getTarget() instanceof RoutingStatementHandler)){
			return invocation.proceed();
		}
        RoutingStatementHandler statementHandler = (RoutingStatementHandler)invocation.getTarget();    
        StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(statementHandler, "delegate");  
        MappedStatement mappedStatement = (MappedStatement)ReflectUtil.getFieldValue(delegate, "mappedStatement");
        BoundSql boundSql = delegate.getBoundSql();  
        Object parameter = boundSql.getParameterObject();
        if(parameter == null ||!(parameter instanceof Map)||!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())){
        	String sqlId = mappedStatement.getId();
        	String sql = boundSql.getSql();
            sql = sql.replaceAll("\n", "")
            		.replaceAll("\t", "");
            System.out.println("[sql:"+sqlId+"]:"+sql);
            System.out.println("[parameters]:"+parameter);
        	return invocation.proceed();
        }
        Map<String, Object> map = (Map<String, Object>)parameter;
        boolean isFindPage = false;
    	for(Map.Entry<String, Object> entry : map.entrySet()){
    		if(isFindPage){
    			break;
    		}
    		//循环遍历，如果有参数是PageAssistant类型，说明需要分页处理
    		Object param = entry.getValue();
    		if(!(param instanceof PageAssistant)){
    			continue;
    		}
    		isFindPage = true;
    		PageAssistant page = (PageAssistant)param;
    		Connection connection = (Connection)invocation.getArgs()[0];    
            //获取当前要执行的Sql语句，也就是我们直接在Mapper映射语句中写的Sql语句    
            String sql = boundSql.getSql();    
            //给当前的page参数对象设置总记录数    
            this.setTotalCount(page, mappedStatement, connection, map);    
            //获取分页Sql语句    
            String pageSql = this.getPageSql(page, sql);    
            //利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句    
            ReflectUtil.setFieldValue(boundSql, "sql", pageSql);
    	}
    	String sqlId = mappedStatement.getId();
    	String sql = boundSql.getSql();
        sql = sql.replaceAll("\n", "")
        		.replaceAll("\t", "");
        System.out.println("[sql:"+sqlId+"]:"+sql);
        System.out.println("[parameters]:"+parameter);
        return invocation.proceed();    
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {    
            return Plugin.wrap(target, this);    
        } else {    
            return target;    
        }   
	}

	@Override
	public void setProperties(Properties properties) {
	}
	
	private void setTotalCount(PageAssistant page,
			MappedStatement mappedStatement, Connection connection, Map<String, Object> parameterObj) {
		// 获取对应的BoundSql，这个BoundSql其实跟我们利用StatementHandler获取到的BoundSql是同一个对象。
		// delegate里面的boundSql也是通过mappedStatement.getBoundSql(paramObj)方法获取到的。
		BoundSql boundSql = mappedStatement.getBoundSql(parameterObj);
		// 获取到我们自己写在Mapper映射语句中对应的Sql语句
		String sql = boundSql.getSql();
		// 通过查询Sql语句获取到对应的计算总记录数的sql语句
		String countSql = this.getCountSql(sql);
		// 通过BoundSql获取对应的参数映射
		List<ParameterMapping> parameterMappings = boundSql
				.getParameterMappings();
		// 利用Configuration、查询记录数的Sql语句countSql、参数映射关系parameterMappings和参数对象page建立查询记录数对应的BoundSql对象。
		BoundSql countBoundSql = new BoundSql(
				mappedStatement.getConfiguration(), countSql,
				parameterMappings, parameterObj);
		// 通过mappedStatement、参数对象page和BoundSql对象countBoundSql建立一个用于设定参数的ParameterHandler对象
		ParameterHandler parameterHandler = new DefaultParameterHandler(
				mappedStatement, parameterObj, countBoundSql);
		// 通过connection建立一个countSql对应的PreparedStatement对象。
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = connection.prepareStatement(countSql);
			// 通过parameterHandler给PreparedStatement对象设置参数
			parameterHandler.setParameters(pstmt);
			// 之后就是执行获取总记录数的Sql语句和获取结果了。
			rs = pstmt.executeQuery();
			if (rs.next()) {
				int totalRecord = rs.getInt(1);
				// 给当前的参数page对象设置总记录数
				page.setTotalItems(totalRecord);
			}
		} catch (SQLException e) {
			throw new PageException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				throw new PageException(e);
			}
		}
	}
	/**  
     * 根据原Sql语句获取对应的查询总记录数的Sql语句  
     * @param sql  
     * @return  
     */    
    private String getCountSql(String sql) {    
       return "select count(*) from (" + sql+")";    
    }   

    private String getPageSql(PageAssistant page, String sql) {    
        if ("mysql".equalsIgnoreCase(dialect)) {    
//            return getMysqlPageSql(page, sqlBuffer);    
        } else if ("oracle".equalsIgnoreCase(dialect)) {    
            return getOraclePageSql(page, sql);    
        }
        return null;
     }  
    
    private String getOraclePageSql(PageAssistant page, String preSql){
    	StringBuffer pageSql = new StringBuffer("select * from (select g_.*,rownum rn from(");
		pageSql.append(preSql)
			.append(")g_)t_ where t_.rn>")
			.append(page.getOffsetRow())
			.append(" and t_.rn<=" )
			.append(page.getOffsetRow()+page.getPageSize());
		return pageSql.toString();
    }

}
