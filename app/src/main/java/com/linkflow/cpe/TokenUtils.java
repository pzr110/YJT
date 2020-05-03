package com.linkflow.cpe;

import android.content.Context;

import com.tencent.mmkv.MMKV;


public class TokenUtils {

    private static final String KEY_TOKEN = "KEY_TOKEN";//保存的token信息
    private static final String KEY_RTMP = "KEY_RTMP";//推流地址
    private static final String KEY_USER = "KEY_USER";//保存的用户信息
    private static final String KEY_USERID = "KEY_USERID";//用户ID

    private TokenUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化Token信息
     */
    public static void init(Context context) {
        MMKV.initialize(context);
    }

    //    token---------------------------------------------
    public static void setToken(String token) {
        MMKV.defaultMMKV().putString(KEY_TOKEN, token);
    }

    public static String getToken() {
        return MMKV.defaultMMKV().decodeString(KEY_TOKEN);
    }

    public static void clearToken() {
        MMKV.defaultMMKV().remove(KEY_TOKEN);
    }

    public static boolean hasToken() {
        return MMKV.defaultMMKV().containsKey(KEY_TOKEN);
    }
    //    token---------------------------------------------

    //    RTMP操作-------------------------------------------
    public static void setRtmp(String rtmp) {
        MMKV.defaultMMKV().putString(KEY_RTMP, rtmp);
    }

    public static String getRtmp() {
        return MMKV.defaultMMKV().decodeString(KEY_RTMP);
    }

    public static boolean hasRtmp() {
        return MMKV.defaultMMKV().containsKey(KEY_RTMP);
    }

    public static void claerRtmp() {
        MMKV.defaultMMKV().remove(KEY_RTMP);
    }

    //    RTMP操作-------------------------------------------
//    User_id-------------------------------------------
    public static void setUserId(String user_id) {
        MMKV.defaultMMKV().putString(KEY_USERID, user_id);
    }

    public static String getUserId() {
        return MMKV.defaultMMKV().decodeString(KEY_USERID);
    }

    public static boolean hasUserId() {
        return MMKV.defaultMMKV().containsKey(KEY_USERID);
    }

    public static void claerUserId() {
        MMKV.defaultMMKV().remove(KEY_USERID);
    }

    //    User_id-------------------------------------------


    //    user-------------------------------------------
    public static void setUser(String user) {
        MMKV.defaultMMKV().putString(KEY_USER, user);
    }

    public static String getUser() {
        return MMKV.defaultMMKV().decodeString(KEY_USER);
    }

    public static boolean hasUser() {
        return MMKV.defaultMMKV().containsKey(KEY_USER);
    }

    public static void claerUser() {
        MMKV.defaultMMKV().remove(KEY_USER);
    }
//    user-------------------------------------------
}
