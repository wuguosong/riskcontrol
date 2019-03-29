package util;

import org.springframework.data.redis.core.RedisTemplate;


/**
 * RedisUtil操作工具类
 * 通过XML方式注入RedisTemplate
 * Created by LiPan on 2019/2/11.
 */
public class RedisUtil {
    private static RedisTemplate<Object, Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void init() {
    }

    /**
     * 通过Key获取Value
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * 通过ey删除Value
     *
     * @param key
     */
    public static void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置
     *
     * @param key   Key
     * @param value Value
     * @return
     */
    public static void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
}
