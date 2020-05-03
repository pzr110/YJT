package com.linkflow.fitt360sdk.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.adapter.BTDeviceRecyclerAdapter;
import com.linkflow.fitt360sdk.adapter.MainRecyclerAdapter;
import com.linkflow.fitt360sdk.dialog.RTMPStreamerDialog;
import com.linkflow.fitt360sdk.item.BTItem;
import com.linkflow.fitt360sdk.item.BaseBean;
import com.linkflow.fitt360sdk.item.RtmpBean;
import com.linkflow.fitt360sdk.service.RTMPStreamService;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.HashMap;

import app.library.linkflow.ConnectManager;
import app.library.linkflow.connect.BTConnectHelper;
import app.library.linkflow.connect.ConnectHelper;
import app.library.linkflow.connect.WifiConnectHelper;
import app.library.linkflow.manager.NeckbandRestApiClient;
import app.library.linkflow.manager.model.PhotoModel;
import app.library.linkflow.manager.model.RecordModel;
import app.library.linkflow.manager.neckband.ConnectStateManage;
import app.library.linkflow.rtmp.RTSPToRTMPConverter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.linkflow.fitt360sdk.adapter.MainRecyclerAdapter.ID.ID_GALLERY;
import static com.linkflow.fitt360sdk.adapter.MainRecyclerAdapter.ID.ID_SETTING;
import static com.xuexiang.xupdate.XUpdate.getContext;

public class MainActivity extends BaseActivity implements MainRecyclerAdapter.ItemClickListener,
        PhotoModel.Listener, BTDeviceRecyclerAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener,
        BTConnectHelper.Listener, ConnectManager.Listener {

    public static final String ACTION_START_RTMP = "start_rtmp", ACTION_STOP_RTMP = "stop_rtmp";
    private static final int PERMISSION_CALLBACK = 366;

    private RTSPToRTMPConverter mRSToRMConverter;
    private RecordModel mRecordModel;
    private PhotoModel mPhotoModel;

    private MainRecyclerAdapter mAdapter;

    private long mStartedRecordTime;
    private long mStreamingClickedTime;
    private RTMPStreamerDialog mRTMPStreamerDialog;

    //////////////////////////////////////
    public static final int CALLED_BY_START_ACTIVITY = 1001, CALLED_BY_SETTING_ACTIVITY = 1002;
    private static final int MSG_P2P_STATE_DISABLED = 10;
    public static final String KEY_CALLED_BY = "calledBy";
    public static final String ACTION_BT_CONNECTED = "linkflow.app.fitt360.BT_CONNECTED";
    private int mCalledBy;

    private SwipeRefreshLayout mRefreshLayout;
    private AVLoadingIndicatorView mProgressBar;
    private BTDeviceRecyclerAdapter mAdapterConnect;
    private BluetoothDevice mSelectedBTDevice;

    private ConnectHelper mConnectHelper;
    private BTConnectHelper mBTConnectHelper;
    private WifiConnectHelper mWifiConnectHelper;
    private boolean mCanceledBTDiscovery;

    private int mBeforeClickedItemPosition = -1;
    private ConnectManager mConnector;
    private WifiManager mWifiManager;
    private Handler mP2PStateChangeHandler;


    //////////////////////////////
    private RelativeLayout mRelLive;
    private RelativeLayout mRelVideo;

    private ImageView mIvAlbum;
    private ImageView mIvUser;


    //////////////////////////////


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(ACTION_STOP_RTMP)) {
                    if (intent.getIntExtra("close", -1) == 10) {
                        mAdapter.changeStreamingState(false);
                    }
                } else if (action.equals(ACTION_START_RTMP)) {
                    mAdapter.changeStreamingState(true);
                }
            }
        }
    };
    private AlertDialog mAutoConnectDialog;
    private TextView mTextView;
    private View mDialogView;
    private TextView mTvDialogTips;

    private String dialogStr = "连接蓝牙中";

    private Handler hd = new MyHandler();

    private TextView mTvHelp;

    private LinearLayout mRlBtList;
    private ImageView mIvBtList;
    private String TAG = "MainActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideHeader();
        super.onCreate(savedInstanceState);
        setHeaderView(R.layout.header_bt_list);
        setBodyView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE}, PERMISSION_CALLBACK);
        }
        initLogin();
