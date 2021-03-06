package fnd;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;



import util.CrudUtil;
import util.DbUtil;



public class User {
	
	
	/**
	 * 用户管理对象
	 */
	private static final long serialVersionUID = 1L;
	
	private final  String DocumentName="sys_user";		

	
	public List<Map> Insert(String json){
		
		//Map paramMap = new HashMap();
		Document doc=Document.parse(json);
		//DbUtil.openSession().insert("demo.insertUser", doc);
		
		List<Map> list = DbUtil.openSession().selectList("demo.selectUser");
		DbUtil.close();
		return list;
		
		
		
//		//查询结果为list
//				List<Map> list = DbUtil.openSession().selectList("demo.selectUser");
//				//查询结果为map
//				Map paramMap = new HashMap();
//				paramMap.put("id", 1);
//				Map map = DbUtil.openSession().selectOne("demo.selectUserOne", paramMap);
//				//新增
//				DbUtil.openSession().insert("demo.insertUser", paramMap);
//				//删除
//				DbUtil.openSession().delete("demo.insertUser", paramMap);
//				return null;
//		
//		
//		DbUtil.openSession().insert("");
		
		//return "";
	}
	
	
	
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
	
	//
	//根据查询条件，查询一个用户
    public Document GetByID(String id){

//    	Document doc1=Document.parse(json);
    	
    	BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        Document doc = DbUtil.getColl(DocumentName).find(query).first();
    	
//    	Document doc= DbUtil.getColl(DocumentName).find(bson).first();
		return doc;
	}
	//查询一个用户
    public Document GetByCode(String json){

    	Bson bson=BsonDocument.parse(json);
    	Document doc= DbUtil.getColl(DocumentName).find(bson).first();
		return doc;
	}
    
    //判断用户名是否存在
    public Document ExistCode(String json){

    	Bson bson=BsonDocument.parse(json);
    	Document doc= DbUtil.getColl(DocumentName).find(bson).first();
		return doc;
	}
    
    
    //用户登录
    public String Login(String json){
    	//查询数据库中用户名及密码
    	
    	
    	//比较用户名
    	
    	Bson bson=BsonDocument.parse(json);
    //	String userCode=bson..get("user_code").toString();
    	Document doc= DbUtil.getColl(DocumentName).find(bson).first();
    	DbUtil.close();
    	
    	//比较密码
	
    	
    	//符合返回成功，否则失败
    	
    	return "";
    }
	
}
