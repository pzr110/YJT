package com.linkflow.fitt360sdk.activity.user;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.BarUtils;
import com.linkflow.cpe.net.Api;
import com.linkflow.cpe.net.BaseSubscriber;
import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.activity.BaseActivity;
import com.linkflow.fitt360sdk.adapter.MessageAdapter;
import com.linkflow.fitt360sdk.item.BaseBean;
import com.linkflow.fitt360sdk.item.MessageBeanList;
import com.linkflow.fitt360sdk.item.MessageInfoBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MessageActivity extends BaseActivity {

    private ImageView mTvBack;
    private RecyclerView mRecyclerView;

    private MessageAdapter mAdapter;
    private List<MessageInfoBean> mBeanList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);

        mTvBack = findViewById(R.id.tv_back);
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = findViewById(R.id.recyclerView);

        initView();

        getInfo();

    }



    private void getInfo() {
        HashMap<String, Object> params = new HashMap<>();
//        params.put("page", page + "");
        Api.getRetrofit().getMessageList(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean<MessageBeanList>>(this) {

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
                        Log.e("TAGPZR", "onError" + e.toString() + "Err:" + e.getMessage());


                    }

                    @Override
                    public void onNext(BaseBean<MessageBeanList> bean) {
                        super.onNext(bean);


                        List<MessageInfoBean> netLists = bean.getData().getLists();
                        mAdapter.setNewData(netLists);
                    }
                });

    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mBeanList = new ArrayList<>();
        mAdapter = new MessageAdapter(mBeanList);

        View emptyView = getLayoutInflater().inflate(R.layout.item_empty, null);
        mAdapter.setEmptyView(emptyView);
        mRecyclerView.setAdapter(mAdapter);
    }
}
