package announce;

import com.yk.announce.entity.Announce;
import com.yk.announce.servcie.IAnnounceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author lipan92
 * @version 1.0
 * @calssName AnnounceTest
 * @description 公告测试
 * @date 2019/11/21 0021 15:30
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class AnnounceServiceTest {
    @Autowired
    private IAnnounceService announceService;

    private List<Announce> list = new ArrayList();

    @Before
    public void before() {
        for (int i = 1; i <= 5; i++) {
            Announce announce = new Announce();
            announce.setTitle("主题" + i);
            announce.setComments("内容" + i);
            announce.setStatus("0");
            announce.setUpdateBy("admin92");
            announce.setUpdateTime(DateUtil.getCurrentDate());
            announce.setCreateBy("admin92");
            announce.setCreateTime(DateUtil.getCurrentDate());
            list.add(announce);
        }
    }

    @Test
    public void testList() {
        List<Announce> list1 = announceService.findList(new HashMap());
        for (Announce announce : list1) {
            System.out.println(announce);
        }
    }

    @Test
    public void testOne() {
        Announce announce = announceService.findOne(10033L);
        System.out.println(announce);
    }


    @Test
    public void testSave() {
        for (Announce announce : list) {
            Announce anno = announceService.save(announce);
            System.out.println(anno);
        }
    }

    @Test
    public void testUpdate() {
        Announce announce1 = announceService.findOne(10033L);
        announce1.setUpdateTime(DateUtil.getCurrentDate());
        announce1.setUpdateBy("lipan92");
        Announce announce2 = announceService.update(announce1);
        System.out.println(announce2);
    }

    @Test
    public void testDel() {
        announceService.delete(10033L);
    }
}
