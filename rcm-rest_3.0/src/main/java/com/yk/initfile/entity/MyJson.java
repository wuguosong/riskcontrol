package com.yk.initfile.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * MyJson
 */
public class MyJson extends JSONObject implements Comparable<MyJson> {
    @Override
    public int compareTo(MyJson o) {
        String v1 = this.getString(JsonField.version).split("\\.")[0];
        String v2 = o.getString(JsonField.version).split("\\.")[0];
        int result = new Integer(v1).compareTo(new Integer(v2));
        if(result > 0){
            return -1;
        }else{
            return 1;
        }
    }
}
