package redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import util.CacheUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by LiPan on 2019/2/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class CacheTest {
    @Test
    public void getTest(){
        String value = CacheUtil.get("lipan", "初始值为空!");
        System.out.println(value);
    }

    @Test
    public void setTest() {
        for (int i = 1; i <= 4; i++){
            CacheUtil.set(i + "", i);
        }
        for (int i = 1; i <= 4; i++){
            System.out.println(CacheUtil.get(i + ""));
        }
    }

    @Test
    public void removeKeyTest() {
        String value = CacheUtil.get("lipan", "初始值为空!");
        System.out.println(value);
        CacheUtil.remove("lipan");
        System.out.println(CacheUtil.get("lipan", "已经被移除,值为空!"));
    }

    @Test
    public void removeTest(){
        for (int i = 1; i <= 4; i++){
            CacheUtil.set(i + "", i);
        }
        for (int i = 1; i <= 4; i++){
            System.out.println(CacheUtil.get(i + ""));
        }
        CacheUtil.remove();
        for (int i = 1; i <= 4; i++){
            System.out.println(CacheUtil.get(i + ""));
        }
    }

    @Test
    public void testClear(){
        for (int i = 1; i <= 4; i++){
            CacheUtil.set("wjsxhclj_" + i, "我就是喜欢吃辣椒_" + i);
        }
        for (int i = 1; i <= 4; i++){
            System.out.println(CacheUtil.get("wjsxhclj_" + i));
        }
        CacheUtil.clear("wjsxh");
        for (int i = 1; i <= 4; i++){
            System.out.println(CacheUtil.get("wjsxhclj_" + i));
        }
    }

    @Test
    public void testFetch(){
        for (int i = 1; i <= 4; i++){
            CacheUtil.set("pp_wjsxhclj_" + i, "pp_我就是喜欢吃辣椒_" + i);
            CacheUtil.set("po_wjsxhclj_" + i, "po_我就是喜欢吃辣椒_" + i);
        }
        for (int i = 1; i <= 4; i++){
            System.out.println(CacheUtil.get("pp_wjsxhclj_" + i));
            System.out.println(CacheUtil.get("po_wjsxhclj_" + i));
        }
        Map<String, String> map = CacheUtil.fetchVarsByPrefix("po_wjsxhclj_");
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for(Iterator<Map.Entry<String, String>> iterator = entries.iterator(); iterator.hasNext();){
            Map.Entry<String, String> entry = iterator.next();
            System.out.println(entry.getKey() + ":" + entry.getValue() );
        }
    }
}
