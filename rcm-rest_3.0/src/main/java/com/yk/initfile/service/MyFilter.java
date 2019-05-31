package com.yk.initfile.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Mongo分页过滤
 */
public class MyFilter {
    private static final Logger logger = LoggerFactory.getLogger(MyFilter.class);
    private int limit;
    private int skip;

    public MyFilter(int limit, int skip) {
        this.limit = limit;
        this.skip = skip;
    }

    /**
     * 分页过滤
     * @param list
     * @param <T>
     * @return
     */
    public <T> List<T> doFilter(List<T> list) {
        if (limit == 0) {
            return list;
        }
        int size = list.size();
        if (size <= limit) {
            return list;
        } else {
            List<T> register = new ArrayList<T>();
            for (T t : list) {
                int index = list.indexOf(t);
                if (index >= skip && index < limit) {
                    logger.debug("当前索引：" + index);
                    register.add(t);
                }
            }
            return register;
        }
    }
}
