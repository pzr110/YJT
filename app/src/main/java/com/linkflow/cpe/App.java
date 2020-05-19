package com.linkflow.cpe;


import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;

import java.io.File;

import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;

/**
 * Custom applcation class
 */
public class App extends Application {
    public static App INS;
    private static final Handler sHandler = new Handler();

//    public static String BaseUrl = "http://183.220.194.106:38080/cpe/";
//    public static String BaseUrl ="http://192.168.41.64:8080/cpe/";//内网地址
//    public static String BaseUrl ="http://192.168.0.250:8000/cpe/";//内网地址
    public static String BaseUrl ="http://192.168.0.17:8000/cpe/";//内网地址

    // Singleton pattern to get unique MyApplication instance
    public static App getInstance() {
        if (null == INS) {
            INS = new App();
        }
        return INS;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INS = this;
        initUpdate();
        TokenUtils.init(this);
        String logPath = new File(Environment.getExternalStorageDirectory(), "demo.log").getAbsolutePath();
//        Log.e(true, true, logPath, true);
        testGetHeap();
    }


    private void testGetHeap() {
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        Log.i("OneDemoApplication:", " MaxMemory " + Long.toString(maxMemory / (1024 * 1024)));

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        Log.e("OneDemoApplication:", " MemoryClass " + Long.toString(activityManager.getMemoryClass()));
        Log.e("OneDemoApplication:", " LargeMemoryClass " + Long.toString(activityManager.getLargeMemoryClass()));


        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        Log.e("OneDemoApplication", "系统总内存:" + (info.totalMem / (1024 * 1024)) + "M");
        Log.e("OneDemoApplication", "系统剩余内存:" + (info.availMem / (1024 * 1024)) + "M");
        Log.e("OneDemoApplication", "系统是否处于低内存运行：" + info.lowMemory);
        Log.e("OneDemoApplication", "系统剩余内存低于" + (info.threshold / (1024 * 1024)) + "M时为低内存运行");
    }

    //Initialize third-party update plugin initialization, must have
    public void initUpdate() {
        XUpdate.get()
                .debug(true)
                .isWifiOnly(false)                                               //The default setting only checks version updates under wifi
                .isGet(false)                                                    //The default setting uses get requests to check the version
                .isAutoMode(false)                                              //The default setting is non-automatic mode, which can be configured according to the specific use
                .param("versionCode", UpdateUtils.getVersionCode(this))         //Set default public request parameters
                .param("appKey", getPackageName())
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {     //Set the listener for version update errors
                    @Override
                    public void onFailure(UpdateError error) {
                        if (error.getCode() != CHECK_NO_NEW_VERSION) {          //Dealing with different errors
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .supportSilentInstall(true)                                     //Sets whether to support silent installation. The default is true.
                .setIUpdateHttpService(new OKHttpUpdateHttpService())           //This must be set! Implement network request functions.
                .init(this);                                      //This must be initialized
    }

    public static void runUi(Runnable runnable) {
        sHandler.post(runnable);
    }
}
