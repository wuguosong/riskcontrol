/**
 * 
 */
package ctm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;

import com.mongodb.client.MongoCursor;

import common.Constants;
import util.CrudUtil;
import util.DbUtil;

/** 
* @Description: 合同管理
* @author zhangkewei
* @date 2016年3月30日 下午12:28:03 
*/
public class ContractInfo {
		// 编号对应的字段
		public static final String CONTRACT_CODE = "contract_code";

		// 查询
		public List<Document> get(String json) {
			return CrudUtil.get(json);
		}
		
		
		//获取字典表
		//{"dics":[{"collection_name":"ctm_sourcing_method","columns":["sourcing_method_name","sourcing_method_code"]}]}
		public Document dicList(String json){
			BsonDocument jsonDoc = CrudUtil.parse(json);
			BsonArray bsonArray = jsonDoc.getArray("dics");
			Iterator<BsonValue> it = bsonArray.iterator();
			Document myDoc = new Document();
			Document dicDoc = new Document();
			myDoc.append("dic", dicDoc);
			while(it.hasNext()){
				BsonDocument bs = (BsonDocument)it.next();
				String collection = bs.getString("collection_name").getValue();
				BsonArray columns = bs.getArray("columns");
				MongoCursor<Document> cursor = DbUtil.getColl(collection).find().iterator();
				List<Document> list = this.convertCursorToList(cursor, columns);
				dicDoc.append(collection, list);
			}
			return myDoc;
		}
		
		private List<Document> convertCursorToList(MongoCursor<Document> cursor, BsonArray columns) {
			List<Document> docList = new LinkedList<Document>();
			while (cursor.hasNext()) {
				Document d = cursor.next();
				if(null != columns && columns.size() > 0){
					Document doc = new Document();
					//获取目标列，并将列插入新的document中
					Iterator<BsonValue> it = columns.iterator();
					while(it.hasNext()){
						BsonValue bv = it.next();
						String colName = bv.asString().getValue();
						String colValue = d.getString(colName);
						doc.append(colName, colValue);
					}
					docList.add(doc);
				}else{
					docList.add(d);
				}
			}
			return docList;
		}

		// 新增
		public void create(String json) {
			//System.out.println(json);
			BsonDocument jsonDoc = CrudUtil.parse(json);
			String collection = CrudUtil.getCollection(jsonDoc);
			BsonDocument data = CrudUtil.getData(collection, jsonDoc);
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
		
		public void saveHtml(String json){
			BsonDocument jsonDoc = CrudUtil.parse(json);
			BsonDocument bs = (BsonDocument)jsonDoc.get("xx");
			String html = bs.getString("html").getValue();
			String js = bs.getString("javascript").getValue();
			String htmlName = bs.getString("htmlName").getValue();
			try {
				this.saveHmtlFile(htmlName, html, js);
			} catch (IOException e) {
				throw new RuntimeException("保存html文件出错", e);
			}
		}
		
		/*
		 * 保存html文件
		 */
		private void saveHmtlFile(String fileName, String html, String js) throws IOException{
			String basePath = "E:/workspace/ctm-new/html/page/ctm/contract/",
					contractTemplate = "contract_template.html";
			//先获取模板页面内容
			File templateFile = new File(basePath + contractTemplate);
			InputStream is = null;
			OutputStream os = null;
			BufferedOutputStream bos = null;
			try {
				StringBuffer sb = new StringBuffer();
				is = new FileInputStream(templateFile);
				byte[] b = new byte[8*1024];
				while(is.read(b) != -1){
					sb.append(new String(b,Constants.UTF8));
				}
				String content = sb.toString().replace("#includePage#", html).replace("#includeJs#", js);
				if(!fileName.endsWith(".html")){
					fileName += ".html";
				}
				File destFile = new File(basePath+fileName);
				os = new FileOutputStream(destFile);
				bos = new BufferedOutputStream(os);
				bos.write(content.getBytes(Constants.UTF8));
			} finally{
				if(is != null) is.close();
				if(bos != null) bos.close();
				if(os != null) os.close();
			}
		}
}
