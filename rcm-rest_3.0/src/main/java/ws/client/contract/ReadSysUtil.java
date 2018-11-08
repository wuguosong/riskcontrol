package ws.client.contract;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bpm.listener.ProjectReviewEndListener;
import util.PropertiesUtil;
import util.Util;

/**
 * offer contract statistics
 * @author CarolineHao
 */
public class ReadSysUtil {
	private static Logger logger = LoggerFactory.getLogger(ProjectReviewEndListener.class);
	private static final String contractType_Investment = "投资";
	private static final String contractType_Others = "其他";
	private Connection dbConn;
	
	public ReadSysUtil(){
		
	}
	
	public void connectDB(){
		try{
			String driverName = PropertiesUtil.getProperty("contactSys.driverName");
			Class.forName(driverName);
			dbConn = DriverManager.getConnection(
					   PropertiesUtil.getProperty("contactSys.dbURL"),
					   PropertiesUtil.getProperty("contactSys.userName"),
					   PropertiesUtil.getProperty("contactSys.password"));
		}catch(Exception e){
			String errorStr = Util.parseException(e);
			logger.error(errorStr);
		}    
	}
	
	public Map<String, Object> getMonthlyStatistics(){
		connectDB();
		Map<String, Object> monthlyStatistics = new HashMap<String, Object>();
		
		monthlyStatistics.put("investment", this.getMonthlyStatistics(contractType_Investment));
		monthlyStatistics.put("others", this.getMonthlyStatistics(contractType_Others));
		
		return monthlyStatistics;
	}
	
	/**
	 * get yearly person detail
	 * @param contractType
	 * @return
	 */
	private List<Map<String, Object>> getMonthlyStatistics(String contractType){
		connectDB();
		String sql = "select a.name,a.followed,b.finished,b.avgTime"
				+ " from (select c.[审批人姓名] as name,count(c.[审批人姓名]) as followed"
				+ " from CMS_RiskControl_ApprovalOfStatistics c"
				+ " where c.[合同分类]=? and c.[审批人编号]!='0001N61000000000195V' and c.[签订日期] is null"
				+ " group by c.[审批人编号],c.[审批人姓名])a"
				+ " full outer join (select c.[审批人姓名] as name,count(c.[签订日期])as finished ,avg(c.[合同审批风控用时]) as avgTime"
				+ " from CMS_RiskControl_ApprovalOfStatistics c"
				+ " where c.[合同分类]=? and c.[审批人编号]!='0001N61000000000195V' "
				+ "	and Datename(year,c.[签订日期])=Datename(year,GETDATE()) "
				+ " group by c.[审批人编号],c.[审批人姓名])b"
				+ " on a.name = b.name";

		ResultSet rs = null;
		List<Map<String, Object>> monthlyStatistics = new ArrayList<Map<String, Object>>();
		try {
			PreparedStatement ps = dbConn.prepareStatement(sql);
			ps.setString(1, contractType);
			ps.setString(2, contractType);
			
			rs = ps.executeQuery();
			ResultSetMetaData md = rs.getMetaData();
	        int columnCount = md.getColumnCount();
			while(rs.next()){
				Map<String, Object> rowData = new HashMap<String, Object>();

	            for (int i = 1; i <= columnCount; i++) {
	                rowData.put(md.getColumnName(i), rs.getObject(i));
	            }

	            monthlyStatistics.add(rowData);
			}
		} catch (SQLException e) {
			String errorStr = Util.parseException(e);
			logger.error(errorStr);
		}
		
		return monthlyStatistics;
	}
	
	public Map<String, int[]> getYearlyStatistics(){
		connectDB();
		Map<String, int[]> yearlyStatistics = new HashMap<String, int[]>();
		
		yearlyStatistics.put("investment", this.getYearlyStatistics(contractType_Investment));
		yearlyStatistics.put("others", this.getYearlyStatistics(contractType_Others));
		
		return yearlyStatistics;
	}
	
	private int[] getYearlyStatistics(String contractType){
		String sql = "select Datename(month,c.[签订日期])as month,count(c.[合同编号])as num"
				+ " from CMS_RiskControl_ApprovalOfStatistics c"
				+ " where Datename(year,c.[签订日期])=Datename(year,GETDATE()) "
				+ " and c.[合同分类]=?"
				+ " GROUP BY Datename(month,c.[签订日期])";
		ResultSet rs = null;
		int[] yearlyStatistics = new int[12];
		PreparedStatement ps;
		try {
			ps = dbConn.prepareStatement(sql);
			ps.setString(1, contractType);
			
			rs = ps.executeQuery();
			while(rs.next()){
				int month = rs.getInt("month");
				yearlyStatistics[month-1] = rs.getInt("num");
			}
		} catch (SQLException e) {
			String errorStr = Util.parseException(e);
			logger.error(errorStr);
		}
		
		return yearlyStatistics;
	}
	
}
