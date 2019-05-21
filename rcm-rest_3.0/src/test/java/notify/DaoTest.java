package notify;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yk.notify.dao.INotifyMapper;
import com.yk.notify.entity.Notify;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by LiPan on 2019/3/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class DaoTest {
    @Resource
    private INotifyMapper notifyMapper;
    @Resource
    private Notify notify;
    @Test
    public void testSave(){
        notify.setAssociateId("1008611");
        notify.setBusinessId("1008611XXX");
        notify.setBusinessModule("XXX");
        notify.setNotifyComments("Notify");
        notify.setNotifyCreated("1008411LiPan");
        notify.setNotifyCreatedName("LiPan");
        notify.setNotifyUser("1005711This");
        notify.setNotifyUserName("This");
        notifyMapper.insertNotify(notify);
        System.out.println(notify);
    }

    @Test
    public void testGet(){
        notify = notifyMapper.selectNotifyById("10041");
        System.out.println(notify);
    }

    @Test
    public void testUpdate(){
        notify = notifyMapper.selectNotifyById("10041");
        notifyMapper.modifyNotify(notify);
        System.out.println(notify);
    }

    @Test
    public void testDelete(){
        notifyMapper.removeNotify("10041");
        notify = notifyMapper.selectNotifyById("10041");
        System.out.println(notify);
    }

    @Test
    public void testList(){
        /*List<Notify> notifies = notifyMapper.selectNotifies(null, null);
        for(Notify notify : notifies){
            System.out.println(notify);
        }*/
    }

    @Test
    public void testReadList(){
        List<JSONObject> list = notifyMapper.selectNotifyInfo("0001N6100000000MXI0K");
        for(JSONObject jsonObject : list){
            System.out.println(JSON.toJSONString(jsonObject));
        }
    }

}
