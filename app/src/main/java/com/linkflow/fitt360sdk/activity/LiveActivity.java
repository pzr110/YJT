package com.linkflow.fitt360sdk.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.dialog.RTMPStreamerDialog;
import com.linkflow.fitt360sdk.helper.TimeUtils;
import com.linkflow.fitt360sdk.helper.TimerHelper;
import com.linkflow.fitt360sdk.service.RTMPStreamService;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DecimalFormat;
import java.util.Timer;

import app.library.linkflow.manager.StoredDataManager;
import app.library.linkflow.manager.item.BaseExtendSetItem;
import app.library.linkflow.manager.item.BaseSetItem;
import app.library.linkflow.manager.item.RecordSetItem;
import app.library.linkflow.manager.item.StatusItem;
import app.library.linkflow.manager.item.VolumeItem;
import app.library.linkflow.manager.model.InfoModel;
import app.library.linkflow.manager.neckband.ConnectStateManage;
import app.library.linkflow.manager.neckband.NeckbandManager;
import app.library.linkflow.manager.neckband.SetManage;
import app.library.linkflow.manager.neckband.SettingListener;
import app.library.linkflow.rtmp.RTSPToRTMPConverter;

public class LiveActivity extends BaseActivity implements RTSPToRTMPConverter.Listener,
        SetManage.Listener, SettingListener {
    private TextView mTvPush;
    private RTMPStreamerDialog mRTMPStreamerDialog;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.#");

    private AVLoadingIndicatorView mAviLoading;
    private TextView mTvBack;
    private TextView mLiveFpsView;
    //    private Chronometer mTvLiveTime;
    private Spinner mSpResolution;
    private Spinner mSpUpload;
    private Spinner mSpFps;
    private ImageView mImgMainBtn;
    private ImageView mImgRedDot;
    private TextView mTvMine;
    private TextView mTvTime;


    private boolean isPushStream = false;

    private RTSPToRTMPConverter mRSToRMConverter;

    private long mStreamingClickedTime;
    private long mRecordTime = 0;

    private RTSPToRTMPConverter mConverter;

    private RTMPStreamService mService;


    private int mVideoWidth = 3840;
    private int mVideoHeight = 1920;
    private int mBitRate = 30;
    private int mFps = 30;

    protected SetManage mSetManage;

    private boolean listenerEnable = true;
    private TimeUtils mTimeUtils;
    private ProgressBar mProgressBarPower;
    private TextView mTvPower;

    private boolean isTimer = false;

    private ImageView mIvAlbum;
    private ImageView mIvUser;
    public static final String ACTION_START_RTMP = "start_rtmp", ACTION_STOP_RTMP = "stop_rtmp";

    private Switch mSwitchAuto;

    private boolean isAutoBit = false;


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(ACTION_STOP_RTMP)) {
                    if (intent.getIntExtra("close", -1) == 10) {
//                        mAdapter.changeStreamingState(false);
                        mTimeUtils.stop();
                    }
                } else if (action.equals(ACTION_START_RTMP)) {
//                    mAdapter.changeStreamingState(true);
                    mTimeUtils.start();
                }
            }
        }
    };


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
        mTimeUtils = new TimeUtils(callBack, 1000);

        mSetManage = mNeckbandManager.getSetManage();
        mSetManage.setListener(this);
        mSetManage.getRecordSetModel().setListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_START_RTMP);
        intentFilter.addAction(ACTION_STOP_RTMP);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFilter);

        initViewId();

        initListener();

        getPower();
        Log.e("TAGTAG", "Status" + mConverter.getSentByteAmount());




    }

    private void getPower() {

    }

    @Override
    public void onConnectState(ConnectStateManage.STATE state) {
        super.onConnectState(state);
        if (state == ConnectStateManage.STATE.STATE_DONE) {
            mNeckbandManager.getRecordModel(this).getRecordState(mNeckbandManager.getAccessToken());
            mBatteryAndStorageChecker.setListener(new BatteryAndStorageCheckerListener() {
                @Override
                public void takeBatteryAndStorageLevel() {
                    mNeckbandManager.getInfoManage().getInfoModel().getStatus(mNeckbandManager.getAccessToken(), new InfoModel.StatusListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void completedGetStatus(boolean success, StatusItem statusItem, VolumeItem volumeItem) {
                            if (success) {

                                mBeforeBatteryLevel = statusItem.mBatteryLevel;
                                Log.e("power", "power:" + mBeforeBatteryLevel);

                                mProgressBarPower.setProgress(mBeforeBatteryLevel);
                                mTvPower.setText(mBeforeBatteryLevel + "%");
//                                Toast.makeText(LiveActivity.this, "仅剩" + mBeforeBatteryLevel + "% 电量", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else if (state == ConnectStateManage.STATE.STATE_NONE) {
            mNeckbandManager.setRecordState(false);
        }
    }

    private TimeUtils.UpdateUiCallBack callBack = new TimeUtils.UpdateUiCallBack() {
        @Override
        public void updateUI(String text) {
//            updateText(text);
            mTvTime.setText(text);
            Log.e("TAGTAG", "Timeformat" + text);
            mLiveFpsView.setText(mDecimalFormat.format((mConverter.getSentByteAmount() / 1000000f) * 8));
        }
    };


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

        if (isTimer) {
            mTimeUtils.stop();
        }

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
            mSpFps.setEnabled(false);
            mSpResolution.setEnabled(false);
            mSpUpload.setEnabled(false);
            mImgMainBtn.setBackgroundResource(R.drawable.shape_bg_gray_circle);
            mImgRedDot.setBackgroundResource(R.drawable.shape_accent_white_circle);
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

    @SuppressLint("SetTextI18n")
    private void initViewId() {
        mAviLoading = findViewById(R.id.avi_loading);
        mTvBack = findViewById(R.id.tv_back);
        mLiveFpsView = findViewById(R.id.live_fps_view);
//        mTvLiveTime = findViewById(R.id.tv_live_time);
        mSpResolution = findViewById(R.id.sp_resolution);
        mSpUpload = findViewById(R.id.sp_upload);
        mSpFps = findViewById(R.id.sp_fps);
        mImgMainBtn = findViewById(R.id.img_main_btn);
        mImgRedDot = findViewById(R.id.img_red_dot);

        mTvTime = findViewById(R.id.tv_time);

        mProgressBarPower = findViewById(R.id.progressBar_power);
        mTvPower = findViewById(R.id.tv_power);

        mProgressBarPower.setMax(100);
        mProgressBarPower.setProgress(100);
        mTvPower.setText(100 + "%");


//        mTvMine = findViewById(R.id.tv_mine);
        mIvAlbum = findViewById(R.id.iv_album);
        mIvUser = findViewById(R.id.iv_user);
        mImgMainBtn.bringToFront();
        mImgRedDot.bringToFront();
        mImgMainBtn.setOnClickListener(this::onClick);
        mTvBack.setOnClickListener(this::onClick);
        mIvAlbum.setOnClickListener(this::onClick);
        mIvUser.setOnClickListener(this::onClick);


        mSwitchAuto = findViewById(R.id.switch_auto);

        mSwitchAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAutoBit = true;
                    mSpUpload.setEnabled(false);
                } else {
                    isAutoBit = false;
                }
            }
        });


    }

    private void startStream() {
        listenerEnable = false;
//        mTimeUtils.start();
        isTimer = true;

        String deviceRtmpUrl = SPUtils.getInstance().getString("deviceRtmpUrl", "rtmp://192.168.0.250:1935/ccmc/stream1");

        Log.e("TAGLIVE", "URL:" + deviceRtmpUrl);
//        mNeckbandManager.getNotifyManage().getNotifyModel().agreementTemperLimit(mNeckbandManager.getAccessToken(), true, this);
        Intent intent = new Intent(LiveActivity.this, RTMPStreamService.class);
        intent.setAction(RTMPStreamService.ACTION_START_RTMP_STREAM);
        intent.putExtra("rtmp_url", deviceRtmpUrl);
        intent.putExtra("rtmp_bitrate_auto", isAutoBit);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("TAGPZR", "HEREBBB");
            startForegroundService(intent);
        } else {
            Log.e("TAGPZR", "HERECCC");
            startService(intent);
        }

//        if (mRecordTime != 0) {
//            mTvLiveTime.setBase(mTvLiveTime.getBase() + (SystemClock.elapsedRealtime() - mRecordTime));
//        } else {
//            mTvLiveTime.setBase(SystemClock.elapsedRealtime());
//            int hour = (int) ((SystemClock.elapsedRealtime() - mTvLiveTime.getBase()) / 1000 / 60);
//            mTvLiveTime.setFormat("0" + String.valueOf(hour) + ":%s");
//        }
//        mTvLiveTime.start();

        Log.e("TAGTAG", "Status" + mConverter.getSentByteAmount());


    }

    private void stopStream() {

//        mTimeUtils.stop();

        Intent intent = new Intent(LiveActivity.this, RTMPStreamService.class);
        intent.setAction(RTMPStreamService.ACTION_CANCEL_RTMP_STREAM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        mRSToRMConverter.exit();
//        mTvLiveTime.stop();
        mRecordTime = SystemClock.elapsedRealtime();

    }

    private void changeLiveEnable(boolean isPushStream) {
        if (isPushStream) {
            // 推流中
            mImgMainBtn.setBackgroundResource(R.drawable.shape_bg_white_circle);
            mImgRedDot.setBackgroundResource(R.drawable.shape_center_red_circle);
//            mTvBack.setEnabled(false);
            mSpFps.setEnabled(false);
            mSpResolution.setEnabled(false);
            mSpUpload.setEnabled(false);
            mIvAlbum.setEnabled(false);
            mIvUser.setEnabled(false);

            listenerEnable = false;
        } else {
            // 停止了推流
            listenerEnable = true;
            mIvAlbum.setEnabled(true);
            mIvUser.setEnabled(true);
//            mTvBack.setEnabled(true);
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
                if (listenerEnable) {
                    finish();
                } else {
                    ToastUtils.showShort(R.string.str_stop_live);
                }
                break;
            }
            case R.id.iv_album: {
                ActivityUtils.startActivity(AlbumActivity.class);
                break;
            }
            case R.id.iv_user: {
                ActivityUtils.startActivity(UserActivity.class);

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
    public void completedCallSetApi(boolean success, boolean isSet) {
        if (isSet) {
            Toast.makeText(this, success ? R.string.applied : R.string.applied_fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void completedSetBaseSetting(boolean success, BaseSetItem item) { // 默认设置
        if (success && item instanceof RecordSetItem) {
            StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_RECORD_WIDTH, mVideoWidth);
            StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_RECORD_HEIGHT, mVideoHeight);
            StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_BITRATE, ((RecordSetItem) item).mBitrate);
            StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_FPS, ((RecordSetItem) item).mFPS);
        }
    }

    @Override
    public void completedSetExtendSetting(boolean b, BaseExtendSetItem baseExtendSetItem) {

    }


}
