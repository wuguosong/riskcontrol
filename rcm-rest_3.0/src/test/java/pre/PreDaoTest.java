package pre;

import com.yk.rcm.pre.dao.IPreAuditLogMapper;
import com.yk.rcm.pre.dao.IPreInfoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import util.DateUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class PreDaoTest {
    @Resource
    private IPreAuditLogMapper preAuditLogMapper;
    @Resource
    private IPreInfoMapper preInfoMapper;

    @Test
    public void testLogSave() {
        Map<String, Object> data = new HashMap();
        data.put("businessid", "1008611");
        data.put("audituserid", "1008611");
        data.put("audittime", DateUtil.getCurrentDate());
        data.put("opinion", "1008611");
        data.put("auditstatus", "1008611");
        data.put("orderby", "1008611");
        data.put("iswaiting", "1008611");
        data.put("taskdesc", "1008611");
        data.put("taskid", "1008611");
        data.put("executionid", "1008611");
        data.put("lastUserId", "1008611");
        data.put("stage", "1008611");
        data.put("changeType", "1008611");
        data.put("oldUserId", "1008611");
        data.put("notifUsers", "1008611");
        int result = preAuditLogMapper.save(data);
        System.out.println("执行结果：" + result);
    }

    @Test
    public void testUpdateAuditStatusByBusinessId() {
        Map<String, Object> data = new HashMap();
        data.put("businessId", "5a1e2f7222ddf2508278de51");
        data.put("wf_state", "2");
        int result = preInfoMapper.updateAuditStatusByBusinessId(data);
        System.out.println("执行结果：" + result);
    }

    @Test
    public void testUpdateLastUpdateDate() {
        Map<String, Object> data = new HashMap();
        data.put("businessId", "5a1e2f7222ddf2508278de51");
        try{
            data.put("last_update_date", DateUtil.convertStrToDate("2017-12-12 13:22:49", DateUtil.DATEFORMAT_YYYY_MM_DD_HH_MM_SS));
        }catch(Exception e){
            e.printStackTrace();
        }
        int result = preInfoMapper.updateLastUpdateDate(data);
        System.out.println("执行结果：" + result);
    }
}
