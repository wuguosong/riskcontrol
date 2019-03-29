package ctm;

import java.util.List;
import java.util.regex.Pattern;

import org.bson.BsonDocument;
import org.bson.Document;

import com.mongodb.BasicDBObject;

import common.BusinessException;
import util.CrudUtil;
import util.DbUtil;
import util.Util;

/**
 * 条款管理
 * 
 * @author zhouyoucheng
 *
 */
public class Clause {
	// 编号对应的字段
	public static final String CLAUSEID = "clause_id";

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
		String clause_id = CrudUtil.getString(data, CLAUSEID);
		Util.notNull(CLAUSEID, clause_id);
		// 重复校验
		Document doc = DbUtil.getColl(collection).find(new Document(CLAUSEID, clause_id)).first();
		if (Util.isNotEmpty(doc)) {
			throw new BusinessException(new StringBuilder(CLAUSEID).append(" already exists").toString());
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

	// 获取下一个条款编号
	public String nextId(String json) {
		BsonDocument jsonDoc = CrudUtil.parse(json);
		String collection = CrudUtil.getCollection(jsonDoc);
		BsonDocument condtBson = CrudUtil.getDocument(jsonDoc, collection);
		Util.notNull(ClauseType.CLAUSETYPE, condtBson);
		// 条框类型编号
		String clause_type = CrudUtil.getString(condtBson, ClauseType.CLAUSETYPE);
		Util.notNull(ClauseType.CLAUSETYPE, clause_type);
		// 根据条款类型匹配
		Pattern pattern = Pattern.compile(new StringBuffer(clause_type).append("_.*$").toString(),
				Pattern.CASE_INSENSITIVE);
		BasicDBObject basic = new BasicDBObject(CLAUSEID, pattern);
		// 获取当前最大编号记录
		Document doc = CrudUtil.maxSerial(collection, CLAUSEID, basic);
		int maxSerial = 0;
		if (Util.isNotEmpty(doc)) {
			// 该类型最大编号
			String maxClause_id = doc.getString(CLAUSEID);
			String[] ctp_cid = maxClause_id.split("_");
			// 最大条款编号
			String clause_id = ctp_cid[1];
			if (Util.isNotEmpty(clause_id)) {
				maxSerial = Integer.parseInt(clause_id);
			}
		}
		StringBuffer sb = new StringBuffer(clause_type).append("_").append(CrudUtil.convertSerial(maxSerial + 1, 3));
		return sb.toString();
	}
}
