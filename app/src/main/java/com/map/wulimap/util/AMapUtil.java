/**
 *
 */
package com.map.wulimap.util;


import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

import java.text.DecimalFormat;
import java.text.NumberFormat;


//高德map转换工具
public class AMapUtil {
    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    //double  to string
    public static String format(double num) {
        NumberFormat formatter = new DecimalFormat("0.############");
        String str = formatter.format(num);
        return str;
    }

}
