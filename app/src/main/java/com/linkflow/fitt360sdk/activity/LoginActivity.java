package com.linkflow.fitt360sdk.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.linkflow.cpe.App;
import com.linkflow.cpe.Constans;
import com.linkflow.cpe.DeviceManager;
import com.linkflow.cpe.TokenUtils;
import com.linkflow.cpe.net.Api;
import com.linkflow.cpe.net.BaseSubscriber;
import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.helper.DoubleClickListener;
import com.linkflow.fitt360sdk.helper.Md5;
import com.linkflow.fitt360sdk.item.BaseBean;
import com.linkflow.fitt360sdk.item.Device;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.xuexiang.xupdate.XUpdate.getContext;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtUser;
    private EditText mEtPass;
    private TextView mTvLogin;
    private String TAG = "LoginActivity";

    private AVLoadingIndicatorView mAviLoading;

    private ImageView mIvChangeUrl;
    private Constans mConstans;

    private String mUrl;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        initViewId();
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);
        initLogin();



    }


    private void initLogin() {
        boolean login = SPUtils.getInstance().getBoolean("login");
        if (login) {
            ActivityUtils.startActivity(MainActivity.class);
            finish();
        }
    }

    private void initViewId() {
        mEtUser = findViewById(R.id.et_user);
        mEtPass = findViewById(R.id.et_pass);
        mTvLogin = findViewById(R.id.tv_login);
        mTvLogin.setOnClickListener(this);

        mAviLoading = findViewById(R.id.avi_loading);
        mAviLoading.hide();

        mIvChangeUrl = findViewById(R.id.iv_change_url);
        mIvChangeUrl.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                changeUrl();
            }
        });
    }

    private void changeUrl() {
//        mConstans = new Constans();
//        String baseUrl = mConstans.getBaseUrl();
        showUrlDialog();
    }

    private void showUrlDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_baseurl, null, false);
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
        Window window = alertDialog.getWindow();
        alertDialog.setCancelable(true);

        TextView url1 = view.findViewById(R.id.tv_url1);
        TextView url2 = view.findViewById(R.id.tv_url2);
        TextView url3 = view.findViewById(R.id.tv_url3);

        url1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUrl = url1.getText().toString();
                App.BaseUrl = mUrl;
                alertDialog.dismiss();
            }
        });

        url2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUrl = url2.getText().toString();
                App.BaseUrl = mUrl;
                alertDialog.dismiss();
            }
        });

        url3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUrl = url3.getText().toString();
                App.BaseUrl = mUrl;
                alertDialog.dismiss();
            }
        });


        alertDialog.show();

        alertDialog.getWindow().setLayout((ScreenUtils.getScreenWidth() / 4 * 3), LinearLayout.LayoutParams.WRAP_CONTENT);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_login) {
            if (mEtUser.getText().toString().trim().isEmpty() || mEtPass.getText().toString().trim().isEmpty()) {
                ToastUtils.showShort("输入不能为空");
            } else {
//                ToastUtils.showShort("登录成功");
                postLogin(mEtUser.getText().toString(), Md5.md5Decode32(mEtPass.getText().toString()));
            }
        }
    }

    private void postLogin(String account, String password) {
        Log.e("BASEURL", "URL:" + App.BaseUrl);

        @SuppressLint("HardwareIds") String device_sn = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        HashMap<String, Object> params = new HashMap<>();
        params.put("account", account);
        params.put("password", password);
        params.put("device_sn", device_sn);
        Api.getRetrofit().postLogin(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean<Device>>(LoginActivity.this) {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mAviLoading.show();
                        Log.e(TAG, "onStart");
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mAviLoading.hide();
                        Log.e(TAG, "onCompleted");
                    }


                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.e(TAG, "onError" + e);
                    }

                    @Override
                    public void onNext(BaseBean<Device> deviceBaseBean) {
                        super.onNext(deviceBaseBean);
                        Log.e(TAG, "onNext");
                        String token = deviceBaseBean.getData().getToken();
                        TokenUtils.setToken(token);
                        DeviceManager.getIns().saveDeviceInfo(deviceBaseBean.getData());//保存用户信息
                        SPUtils.getInstance().put("login", true);
                        String account1 = deviceBaseBean.getData().getAccount();
                        SPUtils.getInstance().put("account", account1);
                        SPUtils.getInstance().put("deviceSN", device_sn);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("account", account1);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}
