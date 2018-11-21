package report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.bson.Document;

/**
 * use POI to operate WORD
 */
class MSWord {

	private XWPFDocument document;
	private Bookmarks    bookmarks = null;
	private Logger 		 logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	/**
	 * read the file in templatePath as a template
	 * @param templatePath ../xxx.doc or .docx
	 */
	public void setTemplate(String templatePath) {
		try {
			this.document = new XWPFDocument(
			         POIXMLDocument.openPackage(templatePath));
			
			bookmarks = new Bookmarks(document);
		} catch (IOException e) {
			logger.info("The file '" + templatePath + "' is not exist.");
			e.printStackTrace();
		}		
	}	
	
	/**
	 * replace corresponding string at header
	 * @param projectName
	 * @param createDate
	 */
	public void addHeader(String projectName, String createDate){
		List<XWPFHeader> headers = document.getHeaderList();
		for(XWPFHeader header : headers){
			List<XWPFParagraph> paragraphs = header.getParagraphs();
			for(XWPFParagraph paragraph : paragraphs){
				if(paragraph.getText().contains("projectName") 
						|| paragraph.getText().contains("createDate")){
					List<XWPFRun> runs = paragraph.getRuns();
					for(XWPFRun run : runs){
						for(int i = 0; i < run.getCTR().sizeOfTArray(); i++){
			        		if(run.getText(i).contains("projectName")){
			        			String replaceBy = run.getText(i).replaceAll("projectName", projectName);
			        			run.setText(replaceBy, i);
			        		}else if(run.getText(i).contains("createDate")){
			        			String replaceBy = run.getText(i).replaceAll("createDate", createDate);
			        			run.setText(replaceBy, i);
			        		}
			        	}
					}
				}
			}
		}
	}
	
	
	/**
	 * insert a set of strings before corresponding bookmark
	 * @param indicator Map<bookmarkName, content>
	 */
	public void  insertBeforeBookmark(Map<String,String> indicator) {
		Iterator<String> bookMarkIter = bookmarks.getNameIterator();
		while (bookMarkIter.hasNext()) {
			String bookMarkName = bookMarkIter.next();
			Bookmark bookMark = bookmarks.getBookmark(bookMarkName);

			if (indicator.get(bookMarkName)!=null) {
				bookMark.insertTextAtBookMark(indicator.get(bookMarkName), Bookmark.INSERT_BEFORE);
			}
		}
	}
	
	/**
	 * insert a set of strings before corresponding bookmark
	 * @param bookmarkNames
	 * @param data
	 */
	public void  insertBeforeBookmark(String[] bookmarkNames, Document data) {
		for(String bookmarkName:bookmarkNames){
			Bookmark bookMark = bookmarks.getBookmark(bookmarkName);
			
			if(data.containsKey(bookmarkName)){
				bookMark.insertTextAtBookMark(data.getString(bookmarkName), Bookmark.INSERT_BEFORE);
			}
		}
	}
	
	/**
	 * @param marks marks[0] must be the bookmarkName of the table
	 * @param data List<Document columnData>
	 * @param columns total
	 * @param titlecolumns
	 */
	public void fillTableByColumn(String[] marks, List<Document> data, int columns, int titlecolumns) {
		String bookmarkName = marks[0];
		Bookmark bookmark = bookmarks.getBookmark(bookmarkName);
		XWPFTable table = bookmark.getContainerTable();
		
		if(data == null || data.size() == 0){
			this.replaceTableTexts(marks, null);
			return;
		}
		
		this.replaceTableTexts(marks, data.get(0));
		
		List<XWPFTableRow> rows = table.getRows();
		for(int i = 1; i < data.size(); i++){
			Document columnData = data.get(i);
			for(int j = 0; j < titlecolumns; j++){
				XWPFTableRow row = rows.get(j);
				
				XWPFTableCell oldCell = row.getTableCells().get(row.getTableCells().size() - 1);
				XWPFTableCell newCell = row.addNewTableCell();
				
				newCell.getCTTc().setTcPr(oldCell.getCTTc().getTcPr());
				newCell.getParagraphs().get(0).getCTP().setPPr(oldCell.getParagraphs().get(0).getCTP().getPPr());
				newCell.setText(columnData.getString(marks[j*2+1]));
			}
			for(int j = titlecolumns; j < rows.size();j++){
				XWPFTableRow row = rows.get(j);
				for(int k = 0; k < columns; k++){
					XWPFTableCell oldCell = row.getTableCells().get(row.getTableCells().size() - columns);
					XWPFTableCell newCell = row.addNewTableCell();
					
					newCell.getCTTc().setTcPr(oldCell.getCTTc().getTcPr());
					newCell.getParagraphs().get(0).getCTP().setPPr(oldCell.getParagraphs().get(0).getCTP().getPPr());
					newCell.setText(columnData.getString(marks[j*columns + 1 + k]));
				}
			}
		}
	}
	
