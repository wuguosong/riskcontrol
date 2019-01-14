package com.goukuai.helper;

import com.goukuai.constant.YunkuConf;
import com.yunkuent.sdk.EntFileManager;
import com.yunkuent.sdk.SSOManager;

/**
 * Created by wink on 2018/3/12.
 */
public class SSOManagerHelper {

    private static volatile SSOManager instance = null;

    public static SSOManager getInstance() {
        if (instance == null) {
            synchronized (EntFileManager.class) {
                if (instance == null) {
                    instance = new SSOManager(YunkuConf.ORG_CLIENT_ID, YunkuConf.ORG_CLIENT_SECRET);
                }
            }
        }
        return instance;
    }
}
