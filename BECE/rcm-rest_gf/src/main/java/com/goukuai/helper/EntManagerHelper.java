package com.goukuai.helper;

import com.goukuai.constant.YunkuConf;
import com.yunkuent.sdk.EntManager;

/**
 * Created by qp on 2017/3/2.
 */
public class EntManagerHelper {

    private static volatile EntManager instance = null;

    public static EntManager getInstance() {
        if (instance == null) {
            synchronized (EntManager.class) {
                if (instance == null) {
                    instance = new EntManager(YunkuConf.CLIENT_ID, YunkuConf.CLIENT_SECRET);
                }
            }
        }
        return instance;
    }
}
