package com.linkflow.fitt360sdk.activity.user;

import android.view.View;
import android.widget.ImageView;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.activity.BaseActivity;

public class AboutActivity extends BaseActivity {

    private ImageView mTvBack;


    @Override
    protected void setHeaderView(int resource) {
        super.setHeaderView(resource);
        setHeaderView(R.layout.activity_base_head);
        setBodyView(R.layout.activity_about);

        mTvBack = findViewById(R.id.tv_back);
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
