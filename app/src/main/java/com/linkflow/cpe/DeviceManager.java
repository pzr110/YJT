package com.linkflow.cpe;

import android.content.SharedPreferences;
import android.util.Base64;

import com.linkflow.fitt360sdk.item.Device;


/**
 * Streaming media account information is stored locally.
 */
public class DeviceManager {
    private static DeviceManager mDeviceManager;

    private DeviceManager() {

    }

    public static DeviceManager getIns() {
        if (mDeviceManager == null) {
            synchronized (DeviceManager.class) {
                mDeviceManager = new DeviceManager();
            }
        }
        return mDeviceManager;
    }

    /**
     * Get account information
     * @return
     */
    public synchronized Device getDeviceInfo() {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("deviceinfo", 0);
        String temp = mPreferences.getString("_device", "");
        byte[] base64Bytes = Base64.decode(temp, Base64.DEFAULT);
        String retStr = new String(base64Bytes);
        return JsonParser.deserializeByJson(temp, Device.class);
    }

    /**
     * Save account information
     */
    public synchronized void saveDeviceInfo(Device user) {
        if (user == null) return;
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("deviceinfo", 0);
        String json = JsonParser.serializeToJson(user);
        String temp = new String(Base64.encode(json.getBytes(), Base64.DEFAULT));
        mPreferences.edit().putString("_device", json).commit();
    }
    /**
     * Clear account information
     */

    public synchronized void clearDeviceInfo() {
        SharedPreferences mPreferences = App.getInstance().getSharedPreferences("deviceinfo", 0);
        mPreferences.edit().putString("_device", "").commit();
    }
}
