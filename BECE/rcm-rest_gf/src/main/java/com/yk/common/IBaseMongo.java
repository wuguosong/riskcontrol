/**
 * 
 */
package com.yk.common;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;

/**
 * @author 80845530
 *
 */
public interface IBaseMongo {
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Map<String, Object> queryById(String id, String collectionName);
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> queryByCondition(BasicDBObject queryWhere, String collectionName);
	/**
	 * 根据id修改，用来指定一个键的值，若该键不存在，则创建
	 * @param id
	 * @param data
	 */
	public void updateSetById(String id, Map<String, Object> data, String collectionName);
	/**
	 * 根据objectId修改，用来指定一个键的值，若该键不存在，则创建
	 * @param id
	 * @param data
	 */
	public void updateSetByObjectId(String id, Map<String, Object> data, String collectionName);
	/**
	 * 根据条件修改
	 * @param filter
	 * @param data
	 * @param collectionName
	 */
	public void updateSetByFilter(Bson filter, Map<String, Object> data, String collectionName);
	/**
	 * 根据id删除
	 * @param id
	 */
	public void deleteById(String id, String collectionName);
	/**
	 * 根据条件删除
	 * @param id
	 */
	public void deleteByCondition(Bson filter, String collectionName);
	/**
	 * 保存
	 * @param doc
	 */
	public void save(Document doc, String collectionName);
	/**
	 * 获取collection
	 * @param collectionName
	 * @return
	 */
	public MongoCollection<Document> getCollection(String collectionName);
	/**
	 * 聚合操作
	 * @param collectionName
	 * @param pipeline
	 * @return
	 */
	public AggregateIterable<Document> aggregate(String collectionName, List<? extends Bson> pipeline);
	
}
