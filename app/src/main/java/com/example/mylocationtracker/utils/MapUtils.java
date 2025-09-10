package com.example.mylocationtracker.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapUtils {

    /**
     * 将经纬度转换为地址字符串
     *
     * @param context Context，用于 Geocoder
     * @param latLng  经纬度坐标
     * @return 地址字符串，如果获取失败返回 null
     */
    public static String getAddressFromLatLng(Context context, LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
