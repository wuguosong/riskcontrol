package common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.text.Format;
import java.text.SimpleDateFormat;  
  
/*** 
 * @author lsf 
 */  
public class ExportExcel {  
 /*************************************************************************** 
  * @param fileName EXCEL文件名称 
  * @param listTitle EXCEL文件第一行列标题集合 
  * @param listContent EXCEL文件正文数据集合 
  * @return 
 * @throws IOException 
  */  
 public  final static Map<String,String> exportExcel(String titleHeader,String fileName, String[] listTitle,String[] listColumn, List<Object> listContent) throws IOException {  
	  String result="系统提示：Excel文件导出成功！";    
		  //创建HSSFWorkbook对象(excel的文档对象)
		  HSSFWorkbook wb = new HSSFWorkbook();
		  HSSFCellStyle style = wb.createCellStyle();
		//建立新的sheet对象（excel的表单）
		HSSFSheet sheet=wb.createSheet(titleHeader);
		//在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
		HSSFRow row1=sheet.createRow(0);
		//创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
		/** ***************以下是EXCEL第一行列标题********************* */  
		   
		/*HSSFCell cell=row1.createCell(0);
		  //设置单元格内容
		cell.setCellValue(titleHeader);
		//合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
		sheet.addMergedRegion(new CellRangeAddress(0,0,0,3));*/
		//在sheet里创建第二行
		//row1=sheet.createRow(1);  
		for (int i = 0; i < listTitle.length; i++) {
	        // 创建单元格，设置值
	        row1.createCell(i).setCellValue(listTitle[i]);
	      }
		   
		//在sheet里创建第三行
		for (int i = 0; i < listContent.size(); i++) {
			Row rows = sheet.createRow((int) i + 1);
			rows.setHeightInPoints(25);
	        Map list=  (Map) listContent.get(i);
	        for (int j = 0; j < listColumn.length; j++) {
		        // 创建单元格，设置值
	        	if(listColumn[j].equals("WF_STATE")){
	        		 Object WF_STATE=list.get("WF_STATE");
	        		 String state="";
	     	        if(null!=WF_STATE){
	     	        	if(WF_STATE.equals("0")){
	     	        		state="起草中";
	     	        	}else if(WF_STATE.equals("1")){
	     	        		state="审批中";
	     	        	}else if(WF_STATE.equals("2")){
	     	        		state="已结束";
	     	        	}else if(WF_STATE.equals("3")){
	     	        		state="已终止";
	     	        	}
	     	        }
	     	        rows.createCell(j).setCellValue(state);
	        	}else if(listColumn[j].equals("state")){
	        		Object WF_STATE=list.get("state");
	        		 String state=null;
	     	        if(null!=WF_STATE){
	     	        	 if(WF_STATE.equals("1")){
	     	        		state="已通知";
	     	        	 }
	     	        }
	     	        rows.createCell(j).setCellValue(state);
	        	}else{
	        		rows.createCell(j).setCellValue(list.get(listColumn[j])!=null?list.get(listColumn[j]).toString():null);
	        	}
		      }
	      }
		
		Format format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		//String filepath="/app/rcm_file/ExcelREPLACE"+format.format(new Date())+".xlsx";
		String filepath=Constants.UPLOAD_DIR+"Excel"+fileName+".xls";
		FileOutputStream out =new FileOutputStream(filepath);
		wb.write(out); 
		out.close();
		Map<String,String> map=new HashMap<String,String>();
		map.put("fileName", "Excel"+fileName+".xls");
		map.put("filePath", filepath);
		return map;
	}  
}