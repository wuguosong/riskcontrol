/**
 * 
 */
package com.yk.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.yk.exception.rcm.DocReportException;

/**
 * 生成world文档
 * @author wufucan
 *
 */
public class DocReportUtil {

	/**
	 * 根据给定的模版生成dorld文档
	 * @param param 数据，值必须是string类型
	 * @param template 模版路径
	 * @param outpath 生成文档存在路径
	 * @return
	 */
	public static void generateWord(Map<String, Object> param,String template, String outpath){
		if(outpath == null || "".equals(outpath)){
			throw new DocReportException("生成word文档失败，输出路径不能为空！");
		}
		if(template == null || "".equals(template)|| !new File(template).exists()){
			throw new DocReportException("生成word文档失败，给定的模版不存在！");
		}
		String outFolder = outpath.substring(0,outpath.lastIndexOf("/"));
		File outf = new File(outFolder);
		if(!outf.exists()){
			outf.mkdirs();
		}
		XWPFDocument doc=null;
        try {
            OPCPackage pack=POIXMLDocument.openPackage(template);
            doc=new XWPFDocument(pack);
            if(param!=null&&param.size()>0){
                //处理段落
                List<XWPFParagraph> paragraphList = doc.getParagraphs();   
                processParagraphs(paragraphList, param, doc); 
                //处理表格
                Iterator<XWPFTable> it = doc.getTablesIterator(); 
                while(it.hasNext()){
                    XWPFTable table = it.next();  
                    List<XWPFTableRow> rows = table.getRows();
                    for (XWPFTableRow row : rows) {
                         List<XWPFTableCell> cells = row.getTableCells();
                         for (XWPFTableCell cell : cells) {
                             List<XWPFParagraph> paragraphListTable =  cell.getParagraphs();
                             processParagraphs(paragraphListTable, param, doc); 
                        }
                    }
                }
            }
            outpath.substring(0,outpath.lastIndexOf("/"));
            FileOutputStream fopts = new FileOutputStream(outpath); 
			doc.write(fopts);  
			fopts.close();
        } catch (IOException e) {
        	throw new DocReportException("生成word文档失败，详情请查看日志！", e);
        }
    }
	
	private static void processParagraphs(List<XWPFParagraph> paragraphList,Map<String, Object> param, XWPFDocument doc){  
        if(paragraphList!=null&&paragraphList.size()>0){
            for (XWPFParagraph paragraph : paragraphList) {
                List<XWPFRun> runs=paragraph.getRuns();
                for (XWPFRun run : runs) {
                    String text=run.getText(0);
                    if(text!=null){
                        boolean isSetText=false;
                        for (Entry<String, Object> entry : param.entrySet()) {
                            String key="${"+entry.getKey()+"}";
                            if(text.indexOf(key)!=-1){
                                isSetText=true;
                                Object value=entry.getValue();
                                if(value == null){
                                	value = "";
                                }
                                text=text.replace(key, value.toString());
                            }
                        }
                        if(isSetText){
                            run.setText(text, 0);
                        }
                    }
                }
            }
        }
    }
	
}
