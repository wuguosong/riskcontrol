package util;

import java.util.*;

/**
 * 全局上下文缓存工具类
 * Created by LiPan on 2019/2/19.
 */
public class CacheUtil {
    private static final ThreadLocal<Map<String, Object>> cache = new ThreadLocal() {
        protected Map<String, Object> initialValue() {
            return new HashMap(4);
        }
    };

    /**
     * 获取缓存对象
     * @return Map<String, Object>
     */
    public static Map<String, Object> getCache() {
        return cache.get();
    }

    /**
     * 通过Key获取
     * @param key Key
     * @param <T> Value
     * @return <T> T
     */
    public static <T> T get(String key) {
        Map map = (Map) cache.get();
        return (T) map.get(key);
    }

    /**
     * 通过Key获取,允许添加默认值
     * @param key Key
     * @param defaultValue 默认Value
     * @param <T> T
     * @return <T> T
     */
    public static <T> T get(String key, T defaultValue) {
        Map map = (Map) cache.get();
        return (T) map.get(key) == null ? defaultValue : (T) map.get(key);
    }

    /**
     * 通过Key设置进入缓存
     * @param key Key
     * @param value Value
     */
    public static void set(String key, Object value) {
        Map map = (Map) cache.get();
        map.put(key, value);
    }

    /**
     * 向缓存中设置键值对
     * @param keyValueMap 键值对
     */
    public static void set(Map<String, Object> keyValueMap) {
        Map map = (Map) cache.get();
        map.putAll(keyValueMap);
    }

    /**
     * 移除缓存中的所有数据
     */
    public static void remove() {
        cache.remove();
    }

    /**
     * 通过前缀获取指定的缓存
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> fetchVarsByPrefix(String prefix) {
        Map<String, T> vars = new HashMap<String, T>();
        if (prefix == null) {
            return vars;
        }
        Map map = (Map) cache.get();
        Set<Map.Entry> set = map.entrySet();
        for (Map.Entry entry : set) {
            Object key = entry.getKey();
            if (key instanceof String) {
                if (((String) key).startsWith(prefix)) {
                    vars.put((String) key, (T) entry.getValue());
                }
            }
        }
        return vars;
    }

    /**
     * 从缓存中通过Key移除
     * @param key Key
     * @param <T>
     * @return
     */
    public static <T> T remove(String key) {
        Map map = (Map) cache.get();
        return (T) map.remove(key);
    }

    /**
     * 通过前缀清除指定的缓存
     * @param prefix 前缀
     */
    public static void clear(String prefix) {
        if (prefix == null) {
            return;
        }
        Map map = (Map) cache.get();
        Set<Map.Entry> set = map.entrySet();
        List<String> removeKeys = new ArrayList<String>();

        for (Map.Entry entry : set) {
            Object key = entry.getKey();
            if (key instanceof String) {
                if (((String) key).startsWith(prefix)) {
                    removeKeys.add((String) key);
                }
            }
        }
        for (String key : removeKeys) {
            map.remove(key);
        }
    }
}