//        getRtmpUrl();
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);
        initViewId();

        mRSToRMConverter = RTSPToRTMPConverter.getInstance();
        mRTMPStreamerDialog = new RTMPStreamerDialog();
        mRTMPStreamerDialog.setClickListener(this);

        mRecordModel = mNeckbandManager.getRecordModel(this);
        mPhotoModel = mNeckbandManager.getPhotoModel(this);

        RecyclerView recycler = findViewById(R.id.recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mAdapter = new MainRecyclerAdapter(this, this);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(mAdapter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_START_RTMP);
        intentFilter.addAction(ACTION_STOP_RTMP);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFilter);

        ConnectManager.getInstance(getApplicationContext()).disconnect();


        ///////////////////////////////////
        mConnector = ConnectManager.getInstance(getApplicationContext()).setListener(this);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mP2PStateChangeHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MSG_P2P_STATE_DISABLED) {
                    if (!mWifiManager.isWifiEnabled()) {
                        Toast.makeText(MainActivity.this, R.string.wifi_state_disabled, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        mConnectHelper = ConnectHelper.getInstance().init(this);
        mBTConnectHelper = mConnectHelper.getBTConnectHelper();
        mWifiConnectHelper = mConnectHelper.getWifiConnectHelper();

        mRefreshLayout = findViewById(R.id.refresh);
        mRefreshLayout.setOnRefreshListener(this);

//        TextView skipBtn = findViewById(R.id.skip_btn);
//        skipBtn.setText(mCalledBy == CALLED_BY_START_ACTIVITY ? R.string.skip : R.string.back);
//        skipBtn.setOnClickListener(this);

        mProgressBar = findViewById(R.id.circular);

        RecyclerView recyclerView = findViewById(R.id.recyclerConnect);
        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        mAdapterConnect = new BTDeviceRecyclerAdapter(this, this);
        recyclerView.setLayoutManager(manager1);
        recyclerView.setAdapter(mAdapterConnect);

        mAdapterConnect.addItems(mBTConnectHelper.getBondedBTList());

        ArrayList<BluetoothDevice> bondedBTList = mBTConnectHelper.getBondedBTList();
        for (int i = 0; i < bondedBTList.size(); i++) {

            if (mBTConnectHelper.isBondedDevice(bondedBTList.get(i))) {
                Log.e("TAGOld", "发现旧设备");
//                ToastUtils.showShort("发现" + bondedBTList.get(i).getName() + "，自动重连中。。。");
                mConnector.start(null, bondedBTList.get(i));
                showAutoConnectDialog(bondedBTList.get(i).getName());
                break;
            }
        }
        ///////////////////////////////////////////
    }


    private void initLogin() {
        boolean login = SPUtils.getInstance().getBoolean("login");
        if (!login) {
            ActivityUtils.startActivity(LoginActivity.class);
        }
    }

    private static final int UPDATE_TEXT = 1;

    @SuppressLint("SetTextI18n")
    private void showAutoConnectDialog(String name) {
        mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_auto_connect, null, false);
        mAutoConnectDialog = new AlertDialog.Builder(this).setView(mDialogView).create();
        Window window = mAutoConnectDialog.getWindow();
        mAutoConnectDialog.setCancelable(true);

        AVLoadingIndicatorView avi = mDialogView.findViewById(R.id.avi_loading);
        mTextView = (TextView) mDialogView.findViewById(R.id.tv_tips);
//        mTextView.setText(name + "自动重连中");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                hd.sendMessage(message); // 发送消息
            }
        }).start();

        mAutoConnectDialog.show();

