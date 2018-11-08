package ctm;

import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;

import common.BusinessException;
import util.CrudUtil;
import util.DbUtil;
import util.Util;

/**
 * 条款类型管理
 * 
 * @author zhouyoucheng
 *
 */
public class ClauseType {
	// 编号对应的字段
	public static final String CLAUSETYPE = "clause_type";

	// 查询
	public List<Document> get(String json) {
		return CrudUtil.get(json);
	}

	// 新增
	public void create(String json) {
		BsonDocument jsonDoc = CrudUtil.parse(json);
		String collection = CrudUtil.getCollection(jsonDoc);
		BsonDocument data = CrudUtil.getData(collection, jsonDoc);
		// 空值校验
		String clause_type = CrudUtil.getString(data, CLAUSETYPE);
		Util.notNull(CLAUSETYPE, clause_type);
		// 重复校验
		Document doc = DbUtil.getColl(collection).find(new Document(CLAUSETYPE, clause_type)).first();
		if (Util.isNotEmpty(doc)) {
			throw new BusinessException(new StringBuilder(CLAUSETYPE).append(" already exists").toString());
		}
		Document insertDoc = CrudUtil.convertBsonToInsertDoc(data);
		DbUtil.getColl(collection).insertOne(insertDoc);
	}

	// 删除
	public void delete(String json) {
		CrudUtil.delete(json);
	}

	// 修改
	public void update(String json) {
		CrudUtil.update(json);
	}

	// 获取下一个条款类型编号
	public String nextId(String json) {
		BsonDocument jsonDoc = CrudUtil.parse(json);
		String collection = CrudUtil.getCollection(jsonDoc);
		// 获取当前最大编号记录
		Document doc = CrudUtil.maxSerial(collection, CLAUSETYPE);
		int maxSerial = 0;
		if (Util.isNotEmpty(doc) && doc.containsKey(CLAUSETYPE)) {
			String clause_type = doc.getString(CLAUSETYPE);
			maxSerial = Integer.parseInt(clause_type);
		}
		return CrudUtil.convertSerial(maxSerial + 1, 3);
	}
}
