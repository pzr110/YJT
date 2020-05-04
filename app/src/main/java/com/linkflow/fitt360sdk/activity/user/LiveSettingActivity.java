package com.linkflow.fitt360sdk.activity.user;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.activity.BaseActivity;

import app.library.linkflow.manager.item.BaseExtendSetItem;
import app.library.linkflow.manager.item.BaseSetItem;
import app.library.linkflow.manager.neckband.SetManage;
import app.library.linkflow.manager.neckband.SettingListener;
import app.library.linkflow.rtmp.RTSPToRTMPConverter;

public class LiveSettingActivity extends BaseActivity implements RTSPToRTMPConverter.Listener,
        SetManage.Listener, SettingListener {

    private Spinner mSpResolution;
    private Spinner mSpUpload;
    private Spinner mSpFps;
    private ImageView mTvBack;


    private int mVideoWidth = 3840;
    private int mVideoHeight = 1920;
    private int mBitRate = 30;
    private int mFps = 30;

    protected SetManage mSetManage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderView(R.layout.activity_base_head);
        setBodyView(R.layout.activity_live_setting);
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);

        mSetManage = mNeckbandManager.getSetManage();
        mSetManage.setListener(this);
        mSetManage.getRecordSetModel().setListener(this);

        initViewId();

    }

    private void initViewId() {
        mTvBack = findViewById(R.id.tv_back);
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSpResolution = findViewById(R.id.sp_resolution);
        mSpUpload = findViewById(R.id.sp_upload);
        mSpFps = findViewById(R.id.sp_fps);

        boolean connected = mNeckbandManager.getConnectStateManage().isConnected();
        if (connected) {
            mSpFps.setEnabled(true);
            mSpResolution.setEnabled(true);
            mSpUpload.setEnabled(true);
        } else {
            mSpFps.setEnabled(false);
            mSpResolution.setEnabled(false);
            mSpUpload.setEnabled(false);

        }

        mSpResolution.setSelection(0, true); // 禁止默认自动调用一次
        mSpUpload.setSelection(0, true); // 禁止默认自动调用一次
        mSpFps.setSelection(0, true); // 禁止默认自动调用一次


        mSpResolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] resolution = getResources().getStringArray(R.array.resolution);
                String resolu = resolution[position];
                String[] xes = resolu.split("x");
                String x = xes[0];
                String s = xes[1];
                mVideoWidth = Integer.parseInt(x);
                mVideoHeight = Integer.parseInt(s);

//                String[] resolution1 = item.mTitle.split("x");
                mSetManage.getRecordSetItem().setResolution(mVideoWidth, mVideoHeight);
                mSetManage.getRecordSetModel().setBaseSettings(mNeckbandManager.getAccessToken(), true, mSetManage.getRecordSetItem());

                Log.e("TAGGA", "H" + mVideoWidth + "X" + mVideoHeight);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mSpUpload.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] upload = getResources().getStringArray(R.array.upload);
                String upl = upload[pos];
                String[] xes = upl.split("M");
                String x = xes[0];
                mBitRate = Integer.parseInt(x);

                mSetManage.getRecordSetModel().setBaseSettings(mNeckbandManager.getAccessToken(), true, mSetManage.getRecordSetItem());
                mSetManage.setBitrate(mNeckbandManager.getAccessToken(), mBitRate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
                ToastUtils.showShort("ASSDD");
            }
        });


        mSpFps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] fps = getResources().getStringArray(R.array.fps);
                String fps1 = fps[pos];
                String[] xes = fps1.split("F");
                String x = xes[0];
                mFps = Integer.parseInt(x);

                mSetManage.getRecordSetModel().setBaseSettings(mNeckbandManager.getAccessToken(), true, mSetManage.getRecordSetItem());
                mSetManage.getTimeLapseModel().setRate(mNeckbandManager.getAccessToken(), mFps);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
                ToastUtils.showShort("ASSDD");
            }
        });

    }

    @Override
    public void completedCallSetApi(boolean success, boolean isSet) {
        if (isSet) {
            Toast.makeText(this, success ? R.string.applied : R.string.applied_fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void completedSetBaseSetting(boolean b, BaseSetItem baseSetItem) {

    }

    @Override
    public void completedSetExtendSetting(boolean b, BaseExtendSetItem baseExtendSetItem) {

    }

    @Override
    public void onFrame(boolean b) {

    }

    @Override
    public void onStatusChange(RTSPToRTMPConverter.RTMP_STATUS rtmp_status) {

    }

    @Override
    public void onError(RTSPToRTMPConverter.ErrorCode errorCode) {

    }
}
