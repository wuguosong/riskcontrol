/**
 * 
 */
package projectPreReview;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;

import util.CrudUtil;
import util.DbUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.QueryOperators;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

/**
 * @Description: TODO
 * @Author zhangkewei
 * @Date 2016年8月15日 下午1:17:42  
 */
public class ProjectPreReviewTest {
	private final String DocumentName="rcm_projectPreReview_info";	
	
	private void init(){
		DbUtil.init("configuration.xml", "db.properties");
	}
	@Test
	public void getAll(){
		init();
		MongoCollection<Document> userCollection = DbUtil.getColl(DocumentName);
//		Bson bson = new Bson();
//		MongoCursor<Document> resutl = userCollection.find().sort(new BasicDBObject("userId",-1)).iterator();
		BasicDBObject filter = new BasicDBObject("pageKey",new BasicDBObject(QueryOperators.GT, 10));
		MongoCursor<Document> resutl = userCollection.find(filter).sort(new BasicDBObject("pageKey",1)).skip(0).limit(10).iterator();
		long count = userCollection.count(new BasicDBObject());
		List<Document> list = CrudUtil.convertCursorToList(resutl);
		for(Document d : list){
			System.out.println(d.get("userId")+"--------------"+d.get("pageKey"));
		}
		//userCollection.find().sort("");
	}
	@Test
	public void add(){
		init();
		for(int i=0;i<100;i++){
			Document doc = new Document();
			doc.append("userId", "user"+i);
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");//可以方便地修改日期格式
			String  createDate= dateFormat.format(now);
			doc.append("create_date", createDate);
			doc.append("pageKey", i);
			DbUtil.getColl(DocumentName).insertOne(doc);
		}
	}
	
	@Test
	public void findAttachmentList(){
		init();
		try {
			String json = "{projectTypes:\"'0201','0202'\"}";
			ProjectPreReview pre = new ProjectPreReview();
			List<Map> list = pre.findAttachmentList(json);
			System.out.println(list.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void update(){
		try {
			init();
			BasicDBObject where =new BasicDBObject();
			where.put("_id", new ObjectId("57ca5099fdcd931830e8d49f"));
			Document set = new Document();
			set.put("aaa", "ccccccccccc");
			DbUtil.getColl("pre_assessment").updateOne(where, new Document("$set", set));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void xPath(){
		init();
		try {
			ProjectPreReview r = new ProjectPreReview();
			Document doc = r.getProjectPreReviewByID("57cf832cfdcd931234353666");
			Document approveAttachment = (Document)doc.get("approveAttachment");
			List<Document> docL = (List<Document>)approveAttachment.get("attachmentNew");
			for(Document d : docL){
				System.out.println(d.toJson());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
