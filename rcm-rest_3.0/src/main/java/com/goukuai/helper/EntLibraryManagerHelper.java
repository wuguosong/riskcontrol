package com.goukuai.helper;

import com.goukuai.constant.YunkuConf;
import com.yunkuent.sdk.EntLibManager;

/**
 * Created by qp on 2017/3/2.
 */
public class EntLibraryManagerHelper {

    private static volatile EntLibManager instance = null;

    public static EntLibManager getInstance() {
        if (instance == null) {
            synchronized (EntLibManager.class) {
                if (instance == null) {
                    instance = new EntLibManager(YunkuConf.CLIENT_ID, YunkuConf.CLIENT_SECRET);
                }
            }
        }
        return instance;
    }
}
