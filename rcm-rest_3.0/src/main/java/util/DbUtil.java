package util;

import java.net.UnknownHostException;

import org.apache.ibatis.session.SqlSession;
import org.bson.Document;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.yk.common.SpringUtil;

/**
 * 数据库连接
 * 
 * @author zhouyoucheng
 *
 */
public class DbUtil {
//	private static SqlSessionFactory factory;
//	private static MongoClient client;
//	private static String dbName; // mongodb数据库名
//	private static ThreadLocal<SqlSession> mybatisTl = new ThreadLocal<SqlSession>();

	// 初始化
	public static void init(String mybatisConfig, String mongoDbConfig) {
		// 加载mybatis
		/*try {
			Reader reader = Resources.getResourceAsReader(mybatisConfig);
			factory = new SqlSessionFactoryBuilder().build(reader);
		} catch (Exception e) {
			throw new ServletException("failed to initialize mybatis, the reason was " + e.getCause());
		}
		// 加载mongodb
		try {
			Properties prop = new Properties();
			InputStream is = DbUtil.class.getClassLoader().getResourceAsStream(mongoDbConfig);
			prop.load(is);
			// 连接host
			String host = prop.getProperty("mongodb.host");
			// 连接端口
			int port = Integer.parseInt((String) prop.get("mongodb.port"));
			// 数据库名
			dbName = prop.getProperty("mongodb.database");
			client = new MongoClient(host, port);
		} catch (Exception e) {
			throw new ServletException("failed to initialize mongodb, the reason was " + e.getCause());
		}*/
	}

	/*********************************** mybatis相关 ***********************************/

	// 获取一个session
	public static SqlSession openSession() {
//		return openSession(true);
		return (SqlSession) SpringUtil.getBean("sqlSession");
	}

	// 获取一个session
	public static SqlSession openSession(boolean autoCommit) {
		/*try {
			if (mybatisTl.get() == null || mybatisTl.get().getConnection().isClosed()) {
				if(factory == null) reload();
				mybatisTl.set(factory.openSession(autoCommit));
			}
		} catch (SQLException e) {
			throw new RuntimeException("failed to execute mybatis openSession ", e);
		}
		return mybatisTl.get();*/
		return openSession();
	}

	// 关闭session
	public static void close() {
		/*SqlSession session = mybatisTl.get();
		if (session != null) {
			session.close();
			session = null;
			mybatisTl.set(null);
		}*/
	}

	// 回滚
	public static void rollback() {
		/*SqlSession session = mybatisTl.get();
		if (session != null) {
			session.rollback();
		}*/
	}

	// 提交
	public static void commit() {
		/*SqlSession session = mybatisTl.get();
		if (session != null) {
			session.commit();
		}*/
	}

	/*********************************** mongodb相关 ***********************************/

	/**
	 * 获取文档操作对象
	 * @param collection
	 * @return
	 */
	@Deprecated
	public static MongoCollection<Document> getColl(String collection) {
		MongoClient client = (MongoClient) SpringUtil.getBean("mongoClient");
		return client.getDatabase(PropertiesUtil.getProperty("mongodb.database")).getCollection(collection);
	}
	@Deprecated
	public static DBCollection getCollection(String table) throws UnknownHostException {

		MongoClient mongoClient = new MongoClient(new ServerAddress(PropertiesUtil.getProperty("mongodb.host"), Integer.valueOf(PropertiesUtil.getProperty("mongodb.port"))));
		DB db = mongoClient.getDB(PropertiesUtil.getProperty("mongodb.database"));
		DBCollection collection = db.getCollection(table);
		return collection;
	}

}
