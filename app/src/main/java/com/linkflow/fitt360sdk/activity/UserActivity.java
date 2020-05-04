package com.linkflow.fitt360sdk.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.library.YLCircleImageView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.activity.user.LiveSettingActivity;
import com.linkflow.fitt360sdk.activity.user.UserMangerActivity;

public class UserActivity extends BaseActivity implements View.OnClickListener {

    private YLCircleImageView mCivUserHead;
    private LinearLayout mLlUseManger;
    private LinearLayout mLlLiveSetting;
    private LinearLayout mLlHotParing;
    private LinearLayout mLlGpsSetting;
    private LinearLayout mLlAbout;
    private ImageView mImgMainBtn;
    private ImageView mImgRedDot;
    private ImageView mIvAlbum;
    private ImageView mIvUser;

    private TextView mTvUserName;
    private TextView mTvLoginOut;

    private Switch mSwitchHot;
    private Switch mSwitchGps;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);

        initViewId();

        initInfo();

        initListener();

    }

    private void initListener() {

    }

    private void initInfo() {
        String account = SPUtils.getInstance().getString("account");
        mTvUserName.setText(account);
    }

    private void initViewId() {
        mCivUserHead = findViewById(R.id.civ_user_head);
        mLlUseManger = findViewById(R.id.ll_use_manger);
        mLlLiveSetting = findViewById(R.id.ll_live_setting);
        mLlHotParing = findViewById(R.id.ll_hot_paring);
        mLlGpsSetting = findViewById(R.id.ll_gps_setting);
        mLlAbout = findViewById(R.id.ll_about);
        mImgMainBtn = findViewById(R.id.img_main_btn);
        mImgRedDot = findViewById(R.id.img_red_dot);
        mIvAlbum = findViewById(R.id.iv_album);
        mIvUser = findViewById(R.id.iv_user);
        mSwitchHot = findViewById(R.id.switch_hot);
        mSwitchGps = findViewById(R.id.switch_gps);

        mTvUserName = findViewById(R.id.tv_user_name);

        mImgMainBtn.bringToFront();
        mImgRedDot.bringToFront();

        mTvLoginOut = findViewById(R.id.tv_login_out);
        mTvLoginOut.setOnClickListener(this);
        mLlUseManger.setOnClickListener(this);
        mLlLiveSetting.setOnClickListener(this);
        mImgMainBtn.setOnClickListener(this);
        mIvAlbum.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_out: {
                SPUtils.getInstance().put("login", false);
                ActivityUtils.startActivity(LoginActivity.class);
                ActivityUtils.finishAllActivities();
                break;
            }
            case R.id.ll_use_manger: {
                ActivityUtils.startActivity(UserMangerActivity.class);
                break;
            }
            case R.id.ll_live_setting: {
                ActivityUtils.startActivity(LiveSettingActivity.class);
                break;
            }
            case R.id.img_main_btn: {
                ActivityUtils.startActivity(MainActivity.class);
                finish();
                break;
            }
            case R.id.iv_album: {
                ActivityUtils.startActivity(GalleryActivity.class);
                break;
            }

        }
    }
}
