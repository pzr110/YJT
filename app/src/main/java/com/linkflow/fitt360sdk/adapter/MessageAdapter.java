package com.linkflow.fitt360sdk.adapter;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.MessageInfoBean;

import java.util.List;

public class MessageAdapter extends BaseQuickAdapter<MessageInfoBean, BaseViewHolder> {

    private ImageView mIvSelect;


    public MessageAdapter(@Nullable List<MessageInfoBean> data) {
        super(R.layout.item_message_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageInfoBean item) {
        helper.setText(R.id.tv_title, item.getTitle())
                .setText(R.id.tv_content, item.getContent())
                .setText(R.id.tv_create_time, item.getCreated_at());
    }
}
