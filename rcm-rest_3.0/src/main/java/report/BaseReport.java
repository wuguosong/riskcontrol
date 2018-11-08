package report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bson.Document;

import util.DbUtil;

abstract class BaseReport {
	protected Document reportData;
	protected MSWord wordFile = new MSWord();
	protected Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	/**
	 * @param ObjectId
	 * @return Report path
	 */
	abstract public String generateReport(String ObjectId);
	
	protected void connectMongoDB(){
	}
	
	protected void closeMongoDB(){
		DbUtil.close();
	}
	
	protected void replaceTableTexts_haveStableColumn(String[] marks, List<Document> data){
		int rowSize = 0;
		if(data != null){
			rowSize = data.size();
		}
		int cellSize = (marks.length-2)/2;
		if(rowSize == 0){
			wordFile.removeTableRowByBookmark(marks[2]);
			wordFile.removeTableRowByBookmark(marks[cellSize+2]);
		}else if(rowSize == 1){
			replace1stRow(marks, data.get(0));;
			wordFile.removeTableRowByBookmark(marks[cellSize+2]);
		}else{
			replaceFrom2ndRow(marks, data);
		}
	}
	
	private void replaceFrom2ndRow(String[] marks,List<Document> data){
		replace1stRow(marks, data.get(0));
		
		int contentSize = (marks.length-2)/2;
		int cellSize = contentSize + 1;
		String[] newMarks  = new String[cellSize + 2];
		
		newMarks[0] = marks[0];
		newMarks[1] = wordFile.getRowNumOfBookmark(marks[contentSize+2])+"";
		newMarks[2] = "title";
		for(int i = 3; i < newMarks.length; i++){
			newMarks[i] = marks[i-1+contentSize];
		}
		
		List<Document> content = new ArrayList<Document>();
		int bookmarkPos = contentSize+2;
		int endPos = marks.length;
		
		for(int i = 1; i < data.size(); i++){
			Document row = new Document();
			Document rowData = data.get(i);
			row.put(marks[bookmarkPos], String.valueOf(i+1));
			for(int j = bookmarkPos + 1; j < endPos; j++){
				String key = marks[j].substring(0, marks[j].length()-2);
				if(rowData.containsKey(key)){
					row.put(marks[j], rowData.getString(key));
				}else{
					row.put(marks[j], "");
				}
			}
			content.add(row);
		}
		
		wordFile.fillTableByRow(newMarks, content, false);
		
	}
	
	private void replace1stRow(String[] marks, Document data){
		Map<String,String> row = new HashMap<String,String>();
		int bookmarkPos = 2;
		int endPos = (marks.length - 2)/2 + 2;
		row.put(marks[bookmarkPos], "1");
		for(int j = bookmarkPos + 1; j < endPos; j++){
			String key = marks[j].substring(0, marks[j].length()-2);
			if(data.containsKey(key)){
				row.put(marks[j], data.getString(key));
			}else{
				row.put(marks[j], "");
			}
		}
		wordFile.replaceTableTexts(marks[bookmarkPos], row);
	}
	
	protected static void storeObjectName(Document data, String[] keys, String subKey){
		for(String key : keys){
			storeObjectName(data, key, subKey);
		}
	}
	
	protected static void storeObjectName(Document data, String key, String subKey){
		if(data.containsKey(key)
				&& data.get(key)!=null){
			Document subData = (Document)data.get(key);
			data.put(key, subData.get(subKey));
		}else{
			data.put(key, "");
		}
	}
	
	/**
	 * @param data 
	 * @param keys[]: key = List<Document> in mongo
	 * @param subKey
	 * @param separator
	 */
	protected static void storeListName(Document data, String[] keys, String subKey, String separator){
		for(String key : keys){
			storeListName(data, key, subKey, separator);
		}
	}
	
	/**
	 * @param data 
	 * @param key = List<Document> in mongo
	 * @param subKey
	 * @param separator
	 */
	protected static void storeListName(Document data, String key, String subKey, String separator){
		if(data.containsKey(key)){
			@SuppressWarnings("unchecked")
			List<Document> subDatas = (List<Document>)data.get(key);
			String value = "";
			for(Document subData : subDatas){
				if(value.equals("")){
					value = subData.getString(subKey);
				}else{
					value = value +separator+ subData.get(subKey);
				}
			}
			data.put(key, value);
		}else{
			data.put(key, "");
		}
	}
	
	protected static void dateFormate(Document data, String key){
		if(data.containsKey(key)){
			data.put(key, getStandardDateFormate(data.getString(key)));
		}
	}
	
	/**
	 * @param date yyyy-mm-dd
	 * @return yyyy年mm月dd日
	 */
	protected static String getStandardDateFormate(String date){
		int y_m = date.indexOf("-");
		int m_d = date.lastIndexOf("-");
		int end = date.indexOf(" ");
		
		String year = date.substring(0, y_m);
		String month = date.substring(y_m+1, m_d);
		String day;
		if(end < 0){
			day = date.substring(m_d+1);
		}else{
			day = date.substring(m_d+1, end);
		}
		return year + "年" + month + "月" + day + "日";
	}
}
