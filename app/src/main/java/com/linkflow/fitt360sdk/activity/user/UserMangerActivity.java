package com.linkflow.fitt360sdk.activity.user;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.linkflow.cpe.net.Api;
import com.linkflow.cpe.net.BaseSubscriber;
import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.activity.MainActivity;
import com.linkflow.fitt360sdk.item.BaseBean;
import com.linkflow.fitt360sdk.item.RtmpBean;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserMangerActivity extends AppCompatActivity {
    private TextView mTvUserName;
    private TextView mTvDevice;
    private TextView mTvRtmp;
    private ImageView mTvBack;
    private TextView mTvDeviceSn;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manger);

        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);
        mTvUserName = findViewById(R.id.tv_user_name);
        mTvDevice = findViewById(R.id.tv_device);
        mTvRtmp = findViewById(R.id.tv_rtmp);
        mTvBack = findViewById(R.id.tv_back);
        mTvDeviceSn = findViewById(R.id.tv_device_sn);

        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getRtmpUrl();
//        getInfo();
    }

    private void getRtmpUrl() {
        HashMap<String, Object> params = new HashMap<>();
        Api.getRetrofit().postRtmpUrl(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean<RtmpBean>>(this) {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.e("TAG", "onStart");
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Log.e("TAG", "onCompleted");
                    }


                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.e("TAGASDSA", "onErrorAS" + e.getMessage());
                    }

                    @Override
                    public void onNext(BaseBean<RtmpBean> bean) {
                        super.onNext(bean);
                        Log.e("TAG", "onNext");
                        String title = bean.getData().getTitle();
                        String stream_url = bean.getData().getStream_url();
                        Log.e("URL", "URL" + stream_url);
                        SPUtils.getInstance().put("deviceTitle", title);
                        SPUtils.getInstance().put("deviceRtmpUrl", stream_url);

                        getInfo(title,stream_url);
                    }
                });
    }

    private void getInfo(String title, String stream_url) {
//        String deviceRtmpUrl = SPUtils.getInstance().getString("deviceRtmpUrl", "rtmp://192.168.0.32:1935/ccmc/stream2");
//        String deviceTitle = SPUtils.getInstance().getString("deviceTitle");
        String account = SPUtils.getInstance().getString("account");
        String deviceSN = SPUtils.getInstance().getString("deviceSN");

        mTvUserName.setText(account);
        mTvDevice.setText(title);
        mTvRtmp.setText(stream_url);
        mTvDeviceSn.setText(deviceSN);
    }
}
