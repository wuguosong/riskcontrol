package ctm;

import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;

import common.BusinessException;
import util.CrudUtil;
import util.DbUtil;
import util.Util;

/**
 * 指标管理
 * 
 * @author zhouyoucheng
 *
 */
public class Index {
	// 编号对应的字段
	public static final String INDEXCODE = "index_code";

	// 查询
	public List<Document> get(String json) {
		return CrudUtil.get(json);
	}

	// 新增
	public void create(String json) {
		BsonDocument jsonDoc = CrudUtil.parse(json);
		String collection = CrudUtil.getCollection(jsonDoc);
		BsonDocument data = CrudUtil.getData(collection, jsonDoc);
		// 指标编号
		String index_code = CrudUtil.getString(data, INDEXCODE);
		Util.notNull(INDEXCODE, index_code);
		// 重复校验
		Document doc = DbUtil.getColl(collection).find(new Document(INDEXCODE, index_code)).first();
		if (Util.isNotEmpty(doc)) {
			throw new BusinessException(new StringBuilder().append(INDEXCODE).append(" already exists").toString());
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
}
