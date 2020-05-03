package com.linkflow.cpe.net;

import android.app.Activity;


import com.android.tu.loadingdialog.LoadingDailog;
import com.linkflow.cpe.NetStateUtils;

import rx.Subscriber;

/**
 * Created by Administrator on 2016/10/11 0011.
 *  http://blog.csdn.net/dd864140130/article/details/52689010
 */

public class BaseSubscriber<T> extends Subscriber<T> {
    private Activity mContext;
    private LoadingDailog mDialog;

    //这个是不显示加载进度的。
    public BaseSubscriber(Activity context,boolean noDialog) {
        mContext = context;
    }

    public BaseSubscriber(Activity context) {
        mContext = context;
        LoadingDailog.Builder loadBuilder=new LoadingDailog.Builder(context)
                .setCancelable(true)
                .setCancelOutside(true);
        mDialog=loadBuilder.create();
    }

   @Override
    public void onStart() {
        //请求开始之前，检查是否有网络。无网络直接抛出异常
        if (!NetStateUtils.isConnected(mContext)) {
            this.onError(new ApiException(ApiErrorCode.ERROR_NO_INTERNET,"无网络连接"));
        }
         else {
//           if (mDialog==null){
//               mDialog = new Dialog(mContext);
//           }
            if(mDialog!=null){
                mDialog.show();
            }
       }
    }

    @Override
    public void onCompleted() {
        if (mDialog!=null && mDialog.isShowing()){
            mDialog.dismiss();
        }

    }

    @Override
    public void onError(Throwable e) {
        ApiErrorHelper.handleCommonError(mContext, e);
        if (mDialog!=null && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    @Override
    public void onNext(T t) {
        if (mDialog!=null && mDialog.isShowing()){
            mDialog.dismiss();
        }

    }
}