//        mConnectDialog.getWindow().setLayout((ScreenUtils.getScreenWidth() / 3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    // 定义一个内部类继承自Handler，并且覆盖handleMessage方法用于处理子线程传过来的消息
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: // 接受到消息之后，对UI控件进行修改
                    mTextView.setText("开始连接！");
                    break;
                case 2: {
                    mTextView.setText("连接成功！");
                    if (mAutoConnectDialog.isShowing()) {
                        mAutoConnectDialog.dismiss();
                    }
                    break;
                }
                case 3: {
                    mTextView.setText("连接失败！请重试");
                    if (mAutoConnectDialog.isShowing()) {
                        mAutoConnectDialog.dismiss();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    private ImageView mImgMainBtn;
    private ImageView mImgCamera;

    private void initViewId() {


        mImgMainBtn = findViewById(R.id.img_main_btn);
        mImgCamera = findViewById(R.id.img_camera);
        mImgMainBtn.bringToFront();
        mImgCamera.bringToFront();

        mRelLive = findViewById(R.id.rel_live);
        mRelVideo = findViewById(R.id.rel_video);

        mIvAlbum = findViewById(R.id.iv_album);
        mIvUser = findViewById(R.id.iv_user);
        mTvHelp = findViewById(R.id.tv_help);

        mRlBtList = findViewById(R.id.rl_bt_list);
        mIvBtList = findViewById(R.id.iv_bt_list);
        mRlBtList.setVisibility(View.GONE);

        mIvBtList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRlBtList.getVisibility() == View.VISIBLE) {
                    mRlBtList.setVisibility(View.GONE);
                } else if (mRlBtList.getVisibility() == View.GONE) {
                    mRlBtList.setVisibility(View.VISIBLE);
                }

            }
        });

        mIvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(UserActivity.class);
            }
        });

        mTvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(HelpActivity.class);
            }
        });

        mIvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(GalleryActivity.class);
                finish();
            }
        });


        mRelLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LiveActivity.class);
                startActivity(intent);
            }
        });

        mRelVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBTConnectHelper.registerReceiver(getApplicationContext());
        mBTConnectHelper.startDiscovery(-1, true, this);

        Log.e("connect state", "called on resume - " + getClass().getName());
        if (mNeckbandManager.getConnectStateManage().isConnected()) {
            mRSToRMConverter = RTSPToRTMPConverter.getInstance();
            mRSToRMConverter.getSentByteAmount2();
            if (mRSToRMConverter.isRTMPWorking()) {

            }
        }

        Log.e("ResFITT", "ReDevice:" + mSelectedBTDevice);

//        getRtmpUrl();

    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.e("ResFITT", "StartDevice:" + mSelectedBTDevice);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRSToRMConverter != null) {
            if (mRSToRMConverter.isRTMPWorking()) {
                mRSToRMConverter.stop();
            }
        }
