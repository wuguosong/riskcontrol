package util;

import java.util.LinkedList;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;

import common.BusinessException;
import common.Constants;

/**
 * crud支持
 * 
 * @author zhouyoucheng
 *
 */
public class CrudUtil {

	// 查询
	public static List<Document> get(String json) {
		BsonDocument jsonDoc = parse(json);
		String collection = getCollection(jsonDoc);
		BsonDocument where = getWhere(collection, jsonDoc);
		MongoCursor<Document> cursor;
		if (Util.isEmpty(where)) {
			// 条件为空查询所有结果
			cursor = DbUtil.getColl(collection).find().iterator();
		} else {
			cursor = DbUtil.getColl(collection).find(where).iterator();
		}
		List<Document> result = convertCursorToList(cursor);
		return result;
	}

	// 删除
	public static void delete(String json) {
		BsonDocument jsonDoc = parse(json);
		String collection = getCollection(jsonDoc);
		BsonDocument where = getWhere(collection, jsonDoc);
		Util.notNull(Constants.WHERE, where);
		DbUtil.getColl(collection).deleteOne(where);
	}

	// 修改
	public static void update(String json) {
		BsonDocument jsonDoc = CrudUtil.parse(json);
		String collection = CrudUtil.getCollection(jsonDoc);
		BsonDocument where = CrudUtil.getWhere(collection, jsonDoc);
		Util.notNull(Constants.WHERE, where);
		Document set = CrudUtil.getSet(collection, jsonDoc);
		DbUtil.getColl(collection).updateOne(where, new Document("$set", set));
	}

	// 获取set
	public static Document getSet(String collection, BsonDocument jsonDoc) {
		BsonDocument paramDoc = jsonDoc.getDocument(collection);
		BsonDocument bsonDoc = getDocument(paramDoc, Constants.SET);
		Util.notNull(Constants.SET, bsonDoc);
		// 增加最后修改时间
		Document setDoc = Document.parse(bsonDoc.toJson());
		setDoc.put(Constants.LAST_UPDATE_DATE, Util.getTime());
		return setDoc;
	}

	// 获取新增数据
	public static BsonDocument getData(String collection, BsonDocument jsonDoc) {
		BsonDocument data = jsonDoc.getDocument(collection);
		Util.notNull(Constants.DATA, data);
		return data;
	}

	// 获取where
	public static BsonDocument getWhere(String collection, BsonDocument jsonDoc) {
		BsonDocument paramDoc = jsonDoc.getDocument(collection);
		return getDocument(paramDoc, Constants.WHERE);
	}

	// 获取表名
	public static String getCollection(BsonDocument jsonDoc) {
		// 空值校验
		Util.notNull("request message", jsonDoc);
		return jsonDoc.keySet().iterator().next();
	}

	// 获取bson中key对应的value值
	public static String getString(BsonDocument doc, String key) {
		return doc.containsKey(key) ? doc.getString(key).getValue() : null;
	}

	// 获取bson中key对应的BsonDocument
	public static BsonDocument getDocument(BsonDocument doc, String key) {
		return doc.containsKey(key) ? doc.getDocument(key) : null;
	}

	// 解析json
	public static BsonDocument parse(String json) {
		try {
			return BsonDocument.parse(json);
		} catch (Exception e) {
			throw new BusinessException("the format of json was invalid, the json was " + json);
		}
	}

	// 将查询结果由游标转换成集合
	public static List<Document> convertCursorToList(MongoCursor<Document> cursor) {
		List<Document> docList = new LinkedList<Document>();
		while (cursor.hasNext()) {
			//转换Document的id值为常规方式
		    Document document=cursor.next();
		    document.put("_id", document.get("_id").toString());
			docList.add(document);
		}
		return docList;
	}

	// 将bson转换成document并增加_id和creation_date，用于插入时使用
	public static Document convertBsonToInsertDoc(BsonDocument bson) {
		Document doc = Document.parse(bson.toJson());
		doc.put(Constants._ID, getHexString());
		doc.put(Constants.CREATION_DATE, Util.getTime());
		return doc;
	}

	// 获取一个ObjectId的hexString
	public static String getHexString() {
		return ObjectId.get().toHexString();
	}

	// 获取某个条款的最大编号记录
	public static Document maxSerial(String collection, String serialField) {
		return maxSerial(collection, serialField, null);
	}

	// 获取某个条款的最大编号记录，basic为条件
	public static Document maxSerial(String collection, String serialField, Bson basic) {
		// 降序排列
		Document orderBy = new Document(serialField, -1);
		// 只输出对应字段，其他字段不输出
		Document exclude = new Document(serialField, 1);
		exclude.append(Constants._ID, 0);
		if (Util.isEmpty(basic)) {
			return DbUtil.getColl(collection).find().projection(exclude).sort(orderBy).first();
		} else {
			return DbUtil.getColl(collection).find(basic).projection(exclude).sort(orderBy).first();
		}
	}

	// 将编号转换成字符串，长度不够，前面补0，digit表示位数
	public static String convertSerial(int serial, int digit) {
		int[] pointNum = new int[digit];
		for (int i = 0; i < digit; i++) {
			pointNum[i] = (int) Math.pow(10, i);
		}
		int num = digit - 1;
		while (--digit > 0) {
			if (serial >= pointNum[digit]) {
				num = num - digit;
				break;
			}
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < num; i++) {
			sb.append("0");
		}
		sb.append(serial);
		return sb.toString();
	}
}
