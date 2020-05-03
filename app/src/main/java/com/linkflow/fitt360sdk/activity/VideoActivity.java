package com.linkflow.fitt360sdk.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.linkflow.fitt360sdk.R;
import com.wang.avi.AVLoadingIndicatorView;

import app.library.linkflow.manager.item.StatusItem;
import app.library.linkflow.manager.item.VolumeItem;
import app.library.linkflow.manager.model.InfoModel;
import app.library.linkflow.manager.model.PhotoModel;
import app.library.linkflow.manager.model.RecordModel;
import app.library.linkflow.manager.neckband.ConnectStateManage;

public class VideoActivity extends BaseActivity implements PhotoModel.Listener {
    private long mStartedRecordTime;

    private RecordModel mRecordModel;
    private PhotoModel mPhotoModel;

    private AVLoadingIndicatorView mAviLoading;
    private TextView mTvBack;
    private ProgressBar mProgressBarPower;
    private TextView mTvPower;
    private ProgressBar mProgressBar2;
    private RelativeLayout mRlVideo;
    private ImageView mIvVideo;
    private TextView mTvVideo;
    private ImageView mIvPhoto;
    private ImageView mImgMainBtn;
    private ImageView mImgRedDot;
    private ImageView mIvVideoCenter;
    private ImageView mIvAlbum;
    private ImageView mIvUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);
        mRecordModel = mNeckbandManager.getRecordModel(this);
        mPhotoModel = mNeckbandManager.getPhotoModel(this);

        initViewId();

        initListener();


    }

    private void initListener() {
        boolean connected = mNeckbandManager.getConnectStateManage().isConnected();
        if (connected) {
            mIvVideo.setEnabled(true);
            mIvPhoto.setEnabled(true);
            mIvVideo.setBackgroundResource(R.drawable.shape_circle_gradient);
            mIvVideoCenter.setBackgroundResource(R.drawable.shape_accent_white_circle);
        } else {
            mIvVideo.setEnabled(false);
            mIvPhoto.setEnabled(false);
            mIvVideo.setBackgroundResource(R.drawable.shape_bg_gray_circle);
            mIvPhoto.setBackgroundResource(R.drawable.shape_bg_gray_circle);
            mIvVideoCenter.setBackgroundResource(R.drawable.shape_accent_white_circle);
        }
    }

    private void initViewId() {
        mAviLoading = findViewById(R.id.avi_loading);
        mTvBack = findViewById(R.id.tv_back);
        mProgressBarPower = findViewById(R.id.progressBar_power);
        mTvPower = findViewById(R.id.tv_power);
        mProgressBar2 = findViewById(R.id.progressBar2);
        mRlVideo = findViewById(R.id.rl_video);
        mIvVideo = findViewById(R.id.iv_video);
        mTvVideo = findViewById(R.id.tv_video);
        mIvPhoto = findViewById(R.id.iv_photo);
        mImgMainBtn = findViewById(R.id.img_main_btn);
        mImgRedDot = findViewById(R.id.img_red_dot);
        mIvVideoCenter = findViewById(R.id.iv_video_center);



        mIvAlbum = findViewById(R.id.iv_album);
        mIvUser = findViewById(R.id.iv_user);


        mImgMainBtn.bringToFront();
        mImgRedDot.bringToFront();

        mIvVideo.setOnClickListener(this::onClick);
        mIvPhoto.setOnClickListener(this::onClick);
        mTvBack.setOnClickListener(this::onClick);
        mIvAlbum.setOnClickListener(this::onClick);
        mIvUser.setOnClickListener(this::onClick);

    }

    private boolean isRecording = false;

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.iv_video: {
                if (System.currentTimeMillis() - mStartedRecordTime >= 2000) {
                    mStartedRecordTime = System.currentTimeMillis();

                    if (!isRecording) {
                        mRecordModel.actionRecord(mNeckbandManager.getAccessToken(), true);
                        mTvVideo.setText("录制中。。。");
                        isRecording = !isRecording;
                        changeLiveEnable(isRecording);
                        mIvPhoto.setEnabled(false);
                    } else {
                        mRecordModel.actionRecord(mNeckbandManager.getAccessToken(), false);
                        isRecording = !isRecording;
                        changeLiveEnable(isRecording);
                        mTvVideo.setText("录制");
                        mIvPhoto.setEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "请不要频繁切换相机状态", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.iv_photo: {
                mPhotoModel.takePhoto(mNeckbandManager.getAccessToken());
                break;
            }
            case R.id.tv_back: {
                finish();
                break;
            }
            case R.id.iv_album:{
                ActivityUtils.startActivity(GalleryActivity.class);
//                finish();
                break;
            }
            case R.id.iv_user:{
                ActivityUtils.startActivity(UserActivity.class);
                break;
            }
        }

    }

    private void changeLiveEnable(boolean isPushStream) {
        if (isPushStream) {
            // 推流中
            mIvVideo.setBackgroundResource(R.drawable.shape_bg_white_circle);
            mIvVideoCenter.setBackgroundResource(R.drawable.shape_center_red_circle);
            mTvBack.setEnabled(false);

        } else {
            // 停止了推流

            mTvBack.setEnabled(true);
            mIvVideo.setBackgroundResource(R.drawable.shape_circle_gradient);
            mIvVideoCenter.setBackgroundResource(R.drawable.shape_accent_white_circle);
        }
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

    @Override
    public void completedTakePhoto(boolean b, String s) {

    }
}
