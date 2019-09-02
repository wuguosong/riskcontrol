package com.goukuai.helper;

import com.goukuai.constant.YunkuConf;
import com.yunkuent.sdk.ThirdPartyManager;

/**
 * Created by qp on 2017/3/16.
 */
public class ThirdPartyManagerHelper {

    private static volatile ThirdPartyManager instance = null;

    public static ThirdPartyManager getInstance() {
        if (instance == null) {
            synchronized (ThirdPartyManager.class) {
                if (instance == null) {
                    instance = new ThirdPartyManager(YunkuConf.CLIENT_ID, YunkuConf.CLIENT_SECRET, YunkuConf.OUT_ID);
                }
            }
        }
        return instance;
    }
}
