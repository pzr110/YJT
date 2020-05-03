package com.linkflow.fitt360sdk.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.dialog.RTMPStreamerDialog;
import com.linkflow.fitt360sdk.helper.TimerHelper;
import com.linkflow.fitt360sdk.service.RTMPStreamService;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DecimalFormat;

import app.library.linkflow.rtmp.RTSPToRTMPConverter;

public class LiveActivity extends BaseActivity implements RTSPToRTMPConverter.Listener, TimerHelper.Listener {
    private TextView mTvPush;
    private RTMPStreamerDialog mRTMPStreamerDialog;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.#");

    private AVLoadingIndicatorView mAviLoading;
    private TextView mTvBack;
    private TextView mLiveFpsView;
    private Chronometer mTvLiveTime;
    private Spinner mSpResolution;
    private Spinner mSpUpload;
    private Spinner mSpFps;
    private ImageView mImgMainBtn;
    private ImageView mImgRedDot;
    private TextView mTvMine;


    private boolean isPushStream = false;

    private RTSPToRTMPConverter mRSToRMConverter;

    private long mStreamingClickedTime;
    private long mRecordTime = 0;

    private RTSPToRTMPConverter mConverter;

    private RTMPStreamService mService;
    private TimerHelper mTimerHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
//        mTvPush = findViewById(R.id.tv_push);
        mRTMPStreamerDialog = new RTMPStreamerDialog();
        mRTMPStreamerDialog.setClickListener(this);

        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);

        mRSToRMConverter = RTSPToRTMPConverter.getInstance();
        mConverter = RTSPToRTMPConverter.getInstance().init(getMainLooper(), this);
        mTimerHelper = TimerHelper.getInstance().init(getMainLooper(), this);
        initViewId();

        initListener();
        mTimerHelper.start();
        Log.e("TAGTAG", "Status" + mConverter.getSentByteAmount());

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mNeckbandManager.getConnectStateManage().isConnected()) {
            mRSToRMConverter = RTSPToRTMPConverter.getInstance();
            mRSToRMConverter.getSentByteAmount2();
            if (mRSToRMConverter.isRTMPWorking()) {

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimerHelper.stop();

        if (mRSToRMConverter != null) {
            if (mRSToRMConverter.isRTMPWorking()) {
                mRSToRMConverter.stop();
            }
        }
//        mNeckbandManager.getPreviewModel().activateRTSP(mNeckbandManager.getAccessToken(), false);
        if (mNeckbandManager.isRecording()) {
            mNeckbandManager.getRecordModel().actionRecord(mNeckbandManager.getAccessToken(), false);
            mNeckbandManager.setRecordState(false);
        }
    }

    private void initListener() {
        boolean connected = mNeckbandManager.getConnectStateManage().isConnected();
        if (connected) {
            mImgMainBtn.setEnabled(true);
            mImgMainBtn.setBackgroundResource(R.drawable.shape_circle_gradient);
            mImgRedDot.setBackgroundResource(R.drawable.shape_accent_white_circle);
        } else {
            mImgMainBtn.setEnabled(false);
            mImgMainBtn.setBackgroundResource(R.drawable.shape_bg_gray_circle);
            mImgRedDot.setBackgroundResource(R.drawable.shape_accent_white_circle);
        }


    }

    private void initViewId() {
        mAviLoading = findViewById(R.id.avi_loading);
        mTvBack = findViewById(R.id.tv_back);
        mLiveFpsView = findViewById(R.id.live_fps_view);
        mTvLiveTime = findViewById(R.id.tv_live_time);
        mSpResolution = findViewById(R.id.sp_resolution);
        mSpUpload = findViewById(R.id.sp_upload);
        mSpFps = findViewById(R.id.sp_fps);
        mImgMainBtn = findViewById(R.id.img_main_btn);
        mImgRedDot = findViewById(R.id.img_red_dot);


//        mTvMine = findViewById(R.id.tv_mine);

        mImgMainBtn.bringToFront();
        mImgRedDot.bringToFront();
        mImgMainBtn.setOnClickListener(this::onClick);
        mTvBack.setOnClickListener(this::onClick);
    }

    private void startStream() {
        mNeckbandManager.getNotifyManage().getNotifyModel().agreementTemperLimit(mNeckbandManager.getAccessToken(), true, this);
        Intent intent = new Intent(LiveActivity.this, RTMPStreamService.class);
        intent.setAction(RTMPStreamService.ACTION_START_RTMP_STREAM);
        intent.putExtra("rtmp_url", "rtmp://192.168.0.250:1935/ccmc/stream1");
        intent.putExtra("rtmp_bitrate_auto", false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("TAGPZR", "HEREBBB");
            startForegroundService(intent);
        } else {
            Log.e("TAGPZR", "HERECCC");
            startService(intent);
        }

        if (mRecordTime != 0) {
            mTvLiveTime.setBase(mTvLiveTime.getBase() + (SystemClock.elapsedRealtime() - mRecordTime));
        } else {
            mTvLiveTime.setBase(SystemClock.elapsedRealtime());
            int hour = (int) ((SystemClock.elapsedRealtime() - mTvLiveTime.getBase()) / 1000 / 60);
            mTvLiveTime.setFormat("0" + String.valueOf(hour) + ":%s");
        }
        mTvLiveTime.start();

        Log.e("TAGTAG", "Status" + mConverter.getSentByteAmount());


    }

    private void stopStream() {
        Intent intent = new Intent(LiveActivity.this, RTMPStreamService.class);
        intent.setAction(RTMPStreamService.ACTION_CANCEL_RTMP_STREAM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        mRSToRMConverter.exit();
        mTvLiveTime.stop();
        mRecordTime = SystemClock.elapsedRealtime();
    }

    private void changeLiveEnable(boolean isPushStream) {
        if (isPushStream) {
            // 推流中
            mImgMainBtn.setBackgroundResource(R.drawable.shape_bg_white_circle);
            mImgRedDot.setBackgroundResource(R.drawable.shape_center_red_circle);
            mTvBack.setEnabled(false);
            mSpFps.setEnabled(false);
            mSpResolution.setEnabled(false);
            mSpUpload.setEnabled(false);

        } else {
            // 停止了推流
            mTvBack.setEnabled(true);
            mSpFps.setEnabled(true);
            mSpResolution.setEnabled(true);
            mSpUpload.setEnabled(true);
            mImgMainBtn.setBackgroundResource(R.drawable.shape_circle_gradient);
            mImgRedDot.setBackgroundResource(R.drawable.shape_accent_white_circle);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.img_main_btn: {
                if (System.currentTimeMillis() - mStreamingClickedTime > 1500) {
                    mStreamingClickedTime = System.currentTimeMillis();
                    if (!isPushStream) {
                        startStream();
                        isPushStream = !isPushStream;
                        changeLiveEnable(isPushStream);
                    } else {
                        stopStream();
                        isPushStream = !isPushStream;
                        changeLiveEnable(isPushStream);
                    }
                } else {
                    ToastUtils.showShort("请不要频繁切换直播状态");
                }

                break;
            }
            case R.id.tv_back: {
                finish();
                break;
            }
        }


        if (view.getId() == R.id.base_dialog_agree) {
            startStream();
            mRTMPStreamerDialog.dismissAllowingStateLoss();
        } else if (view.getId() == R.id.base_dialog_disagree) {
            mRTMPStreamerDialog.dismissAllowingStateLoss();
        }
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

    @Override
    public void updateTime(String time) {
//        Log.e("TAGTAG", "Timeformat" + mConverter.getSentByteAmount());
        mLiveFpsView.setText(mDecimalFormat.format((mConverter.getSentByteAmount() / 1000000f) * 8));
    }
}