	/**
	 * use when Table template has more then one row needed to be replaced
	 * @param marks 
	 * 		marks[0] must be the bookmarkName of the table
	 * 		marks[1] must be the rowNumber needed to be replaced 
	 * @param data List<Document rowData>
	 * @param templateRowCount count(row) of this data part
	 */
	public void fillTableRows(String[] marks, List<Document> data, int templateRowCount){
		String bookmarkName = marks[0];
		int templateRowNum = Integer.valueOf(marks[1]);
		int cellSize = (marks.length - 2)/templateRowCount;
		List<String[]> content = new ArrayList<String[]>();
		
		if(data == null){
			fillTableRows(bookmarkName, templateRowNum, templateRowCount, content);
			return;
		}
		
		for(Document rowData : data){
			for(int i = 0; i < templateRowCount; i++){
				String[] rowContent = new String[cellSize];
				
				for(int j = 0; j < cellSize; j++){
					String mark = marks[j+2+cellSize*i];
					if(rowData.containsKey(mark)){
						rowContent[j] = rowData.getString(mark);
					}else{
						rowContent[j] = "";
					}
				}
				content.add(rowContent);
			}
		}
		
		fillTableRows(bookmarkName, templateRowNum, templateRowCount, content);
	}

	private void fillTableRows(String bookMarkName, int templateRowNum, int templateRowCount, List<String[]> content){
		Bookmark bookMark = bookmarks.getBookmark(bookMarkName);
		XWPFTable table = bookMark.getContainerTable();
		
		int pos = templateRowNum + templateRowCount;
		for(int i = 0; i < content.size(); i++){
			XWPFTableRow newRow = table.insertNewTableRow(pos);
			this.replaceTableRowText(table.getRow(templateRowNum + i%templateRowCount), newRow, content.get(i));
			pos++;
		}
		
		for(int i = 0; i < templateRowCount; i++){
			table.removeRow(templateRowNum);
		}
	}
	
	/**
	 * @param marks 
	 * 		marks[0] must be the bookmarkName of the table
	 * 		marks[1] must be the rowNumber needed to be replaced 
	 * @param data List<Document rowData>
	 * @param haveUnit
	 */
	public void fillTableByRow(String[] marks, List<Document> data, boolean haveUnit) {
		String bookmarkName = marks[0];
		int templateRowNum = Integer.valueOf(marks[1]);
		int cellSize = marks.length - 2;
		if(haveUnit){
			cellSize = cellSize/2;
		}
		List<String[]> content = new ArrayList<String[]>();
		
		if(data == null){
			fillTable(bookmarkName, templateRowNum, content);
			return;
		}
		
		for(Document rowData : data){
			String[] rowContent = new String[cellSize];
			for(int i = 0; i < cellSize; i++){
				String mark = marks[i+2];
				if(rowData.containsKey(mark)){
					rowContent[i] = rowData.getString(mark);
					if(haveUnit){
						rowContent[i] = rowContent[i] + marks[i+2+cellSize];
					}
				}
				else{
					rowContent[i] = "";
				}
			}
			content.add(rowContent);
		}
		
		fillTable(bookmarkName, templateRowNum, content);
	}
	
