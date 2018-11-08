package report;

import org.apache.poi.xwpf.usermodel.XWPFDocument; 
import org.apache.poi.xwpf.usermodel.XWPFParagraph; 
import org.apache.poi.xwpf.usermodel.XWPFTable; 
import org.apache.poi.xwpf.usermodel.XWPFTableCell; 
import org.apache.poi.xwpf.usermodel.XWPFTableRow; 
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark; 
import java.util.HashMap; 
import java.util.Iterator; 
import java.util.List; 
import java.util.Collection; 
import org.w3c.dom.NamedNodeMap; 
import org.w3c.dom.Node; 

/**
 * use POI to operate word bookmarks
 */
class Bookmarks { 
    
    private HashMap<String, Bookmark> _bookmarks = null; 
    
    /** 
     * get All <bookmark>
     * @param document  Word OOXML document instance. 
     */ 
    public Bookmarks(XWPFDocument document) { 

        this._bookmarks = new HashMap<String, Bookmark>(); 
        
        // get from paragraphs
        this.procParaList(document.getParagraphs()); 

        //get from tables
        List<XWPFTable> tableList = document.getTables(); 
        
        for (XWPFTable table : tableList) { 
        	List<XWPFTableRow> rowList = table.getRows(); 
            for (XWPFTableRow row : rowList){ 
            	List<XWPFTableCell> cellList = row.getTableCells(); 
                for (XWPFTableCell cell : cellList) { 
                	this.procParaList(cell);
                } 
            } 
        } 
    } 

    
    /**
     * @param bookmarkName
     * @return   bookmark/null
     */
    public Bookmark getBookmark(String bookmarkName) { 
        Bookmark bookmark = null; 
        if(this._bookmarks.containsKey(bookmarkName)) { 
            bookmark = this._bookmarks.get(bookmarkName); 
        } 
        return   bookmark; 
    } 
    
    public Collection<Bookmark> getBookmarkList() { 
        return(this._bookmarks.values()); 
    } 
    
    /** 
     * get bookmark name iterator
     */
    public Iterator<String> getNameIterator() { 
        return(this._bookmarks.keySet().iterator()); 
    } 
    

    private void procParaList(XWPFTableCell cell){
    	List<XWPFParagraph> paragraphList = cell.getParagraphs();
    	
    	for(XWPFParagraph paragraph : paragraphList){
    		//�õ������еı�ǩ���
        	List<CTBookmark> bookmarkList = paragraph.getCTP().getBookmarkStartList();
        	for (CTBookmark bookmark : bookmarkList ) {
        		this._bookmarks.put(bookmark.getName(),
        				new Bookmark(bookmark, paragraph, cell));
        	}
    	}
    }
    
    @SuppressWarnings("unused")
	private void procParaList(List<XWPFParagraph> paragraphList, XWPFTableRow tableRow) { 

        NamedNodeMap attributes = null; 
        Node colFirstNode = null; 
        Node colLastNode = null; 
        int firstColIndex = 0; 
        int lastColIndex = 0; 
        
        for (XWPFParagraph paragraph : paragraphList) { 
        	List<CTBookmark> bookmarkList = paragraph.getCTP().getBookmarkStartList(); 

            for (CTBookmark bookmark : bookmarkList ) { 
                // With a bookmark in hand, test to see if the bookmarkStart tag 
                // has w:colFirst or w:colLast attributes. If it does, we are 
                // dealing with a bookmarked table cell. This will need to be 
                // handled differnetly - I think by an different concrete class 
                // that implements the Bookmark interface!! 
                attributes = bookmark.getDomNode().getAttributes(); 
                if(attributes != null) { 

                    // Get the colFirst and colLast attributes. If both - for 
                    // now - are found, then we are dealing with a bookmarked 
                    // cell. 
                    colFirstNode = attributes.getNamedItem("w:colFirst"); 
                    colLastNode = attributes.getNamedItem("w:colLast"); 
                    if(colFirstNode != null && colLastNode != null) { 
                    	
                        // Get the index of the cell (or cells later) from them. 
                        // First convefrt the String values both return to primitive 
                        // int value. TO DO, what happens if there is a 
                        // NumberFormatException. 
                        firstColIndex = Integer.parseInt(colFirstNode.getNodeValue()); 
                        lastColIndex = Integer.parseInt(colLastNode.getNodeValue()); 
                        // if the indices are equal, then we are dealing with a# 
                        // cell and can create the bookmark for it. 
                        if(firstColIndex == lastColIndex) { 
                            this._bookmarks.put(bookmark.getName(), 
                                new Bookmark(bookmark, paragraph, 
                                    tableRow.getCell(firstColIndex))); 
                        } 
                        else { 
                            System.out.println("This bookmark " + bookmark.getName() + 
                                    " identifies a number of cells in the " 
                                    + "table. That condition is not handled yet."); 
                        } 
                    } 
                    else { 
                        this._bookmarks.put(bookmark.getName(), 
                        new Bookmark(bookmark, paragraph,tableRow.getCell(1))); 
                    } 
                } 
                else { 
                    this._bookmarks.put(bookmark.getName(), 
                        new Bookmark(bookmark, paragraph,tableRow.getCell(1))); 
                } 
            } 
        } 
    } 
    
    private void procParaList(List<XWPFParagraph> paragraphList) { 

        for (XWPFParagraph paragraph : paragraphList) { 
        	List<CTBookmark>  bookmarkList = paragraph.getCTP().getBookmarkStartList(); 
            for (CTBookmark bookmark : bookmarkList) { 
                this._bookmarks.put(bookmark.getName(), 
                        new Bookmark(bookmark, paragraph)); 
            } 
        } 
    } 
} 