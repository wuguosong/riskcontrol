package notify;

import com.yk.notify.entity.Notify;
import com.yk.notify.service.INotifyService;
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
public class ServiceTest {
    @Resource
    private INotifyService notifyService;
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
        notifyService.save(notify);
        System.out.println(notify);
    }

    @Test
    public void testGet(){
        notify = notifyService.get("10043");
        System.out.println(notify);
    }

    @Test
    public void testUpdate(){
        notify = notifyService.get("10043");
        notify.setNotifyUserName("我就是喜欢吃辣椒");
        notifyService.update(notify);
        System.out.println(notify);
    }

    @Test
    public void testDelete(){
        System.out.println(notify);
        notifyService.delete("10043");
        notify = notifyService.get("10043");
        System.out.println(notify);
    }

    @Test
    public void testList(){
        /*List<Notify> notifies = notifyService.list(null, null);
        for(Notify notify : notifies){
            System.out.println(notify);
        }*/
    }
}
