/**
 * 
 */
package com.yk.common;

import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author 80845530
 *
 */
@MappedJdbcTypes(JdbcType.CLOB)//此处如果不使用该注解，在myabtis-config.xml中注册该typehandler的时候需要写明jdbctype="TIMESTAMP"
@MappedTypes(String.class)
public class ClobTypeHandler extends BaseTypeHandler<String> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
			String parameter, JdbcType jdbcType) throws SQLException {
		StringReader reader = new StringReader(parameter);
		ps.setClob(i, reader);
	}

	@Override
	public String getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		String value = "";
		Clob clob = rs.getClob(columnName);
		if (clob != null) {
			int size = (int) clob.length();
			value = clob.getSubString(1, size);
		}
		return value;
	}

	@Override
	public String getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		String value = "";
	    Clob clob = rs.getClob(columnIndex);
	    if (clob != null) {
	      int size = (int) clob.length();
	      value = clob.getSubString(1, size);
	    }
	    return value;
	}

	@Override
	public String getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		String value = "";
		Clob clob = cs.getClob(columnIndex);
		if (clob != null) {
			int size = (int) clob.length();
			value = clob.getSubString(1, size);
		}
		return value;
	}

}
