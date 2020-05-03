package com.linkflow.cpe.net;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Administrator on 2016/10/11 0011.
 * /辅助处理异常
 */

public class ApiErrorHelper {

    public static void handleCommonError(Context context, Throwable e) {
        if (e instanceof HttpException) {
            Toast.makeText(context, "服务器异常，请稍后重试", Toast.LENGTH_SHORT).show();
        } else if (e instanceof IOException) {
            Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
        } else if (e instanceof ApiException) {
            if (((ApiException) e).getErrorCode()==2){
//                UserManager.getIns().clearUserInfo();
//                context.startActivity(new Intent(context,LoginActivity.class));
//                ((Activity)context).finish();
                //这里是退出登录
            }
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Log.e("error111111",e.getMessage());
            Toast.makeText(context, "数据异常，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

}
