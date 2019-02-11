package redis;

import org.junit.runner.RunWith;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import util.RedisUtil;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by LiPan on 2019/1/31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class RedisTest {
    // 通过RedisTemplate来进行redis操作
    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Test
    public void set(){
        redisTemplate.opsForValue().set("wjsxhclj", "我就是喜欢吃辣椒");
    }

    @Test
    public void get(){
        // 通过RedisUtil来进行redis操作
        String value = RedisUtil.get("wjsxhclj");
        System.out.println(value);
    }

    @Test
    public void getProgress(){
        String progressLine = (String)redisTemplate.opsForValue().get("progressLine_" + "linux.x64_11gR2_database_2of2.zip");
        System.out.println(progressLine);
    }

    @Test
    public void del(){
        RedisUtil.del("wjsxhclj");
    }

}
