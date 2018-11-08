package ctm;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;


import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import util.CrudUtil;
import util.DbUtil;

public class FileList {
	/**
	 * 用户管理对象
	 */
	private static final long serialVersionUID = 1L;
	
	private final  String DocumentName="contract_info";		

	
	public String Create(String json) {

		Document doc=Document.parse(json);
		
       //设置添加时间
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");//可以方便地修改日期格式
		String  createDate= dateFormat.format( now ); 
		doc.append("create_date", createDate);
		
		DbUtil.getColl(DocumentName).insertOne(doc);
		
		return "";
	} 
	
	public String Update(String json){
		
		Bson bson=BsonDocument.parse(json);
    	UpdateResult doc= DbUtil.getColl(DocumentName).updateMany(bson, bson);
		return doc.toString();
	}
	
	public String Delete(String json){
	
		Bson bson=BsonDocument.parse(json);
    	DeleteResult doc= DbUtil.getColl(DocumentName).deleteMany(bson);
		return doc.toString();
	}
	
	public List<Document> GetAll(){
		
		MongoCursor<Document> cursor = DbUtil.getColl(DocumentName).find().iterator();
		List<Document> docList = CrudUtil.convertCursorToList(cursor);
		return docList;
	}
	
    public Document GetByCode(String json){

    	Bson bson=BsonDocument.parse(json);
    	Document doc= DbUtil.getColl(DocumentName).find(bson).first();
		return doc;
	}
    
    public String Login(String json){
    	//查询数据库中用户名及密码
    	
    	
    	//比较用户名
    	
    	Bson bson=BsonDocument.parse(json);
    //	String userCode=bson..get("contract_code").toString();
    	Document doc= DbUtil.getColl(DocumentName).find(bson).first();
    	
    	//比较密码
	
    	
    	//符合返回成功，否则失败
    	
    	return "";
    }

}