//        mNeckbandManager.getPreviewModel().getMuteState();
//        if ()
//        mNeckbandManager.getPreviewModel().activateRTSP(mNeckbandManager.getAccessToken(), false);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.base_dialog_agree) {
            if (!mRSToRMConverter.isRTMPWorking()) {
                Log.e("TAGPZR", "HEREAAA");
                Intent intent = new Intent(MainActivity.this, RTMPStreamService.class);
                intent.setAction(RTMPStreamService.ACTION_START_RTMP_STREAM);
                intent.putExtra("rtmp_url", mRTMPStreamerDialog.getRTMPUrl());
                intent.putExtra("rtmp_bitrate_auto", mRTMPStreamerDialog.enableAutoBitrate());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.e("TAGPZR", "HEREBBB");
                    startForegroundService(intent);
                } else {
                    Log.e("TAGPZR", "HERECCC");
                    startService(intent);
                }
            }
            mRTMPStreamerDialog.dismissAllowingStateLoss();
        } else if (view.getId() == R.id.base_dialog_disagree) {
            mRTMPStreamerDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void clickedItemConnect(int position) {
        BTItem item = mAdapterConnect.getItem(position);
        if (item.mState == BTItem.STATE_NEAR) {
            mAdapterConnect.checked(mBeforeClickedItemPosition, position);
            mBeforeClickedItemPosition = position;
            mProgressBar.setVisibility(View.GONE);
            mBTConnectHelper.disconnect();
            mWifiConnectHelper.disconnect(true, -1, null);
            mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_NONE);

            mSelectedBTDevice = mAdapterConnect.getItem(position).mDevice;
            if (mBTConnectHelper.isBondedDevice(mSelectedBTDevice)) {
                mConnector.start(null, mSelectedBTDevice);
                showAutoConnectDialog(mSelectedBTDevice.getName());
            } else {
                Toast.makeText(this, R.string.bt_pairing_request, Toast.LENGTH_SHORT).show();
                mBTConnectHelper.selectedBTAddress(mSelectedBTDevice.getAddress());
                mSelectedBTDevice.createBond();
            }
        } else {
            Toast.makeText(this, item.mState == BTItem.STATE_FINDING ? R.string.bt_state_finding_alert : R.string.bt_state_none_alert, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void bluetoothState(boolean isOff) {

    }

    @Override
    public void foundBTDevice(BluetoothDevice device, boolean isCorrectDevice, boolean isBonded) {
        mAdapterConnect.addItem(device);
        if (isCorrectDevice && isBonded) {
            int correctDevicePosition = mAdapterConnect.getCorrectDevicePosition(device.getAddress());
            if (correctDevicePosition != -1) {
                mBeforeClickedItemPosition = correctDevicePosition;
                mAdapterConnect.checked(-1, correctDevicePosition);
            }
        }
    }

    @Override
    public void changedBondedState(BluetoothDevice device, int state) {
        Log.e("btListActivity", "changed bonded state");
        if (state == BluetoothDevice.BOND_BONDED) {
            mAdapterConnect.changedBonded(device.getAddress());
            mConnector.start(null, mSelectedBTDevice);
        } else if (state == BluetoothDevice.BOND_NONE) {
            Toast.makeText(this, R.string.bt_pairing_request_cancel, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void discoveryFinished(boolean foundDevice, boolean isBonded) {
        mProgressBar.setVisibility(View.GONE);
        if (!mCanceledBTDiscovery) {
            mAdapterConnect.changeItemsStateNone();
        }
        mCanceledBTDiscovery = false;
    }

    @Override
    public void discoveryTryCnt(int tryCnt, int maxTryCnt, boolean foundTargetDevice) {
        mProgressBar.setVisibility(View.VISIBLE);
        mAdapterConnect.changeItemStateFinding();
    }

    @Override
    public void onRefresh() {
        mBeforeClickedItemPosition = -1;
        mRefreshLayout.setRefreshing(false);
        mAdapterConnect.clear();
        mAdapterConnect.addItems(mBTConnectHelper.getBondedBTList());

        mCanceledBTDiscovery = mBTConnectHelper.isDiscovering();

        mBTConnectHelper.startDiscovery(-1, true, this);
    }

    public BroadcastReceiver mConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAGPZR", "HERE");
            if (intent.getAction() != null && mCalledBy == CALLED_BY_START_ACTIVITY) {
                if (intent.getAction().equals(ACTION_BT_CONNECTED)) {
                    Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        }
    };


    @Override
    public void bluetoothState(ConnectManager.STATE state, ConnectManager.PARING paring) {
        switch (paring) {
            case REQUEST:
                Toast.makeText(this, R.string.bt_pairing_request, Toast.LENGTH_SHORT).show();
                break;
            case CANCELED:
                Toast.makeText(this, R.string.bt_pairing_request_cancel, Toast.LENGTH_LONG).show();
                break;
            case BONDED:
                break;
            case BONDED_NONE:
                break;
        }

        switch (state) {
            case STATE_CONNECTING:
                mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_BT);

//                Toast.makeText(this, "连接蓝牙中", Toast.LENGTH_SHORT).show();
//                showAutoConnectDialog("");
                break;
            case STATE_CONNECTED:
                Toast.makeText(this, R.string.bt_connected, Toast.LENGTH_SHORT).show();
                break;
            case STATE_DISCONNECTED:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 3;
                        hd.sendMessage(message); // 发送消息
                    }
                }).start();

                Toast.makeText(this, R.string.bt_disconnected, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public void bluetoothDiscovery(int cnt, int max) {
        if (cnt > 4) {
            Toast.makeText(this, R.string.bt_not_found, Toast.LENGTH_LONG).show();
            mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_BT_NOT_FOUND);
        }
    }

    @Override
    public void p2pState(ConnectManager.STATE state, ConnectManager.STATUS status, WifiP2pInfo info) {
        switch (state) {
            case STATE_CONNECTING:
                Toast.makeText(this, R.string.wifi_p2p_connecting, Toast.LENGTH_SHORT).show();
                break;
            case STATE_CONNECTED:
                Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
                NeckbandRestApiClient.setBaseUrl(info.groupOwnerAddress.getHostAddress());
                mNeckbandManager.connect("newwifi", "123456");
                mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_DONE);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 2;
                        hd.sendMessage(message); // 发送消息
                    }
                }).start();

                if (mAutoConnectDialog.isShowing()) {
                    mAutoConnectDialog.dismiss();
                }
                break;
            case STATE_DISCONNECTED:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 3;
                        hd.sendMessage(message); // 发送消息
                    }
                }).start();
                if (mAutoConnectDialog.isShowing()) {
                    mAutoConnectDialog.dismiss();
                }
                Toast.makeText(this, R.string.wifi_p2p_disconnected, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void p2pDiscovery(int cnt, boolean locationEnabled) {
        if (cnt > 4) {
            Toast.makeText(this, locationEnabled ? R.string.wifi_p2p_not_found_alert1 : R.string.wifi_state_disabled, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void step(ConnectManager.STEP step) {
        switch (step) {
            case STEP_NONE:
                mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_NONE);
                break;
            case STEP_START:
                mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_START);
                break;
            case STEP_BT_FINDING:
                mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_BT_FINDING);
                break;
            case STEP_BT:
                mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_BT);
                break;
            case STEP_P2P:
                mNeckbandManager.getConnectStateManage().setState(ConnectStateManage.STATE.STATE_P2P);
                break;
            case STEP_DONE:
                break;
        }
    }

    @Override
    public void alert(ConnectManager.ALERT alert) {
        switch (alert) {
            case ALERT_GPS_ENABLED:
                break;
            case ALERT_GPS_DISABLED:
                Toast.makeText(this, R.string.location_disable, Toast.LENGTH_SHORT).show();
                break;
            case ALERT_DISABLE:
                break;
            case ALERT_BT_OFF:
                Toast.makeText(this, R.string.bt_state_disabled, Toast.LENGTH_SHORT).show();
                break;
            case ALERT_P2P_OFF:
                mP2PStateChangeHandler.removeMessages(MSG_P2P_STATE_DISABLED);
                mP2PStateChangeHandler.sendEmptyMessageDelayed(MSG_P2P_STATE_DISABLED, 1000);
                break;
        }
    }

    @Override
    public void restartAfterLocationEnabled() {
        Log.e("ResFITT", "Device:" + mSelectedBTDevice);
        mConnector.start(null, mSelectedBTDevice);
    }

    @Override
    public void clickedItem(int position) {
        MainRecyclerAdapter.Item item = mAdapter.getItem(position);
        if (item.mId == ID_SETTING) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        } else if (item.mId == ID_GALLERY) {
            Intent intent = new Intent(this, GalleryActivity.class);
            startActivity(intent);
        } else if (mNeckbandManager.getConnectStateManage().isConnected()) {
            switch (item.mId) {
                case ID_RECORDING:
                    if (System.currentTimeMillis() - mStartedRecordTime >= 2000) {
                        mStartedRecordTime = System.currentTimeMillis();
                        boolean isRecording = !mNeckbandManager.isRecording();
                        mRecordModel.actionRecord(mNeckbandManager.getAccessToken(), isRecording);
                        mAdapter.changeRecordState(isRecording);
                    } else {
                        Toast.makeText(this, R.string.alert_record_safe, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ID_TAKE_PHOTO:
                    mPhotoModel.takePhoto(mNeckbandManager.getAccessToken());
                    break;
                case ID_PREVIEW:
                    if (!mNeckbandManager.isRecording()) {
                        Intent intent = new Intent(this, PreviewActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "recording...", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ID_STREAMING:
                    if (System.currentTimeMillis() - mStreamingClickedTime > 1500) {
                        mStreamingClickedTime = System.currentTimeMillis();
                        if (!mRSToRMConverter.isRTMPWorking()) {
//                            mRTMPStreamerDialog.show(getSupportFragmentManager());
                            Intent intent = new Intent(MainActivity.this, LiveActivity.class);
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(MainActivity.this, RTMPStreamService.class);
                            intent.setAction(RTMPStreamService.ACTION_CANCEL_RTMP_STREAM);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(intent);
                            } else {
                                startService(intent);
                            }
                            mRSToRMConverter.exit();
                            mAdapter.changeStreamingState(false);
                        }
                    } else {
                        Toast.makeText(this, "Please, try again later.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } else {
            Toast.makeText(this, "please, check wifi direct.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void completedGetRecordState(boolean success, boolean isRecording) {
        super.completedGetRecordState(success, isRecording);
        Log.e("TAGPZR", "isRecording" + isRecording);
        mAdapter.changeRecordState(isRecording);
    }

    @Override
    public void completedTakePhoto(boolean success, String filename) {

    }
}
