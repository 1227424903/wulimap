package com.map.wulimap.util;


/**
 * Created by Administrator on 2016/4/28.
 */
public class Sdkversionutil {
    //获取版本号
    public static int getSDKVersionNumber() {
        int sdkVersion;
        try {
            sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        return sdkVersion;
    }


}
