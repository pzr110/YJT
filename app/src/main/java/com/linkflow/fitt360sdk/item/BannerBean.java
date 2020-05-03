package com.linkflow.fitt360sdk.item;

import com.linkflow.fitt360sdk.R;

import java.util.ArrayList;
import java.util.List;

public class BannerBean {

    public Integer imageRes;

    public BannerBean(Integer imageRes) {
        this.imageRes = imageRes;

    }

    public static List<BannerBean> getTestData() {
        List<BannerBean> list = new ArrayList<>();
        list.add(new BannerBean(R.drawable.banner2));
        list.add(new BannerBean(R.drawable.banner3));
        list.add(new BannerBean(R.drawable.banner1));

        return list;
    }

}
