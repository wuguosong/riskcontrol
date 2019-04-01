package mongo;

import com.goukuai.kit.Prop;
import com.goukuai.kit.PropKit;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiPan on 2019/4/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class ClientTest {
    @Resource
    private MongoClient mongoClient;
    @Test
    public void noPwdTest() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("rcmDB");
        System.out.println(mongoDatabase);
        ListCollectionsIterable<Document> list = mongoDatabase.listCollections();
        for(MongoCursor<Document> cursor = list.iterator(); cursor.hasNext();){
            Document document = cursor.next();
            System.out.println(document);
        }
    }

    @Test
    public void pwdTest() {
        Prop prop = PropKit.use("dev_db.properties");
        // 地址和端口
        List<ServerAddress> addresses = new ArrayList<ServerAddress>();
        ServerAddress address = new ServerAddress(prop.get("mongodb.host"), prop.getInt("mongodb.port"));
        addresses.add(address);
        // 权限验证
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();
        MongoCredential credential = MongoCredential.createScramSha1Credential(prop.get("mongodb.username"), prop.get("mongodb.database") , prop.get("mongodb.password").toCharArray());
        credentials.add(credential);
        // 获取客户端
        mongoClient = new MongoClient(address, credentials);
        // 获取数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(prop.get("mongodb.database"));
        // 获取集合
        MongoCollection<Document> collection = mongoDatabase.getCollection("rcm_pre_info");
        System.out.println(collection);
    }
}
