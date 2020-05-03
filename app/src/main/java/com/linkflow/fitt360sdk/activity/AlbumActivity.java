package com.linkflow.fitt360sdk.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.BarUtils;
import com.linkflow.fitt360sdk.R;

public class AlbumActivity extends BaseActivity {

    private RecyclerView mRecycler;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);

        mRecycler = findViewById(R.id.recycler);


    }
}
