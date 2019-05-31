package com.yk.initfile.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Mongo分页过滤
 */
public class MyFilter {
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
                    System.out.println(index);
                    register.add(t);
                }
            }
            return register;
        }
    }
}
