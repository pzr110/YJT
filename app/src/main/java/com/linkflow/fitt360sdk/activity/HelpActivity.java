package com.linkflow.fitt360sdk.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.BarUtils;
import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.adapter.ImageAdapter;
import com.linkflow.fitt360sdk.item.BannerBean;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.transformer.AlphaPageTransformer;

import java.util.List;

public class HelpActivity extends AppCompatActivity {
    private Banner mBanner;

    private List<BannerBean> mLists;

    private ImageView mIvBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);

        mBanner = findViewById(R.id.banner);
        useBanner();

        mIvBack = findViewById(R.id.iv_back);
        mIvBack.bringToFront();
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void useBanner() {
//        mBanner.setAdapter(new ImageAdapter(BannerBean.getTestData()));

//        List<BannerBean> testData3 = BannerBean.getTestData3();
//        List<BannerBean> bannerData = lists;
//        String image = bannerData.get(0).getImage();
        List<BannerBean> testData = BannerBean.getTestData();

        mBanner.setAdapter(new ImageAdapter(testData));
        mBanner.setDelayTime(5000);
        //添加画廊效果(可以和其他PageTransformer组合使用，比如AlphaPageTransformer，注意但和其他带有缩放的PageTransformer会显示冲突)
//        mBanner.setBannerGalleryEffect(18, 10);
        //添加透明效果(画廊配合透明效果更棒)
        //设置指示器
//        mBanner.setIndicator(new CircleIndicator(this));
//        mBanner.setIndicatorSelectedColorRes(R.color.color11AEFA);
//        mBanner.addPageTransformer(new AlphaPageTransformer());

        mBanner.start();
    }
}
