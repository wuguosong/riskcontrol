/**
 * 
 */
package com.yk.common;

import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 80845530
 *
 */
@Service("baseMongo")
public class BaseMongo implements IBaseMongo{
	@Resource
	private MongoClient mongoClient;
	@Value("${mongodb.database}")
	private String dbName;
	@Value("${mongodb.host}")
	private String dbHost;
	@Value("${mongodb.port}")
	private int dbPort;
	@Value("${mongodb.username}")
	private String dbUser;
	@Value("${mongodb.password}")
	private String dbAuth;
	public MongoDatabase queryDb(){
		// 地址和端口
		// List<ServerAddress> addresses = new ArrayList<ServerAddress>();
		// ServerAddress address = new ServerAddress(dbHost, dbPort);
		// addresses.add(address);
		// 权限验证
		// List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		// MongoCredential credential = MongoCredential.createScramSha1Credential(dbUser, dbName , dbAuth.toCharArray());
		// credentials.add(credential);
		// 获取客户端
		// mongoClient = new MongoClient(address, credentials);
		return this.mongoClient.getDatabase(dbName);
	}
	
	public MongoCollection<Document> queryCollection(String collectionName){
		return this.queryDb().getCollection(collectionName);
	}

	@Override
	public Map<String, Object> queryById(String id, String collectionName) {
		MongoCollection<Document> dbCollection = this.queryCollection(collectionName);
		BasicDBObject queryAndWhere =new BasicDBObject();
		Object queryId = null;
		try {
			queryId = new ObjectId(id);
		} catch (Exception e) {
			queryId = id;
		}
		queryAndWhere.put("_id", queryId);
		Document doc = dbCollection.find(queryAndWhere).first();
		if(doc == null){
			return null;
		}
		doc.put("id", id);
		return doc;
	}
	@Override
	public void updateSetByObjectId(String id, Map<String, Object> data, String collectionName) {
		if(data != null){
			MongoCollection<Document> dbCollection = this.queryCollection(collectionName);
			BasicDBObject queryAndWhere =new BasicDBObject();
			queryAndWhere.put("_id", new ObjectId(id));
			BasicDBObject update =new BasicDBObject();
			update.put("$set", data);
			dbCollection.updateOne(queryAndWhere, update);
		}
	}
	@Override
	public void deleteById(String id, String collectionName){
		MongoCollection<Document> dbCollection = this.queryCollection(collectionName);
		BasicDBObject queryAndWhere =new BasicDBObject();
		queryAndWhere.put("_id", new ObjectId(id));
		dbCollection.deleteOne(queryAndWhere);
	}
	@Override
	public void save(Document doc, String collectionName) {
		MongoCollection<Document> dbCollection = this.queryCollection(collectionName);
		dbCollection.insertOne(doc);
	}

	@Override
	public void updateSetById(String id, Map<String, Object> data,
			String collectionName) {
		MongoCollection<Document> dbCollection = this.queryCollection(collectionName);
		BasicDBObject queryAndWhere =new BasicDBObject();
		queryAndWhere.put("_id", id);
		BasicDBObject update =new BasicDBObject();
		update.put("$set", data);
		dbCollection.updateOne(queryAndWhere, update);
		
	}
	
	public AggregateIterable<Document> aggregate(String collectionName, List<? extends Bson> list){
		MongoCollection<Document> dbCollection = this.queryCollection(collectionName);
		AggregateIterable<Document> output = dbCollection.aggregate(list);
		return output;
	}

	@Override
	public MongoCollection<Document> getCollection(String collectionName) {
		return this.queryCollection(collectionName);
	}

	@Override
	public void updateSetByFilter(Bson doc, Map<String, Object> data,
			String collectionName) {
		MongoCollection<Document> coll = this.queryCollection(collectionName);
		BasicDBObject update =new BasicDBObject();
		update.put("$set", data);
		coll.updateMany(doc, update);
	}

	@Override
	public List<Map<String, Object>> queryByCondition(BasicDBObject queryWhere, String collectionName) {
		MongoCollection<Document> dbCollection = this.queryCollection(collectionName);
		FindIterable<Document> list = dbCollection.find(queryWhere);
		final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		list.forEach(new Block<Map<String, Object>>(){
			@Override
			public void apply(Map<String, Object> t) {
				result.add(t);
			}
		});
		
		return result;
	}

	@Override
	public void deleteByCondition(Bson filter, String collectionName) {
		MongoCollection<Document> dbCollection = this.queryCollection(collectionName);
		dbCollection.deleteMany(filter);
	}
	
}