	private void fillTable(String bookMarkName, int templateRowNum, List<String[]> content){
		Bookmark bookMark = bookmarks.getBookmark(bookMarkName);
		XWPFTable table = bookMark.getContainerTable();
		XWPFTableRow row = table.getRow(templateRowNum);
		int pos = templateRowNum+1;
		for(int i = 0; i < content.size(); i++){
			XWPFTableRow newRow = table.insertNewTableRow(pos);
			this.replaceTableRowText(row, newRow, content.get(i));
			pos++;
		}
		table.removeRow(templateRowNum);
	}
	
	private void replaceTableRowText(XWPFTableRow sourceRow,XWPFTableRow targetRow,String[] content)
	{
		//复制行属性
		targetRow.getCtRow().setTrPr(sourceRow.getCtRow().getTrPr());
		List<XWPFTableCell> cellList = sourceRow.getTableCells();
		if(null==cellList){
			return ;
		}
		//添加列、复制列以及列中段落属性
		XWPFTableCell targetCell = null;
		for(int i = 0; i < cellList.size(); i++){
			XWPFTableCell sourceCell = cellList.get(i);
			targetCell = targetRow.addNewTableCell();
			//列属性
			targetCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());
			//段落属性
			targetCell.getParagraphs().get(0).getCTP().setPPr(sourceCell.getParagraphs().get(0).getCTP().getPPr());
			targetCell.setText(content[i]);
		}
	}
	
	
	/**
	 * @param bookmarkName of the table
	 * @param markMap Map<mark,refill>
	 */
	public void replaceTableTexts(String bookmarkName, Map<String,String> markMap) {
		
		Bookmark bookMark = bookmarks.getBookmark(bookmarkName);
		XWPFTable table = bookMark.getContainerTable();
		
		if(table != null){			
			int rcount = table.getNumberOfRows();
			for(int i = 0 ;i < rcount; i++){
				XWPFTableRow row = table.getRow(i);
				
				List<XWPFTableCell> cells = row.getTableCells();
				for(XWPFTableCell cell : cells){
					List<XWPFParagraph> paragraphs = cell.getParagraphs();
					for(XWPFParagraph paragraph : paragraphs){
						for(Entry<String,String> e : markMap.entrySet()){
							if(paragraph.getText().contains(e.getKey())){
								logger.info("paragraph: "+paragraph.getText()+" VS e: "+e.getKey());
								cell.replaceParagraphText(paragraphs.indexOf(paragraph), e);
							}
						}
					}
					
				}
			}
		}
	}
	
	/**
	 * @param marks marks[0] must be the bookmarkName of the table
	 * @param data <mark,refill>
	 */
	public void replaceTableTexts(String [] marks, Document data){
		String bookmarkName = marks[0];
		Map<String,String> markMap = new HashMap<String,String>();
		
		for(String mark:marks){
			if(data == null || !data.containsKey(mark) || data.getString(mark)==null){
				markMap.put(mark, "");
			}else{
				markMap.put(mark, data.getString(mark));
			}
		}
		replaceTableTexts(bookmarkName, markMap);
	}

	/**
	 * add doc into this Document
	 * @param doc 
	 * @param bookMarkName The bookMark should be in a paragraph.
	 * @return the document finished insert operation
	 */
	public XWPFDocument addProfile (XWPFDocument doc, String bookMarkName, int[] bookmarkCursor){
		List<IBodyElement> partBodys = doc.getBodyElements();
		Bookmark bookMark = bookmarks.getBookmark(bookMarkName);
		XmlCursor cursor;
		
		int paragraphPos = 0;
		int tablePos = 0;
		int pCursorPos = bookmarkCursor[0];
		int tCursorPos = bookmarkCursor[1];
		for(IBodyElement body : partBodys){
			if(body.getElementType().equals(BodyElementType.PARAGRAPH)){
				cursor = bookMark.getContainerParagraph().getCTP().newCursor();
				XWPFParagraph content = body.getBody().getParagraphs().get(paragraphPos);
				XWPFParagraph newP = this.document.insertNewParagraph(cursor);
				
				if(pCursorPos == -1){
					int pos = this.document.getPosOfParagraph(newP);
					pCursorPos = this.document.getParagraphPos(pos);
				}else{
					pCursorPos++;
				}
				this.document.setParagraph(content, pCursorPos);
				paragraphPos++;
			}else if(body.getElementType().equals(BodyElementType.TABLE)){
				cursor = bookMark.getContainerParagraph().getCTP().newCursor();
				XWPFTable content = body.getBody().getTables().get(tablePos);
				XWPFTable newT = this.document.insertNewTbl(cursor);
				
				if(tCursorPos == -1){
					int pos = this.document.getPosOfTable(newT);
					tCursorPos = this.document.getTablePos(pos);
				}else{
					tCursorPos++;
				}
				this.document.setTable(tCursorPos, content);
				tablePos++;
			}else{//XXX should be method for insert Type "CONTENTCONTROL"
				logger.info("CONTENTCONTROL: " + body.toString());
			}
		}
		
		bookmarkCursor[0] = pCursorPos;
		bookmarkCursor[1] = tCursorPos;
		return this.document;
	}
	
	public boolean removeTableRowByBookmark(String bookmarkName){
		Bookmark bookmark = bookmarks.getBookmark(bookmarkName);
		XWPFTable table = bookmark.getContainerTable();
		XWPFTableRow row = bookmark.getContainerTableRow();
		
		int pos = 0;
		for(; pos < table.getNumberOfRows(); pos++){
			if(table.getRow(pos).equals(row)){
				break;
			}
		}
		return table.removeRow(pos);
	}
	
	public boolean removeParagraphByBookmark(String bookmarkName){
		Bookmark bookmark = bookmarks.getBookmark(bookmarkName);
		XWPFParagraph paragraph = bookmark.getContainerParagraph();
		int pos = document.getPosOfParagraph(paragraph);
		return document.removeBodyElement(pos);
	}
	
	/**
	 * @param bookmarkName The bookMark should be in a table.
	 */
	protected void setMinusToRed(String bookmarkName){
		Bookmark bookmark = bookmarks.getBookmark(bookmarkName);
		XWPFTable table = bookmark.getContainerTable();
		
		List<XWPFTableRow> rows = table.getRows();
		for(XWPFTableRow row : rows){
			List<XWPFTableCell> cells = row.getTableCells();
			for(XWPFTableCell cell : cells){
				if(cell.getText().contains("-")){
					List<XWPFParagraph> paragraphs = cell.getParagraphs();
					for(XWPFParagraph paragraph : paragraphs){
						List<XWPFRun> runs = paragraph.getRuns();
						for(XWPFRun run : runs){
							run.setColor("FF0000");
						}
					}
				}
			}
		}
	}
	
	public void saveAs(String outPutPath) {
	    File newFile = new File(outPutPath);
		FileOutputStream fos = null;
		if(!newFile.getParentFile().exists()){
			newFile.getParentFile().mkdirs();
		}
		try {
			fos = new FileOutputStream(newFile);
		} catch (FileNotFoundException e) {
			logger.info("The file '" + outPutPath + "' is not exist.");
			e.printStackTrace();
		}
		try {
			this.document.write(fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			logger.info("CANNOT write into '" + outPutPath + "'.");
			e.printStackTrace();
		}		
	}	

	protected XWPFDocument getDocument(){
		return this.document;
	}
	
	/**
	 * @param bookmarkName The bookMark should be in a table.
	 * @return rowNum
	 */
	protected int getRowNumOfBookmark(String bookmarkName){
		Bookmark bookmark = bookmarks.getBookmark(bookmarkName);
		return bookmark.getRowNum();
	}
}
