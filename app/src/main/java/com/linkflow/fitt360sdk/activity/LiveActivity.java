package com.linkflow.fitt360sdk.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alivc.rtc.AliRtcAuthInfo;
import com.alivc.rtc.AliRtcEngine;
import com.alivc.rtc.AliRtcEngineEventListener;
import com.alivc.rtc.AliRtcEngineNotify;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.linkflow.cpe.App;
import com.linkflow.cpe.bean.TokenBean;
import com.linkflow.cpe.net.Api;
import com.linkflow.cpe.net.BaseSubscriber;
import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.dialog.RTMPStreamerDialog;
import com.linkflow.fitt360sdk.helper.TimeUtils;
import com.linkflow.fitt360sdk.helper.TimerHelper;
import com.linkflow.fitt360sdk.item.BaseBean;
import com.linkflow.fitt360sdk.item.RtmpBean;
import com.linkflow.fitt360sdk.service.RTMPStreamService;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DecimalFormat;
import java.util.HashMap;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.xuexiang.xupdate.XUpdate.getContext;
import static org.webrtc.alirtcInterface.ErrorCodeEnum.ERR_ICE_CONNECTION_HEARTBEAT_TIMEOUT;
import static org.webrtc.alirtcInterface.ErrorCodeEnum.ERR_SESSION_REMOVED;

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
    private Switch mSwitchVoice;


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
    private String mStream_url;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int PERMISSION_REQ_ID = 0x0002;

    /**
     * SDK提供的对音视频通话处理的引擎类
     */
    private AliRtcEngine mAliRtcEngine;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(ACTION_STOP_RTMP)) {
                    if (intent.getIntExtra("close", -1) == 10) {
//                        mAdapter.changeStreamingState(false);
                        if (mTimeUtils != null) {
                            mTimeUtils.stop();
                        }
                        changeLiveEnable(false);
                    }
                } else if (action.equals(ACTION_START_RTMP)) {
//                    mAdapter.changeStreamingState(true);
                    mTimeUtils.start();
                    changeLiveEnable(true);

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

        getRtmpUrl();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_START_RTMP);
        intentFilter.addAction(ACTION_STOP_RTMP);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFilter);

        initViewId();


        getPower();
        Log.e("TAGTAG", "Status" + mConverter.getSentByteAmount());


    }

    private void getRtmpUrl() {
        Log.e("TAGGYURL", "URL:" + App.BaseUrl);
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
                        mStream_url = bean.getData().getStream_url();
                        Log.e("PZRURL", "URL" + mStream_url);

                        initListener();

                    }
                });
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

        if (mAliRtcEngine != null) {
            mAliRtcEngine.destroy();
        }

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
        if (connected && mStream_url != null) {
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
                    mSpUpload.setEnabled(true);
                }
            }
        });

        mSwitchVoice = findViewById(R.id.switch_voice);

        mSwitchVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ToastUtils.showShort("开始语音");

                    if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                            checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                            checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
                        // 初始化引擎
                        initRTCEngine();
                    }

                } else {
                    ToastUtils.showShort("结束");
                    if (mAliRtcEngine != null) {
                        mAliRtcEngine.destroy();
                        mAliRtcEngine = null;
                    }
                }
            }
        });

    }

    private void getVoiceToken() {

        @SuppressLint("HardwareIds") String device_sn = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Log.e("TAGPZR", "device_sn"+device_sn);

        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", device_sn);
//        params.put("type", "publisher");
        Api.getRetrofit().getToken(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<TokenBean>(this) {

                    @Override
                    public void onStart() {
                        super.onStart();
//                        recyclerView.refreshComplete();
                        Log.e("TAGPZR", "onStart");
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
//                        recyclerView.refreshComplete();
                        Log.e("TAGPZR", "onCompleted");

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
//                        showRec(false);
//                        recyclerView.refreshComplete();
                        Log.e("TAGPZR", "onError" + e.toString());

                    }

                    @Override
                    public void onNext(TokenBean bean) {
                        super.onNext(bean);


                        String appid = bean.getData().getAppid();
                        String nonce = bean.getData().getNonce();
                        String gslb = bean.getData().getGslb().get(0).toString();
                        int timestamp = bean.getData().getTimestamp();
                        String token = bean.getData().getToken();
                        String channel = bean.getData().getChannel();
                        String userid = bean.getData().getUserid();

//                        ToastUtils.showLong(channel);

                        joinChannel(appid, nonce, gslb, timestamp, token, channel, userid);
                    }
                });

    }

    private void joinChannel(String appid, String nonce, String gslb, int timestamp, String token, String channel, String userid) {
        if (mAliRtcEngine == null) {
            return;
        }
        //从控制台生成的鉴权信息，具体内容请查阅:https://help.aliyun.com/document_detail/146833.html
        AliRtcAuthInfo userInfo = new AliRtcAuthInfo();
        userInfo.setAppid(appid);
        userInfo.setNonce(nonce);
        userInfo.setGslb(new String[]{gslb});
        userInfo.setTimestamp(timestamp);
        userInfo.setToken(token);
        userInfo.setConferenceId(channel);///
        userInfo.setUserId(userid);
        /*
         *设置自动发布和订阅，只能在joinChannel之前设置
         *参数1    true表示自动发布；false表示手动发布
         *参数2    true表示自动订阅；false表示手动订阅
         */
        mAliRtcEngine.setAutoPublishSubscribe(true, true);
        mAliRtcEngine.setAudioOnlyMode(true);// 纯音频
        // 加入频道，参数1:鉴权信息 参数2:用户名
        mAliRtcEngine.joinChannel(userInfo, userid);/// 用户名设置

    }


    private void initRTCEngine() {
        //默认不开启兼容H5
        AliRtcEngine.setH5CompatibleMode(0);
        // 防止初始化过多
        if (mAliRtcEngine == null) {
            //实例化,必须在主线程进行。
            mAliRtcEngine = AliRtcEngine.getInstance(getApplicationContext());
            //设置事件的回调监听
            mAliRtcEngine.setRtcEngineEventListener(mEventListener);
            //设置接受通知事件的回调
            mAliRtcEngine.setRtcEngineNotify(mEngineNotify);
            // 初始化本地视图
//            initLocalView();
            //开启预览
//            startPreview();

            //加入频道
            getVoiceToken();


        }
    }

    /**
     * 用户操作回调监听(回调接口都在子线程)
     */
    private AliRtcEngineEventListener mEventListener = new AliRtcEngineEventListener() {

        /**
         * 加入房间的回调
         * @param result 结果码
         */
        @Override
        public void onJoinChannelResult(int result) {
            runOnUiThread(() -> {
                if (result == 0) {
                    showToast("加入频道成功");
                } else {
                    showToast("加入频道失败 错误码: " + result);
                }
            });
        }

        /**
         * 订阅成功的回调
         * @param s userid
         * @param i 结果码
         * @param aliRtcVideoTrack 视频的track
         * @param aliRtcAudioTrack 音频的track
         */
        @Override
        public void onSubscribeResult(String s, int i, AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack,
                                      AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack) {
            if (i == 0) {
//                updateRemoteDisplay(s, aliRtcAudioTrack, aliRtcVideoTrack);
            }
        }


        /**
         * 取消的回调
         * @param i 结果码
         * @param s userid
         */
        @Override
        public void onUnsubscribeResult(int i, String s) {
//            updateRemoteDisplay(s, AliRtcAudioTrackNo, AliRtcVideoTrackNo);
        }

        /**
         * 出现错误的回调
         * @param error 错误码
         */
        @Override
        public void onOccurError(int error) {
            //错误处理
            processOccurError(error);
        }
    };

    /**
     * 特殊错误码回调的处理方法
     *
     * @param error 错误码
     */
    private void processOccurError(int error) {
        switch (error) {
            case ERR_ICE_CONNECTION_HEARTBEAT_TIMEOUT:
            case ERR_SESSION_REMOVED:
                noSessionExit(error);
                break;
            default:
                break;
        }
    }

    /**
     * 错误处理
     *
     * @param error 错误码
     */
    private void noSessionExit(int error) {
        runOnUiThread(() -> new AlertDialog.Builder(LiveActivity.this)
                .setTitle("ErrorCode : " + error)
                .setMessage("发生错误，请退出房间")
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    onBackPressed();
                })
                .create()
                .show());
    }

    /**
     * SDK事件通知(回调接口都在子线程)
     */
    private AliRtcEngineNotify mEngineNotify = new AliRtcEngineNotify() {
        /**
         * 远端用户停止发布通知，处于OB（observer）状态
         * @param aliRtcEngine 核心引擎对象
         * @param s userid
         */
        @Override
        public void onRemoteUserUnPublish(AliRtcEngine aliRtcEngine, String s) {
//            updateRemoteDisplay(s, AliRtcAudioTrackNo, AliRtcVideoTrackNo);
        }

        /**
         * 远端用户上线通知
         * @param s userid
         */
        @Override
        public void onRemoteUserOnLineNotify(String s) {
//            addRemoteUser(s);
        }

        /**
         * 远端用户下线通知
         * @param s userid
         */
        @Override
        public void onRemoteUserOffLineNotify(String s) {
//            removeRemoteUser(s);
        }

        /**
         * 远端用户发布音视频流变化通知
         * @param s userid
         * @param aliRtcAudioTrack 音频流
         * @param aliRtcVideoTrack 相机流
         */
        @Override
        public void onRemoteTrackAvailableNotify(String s, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack,
                                                 AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack) {
//            updateRemoteDisplay(s, aliRtcAudioTrack, aliRtcVideoTrack);
        }
    };


    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }
            initRTCEngine();
        }
    }

    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void startStream() {
        listenerEnable = false;
        isTimer = true;
        mNeckbandManager.getNotifyManage().getNotifyModel().agreementTemperLimit(mNeckbandManager.getAccessToken(), true, this);

        Log.e("TAGLIVE", "URL:" + mStream_url);
//        mNeckbandManager.getNotifyManage().getNotifyModel().agreementTemperLimit(mNeckbandManager.getAccessToken(), true, this);
        Intent intent = new Intent(LiveActivity.this, RTMPStreamService.class);
        intent.setAction(RTMPStreamService.ACTION_START_RTMP_STREAM);
        intent.putExtra("rtmp_url", mStream_url);
        intent.putExtra("rtmp_bitrate_auto", isAutoBit);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("TAGPZR", "HEREBBB");
            startForegroundService(intent);
        } else {
            Log.e("TAGPZR", "HERECCC");
            startService(intent);
        }


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
                    if (!mRSToRMConverter.isRTMPWorking()) {
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
            case R.id.base_dialog_agree: {
                mNeckbandManager.getNotifyManage().getNotifyModel().agreementTemperLimit(mNeckbandManager.getAccessToken(), true, this);
                Log.e("TAGAgree", "Agree");
                break;
            }
            case R.id.base_dialog_disagree: {
                mTemperLimitAlertDialog.dismiss();
                if (mNeckbandManager.isRecording()) {
                    mNeckbandManager.getRecordModel().actionRecord(mNeckbandManager.getAccessToken(), false);
                    mNeckbandManager.setRecordState(false);
                }
                mTemperLimitAlertDialog.dismiss();
                Log.e("TAGAgree", "disAgree");

                break;
            }
        }


//        if (view.getId() == R.id.base_dialog_agree) {
//            startStream();
//            mRTMPStreamerDialog.dismissAllowingStateLoss();
//        } else if (view.getId() == R.id.base_dialog_disagree) {
//            mRTMPStreamerDialog.dismissAllowingStateLoss();
//        }
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
            StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_RECORD_WIDTH, 1920);
            StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_RECORD_HEIGHT, 1080);
            StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_BITRATE, 5);
            StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_FPS, 24);
        }
    }

    @Override
    public void completedSetExtendSetting(boolean b, BaseExtendSetItem baseExtendSetItem) {

    }


}
