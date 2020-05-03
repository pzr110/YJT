package com.linkflow.cpe.net;


import com.linkflow.fitt360sdk.item.BaseBean;
import com.linkflow.fitt360sdk.item.Device;
import com.linkflow.fitt360sdk.item.RtmpBean;

import java.util.HashMap;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Administrator on 2016/10/8 0008.
 */

public interface ApiService {

    /**
     * Login request
     */
    @FormUrlEncoded
    @POST("login/login")
    Observable<BaseBean<Device>> postLogin(@FieldMap HashMap<String, Object> params);


    /**
     * 获取流地址
     */
//    @GET("live/RtmpCheck")
//    Observable<BaseBean<RtmpBean>> postRtmpUrl(@QueryMap HashMap<String, Object> params);

    /**
     * 获取流地址
     */
    @FormUrlEncoded
    @POST("live/RtmpCheck")
    Observable<BaseBean<RtmpBean>> postRtmpUrl(@FieldMap HashMap<String, Object> params);

//    /**
//     * 注册
//     */
//    @FormUrlEncoded
//    @POST("login/index")
//    Observable<BaseBean<User>> login(@FieldMap HashMap<String, Object> params);
//
//
//    @FormUrlEncoded
//    @POST("user/getinfo")
//    Observable<BaseBean<User>> getUserInfo(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 显示的直播列表
//     *
//     * @param params
//     * @return
//     */
////    @GET("live/livelist")
////    Observable<BaseBean<LiveList>> getLivelist(@QueryMap HashMap<String, Object> params);
//    @GET("live/lists")
//    Observable<BaseBean<LiveBeanList>> getLiveList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 录播列表
//     *
//     * @param params
//     * @return
//     */
//    @GET("video/lists")
//    Observable<BaseBean<RecordBeanList>> getRecordList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 录播信息
//     *
//     * @param params
//     * @return
//     */
//    @GET("video/getinfo")
//    Observable<BaseBean<RecordBeanList>> getRecordInfo(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 直播信息
//     *
//     * @param params
//     * @return
//     */
//    @GET("live/getinfo")
//    Observable<BaseBean<LiveBeanList>> getLiveInfo(@QueryMap HashMap<String, Object> params);
//
//    @GET("video/search")
//    Observable<BaseBean<SearchBeanList>> getSearchList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 显示banner
//     */
////    @GET("banner/index")
////    Observable<BaseBean<List<Banner>>> getBannerList(@QueryMap HashMap<String, Object> params);
//    @GET("banner/index")
//    Observable<BaseBean<BannerBeanList>> getBannerList(@QueryMap HashMap<String, Object> params);
//    /**
//     * 登陆请求
//     */
//    @FormUrlEncoded
//    @POST("user/app_login")
//    Observable<BaseBean<User>> app_login(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 获取验证码
//     */
//    @FormUrlEncoded
//    @POST("user/getSms")
//    Observable<BaseBean<String>> getSms(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 验证手机是否OK
//     */
//    @FormUrlEncoded
//    @POST("user/testCode")
//    Observable<BaseBean> testCode(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 修改密码
//     */
//    @FormUrlEncoded
//    @POST("user/changePwd")
//    Observable<BaseBean> changePwd(@FieldMap HashMap<String, Object> params);
//    /**
//     * 设置支付密码
//     */
//    @FormUrlEncoded
//    @POST("user/setPayPwd")
//    Observable<BaseBean> setPayPwd(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 改变接单状态
//     */
//    @FormUrlEncoded
//    @POST("user/changeOpen")
//    Observable<BaseBean> changeOpen(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 添加新地址
//     */
//    @FormUrlEncoded
//    @POST("user/addNewAddress")
//    Observable<BaseBean> addNewAddress(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 获取地址列表
//     */
//    @GET("user/getAddressList")
//    Observable<BaseBean<AddressList>> getAddressList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 获取用户消息
//     */
//    @GET("user/getNewsList")
//    Observable<BaseBean<NewsList>> getNewsList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 获取待接订单
//     */
//    @GET("user/getJieDanOrderList")
//    Observable<BaseBean<OrderList>> getJieDanOrderList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 获取地址列表
//     */
//    @GET("user/getRepairList")
//    Observable<BaseBean<RepairBean>> getRepairList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 获取用户消息
//     */
//    @GET("user/getMoney")
//    Observable<BaseBean<Double>> getMoney(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 获取交易记录
//     */
//    @GET("user/money_log")
//    Observable<BaseBean<MoneyHistoryList>> getMoneyHistory(@QueryMap HashMap<String, Object> params);
//    /**
//     * 获取提现记录
//     */
//    @GET("user/detail")
//    Observable<BaseBean<List<DrawDetail>>> getDrawHistory();
//
//    /**
//     * 添加修改订单状态
//     */
//    @FormUrlEncoded
//    @POST("user/order_time")
//    Observable<BaseBean> order_time(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 添加修改订单状态
//     */
//    @FormUrlEncoded
//    @POST("user/order_time_chancel")
//    Observable<BaseBean> order_time_chancel(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 获取接单状态的订单
//     */
//    @GET("user/getRepairingOrderList")
//    Observable<BaseBean<OrderList>> getRepairingOrderList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 上传到达定位信息
//     */
//    @FormUrlEncoded
//    @POST("user/update_location")
//    Observable<BaseBean> update_location(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 首页订单数据
//     */
//    @GET("user/getIndexOrder")
//    Observable<BaseBean<IndexOrder>> getIndexOrder(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 首页Banner数据
//     */
//    @GET("user/getBanner")
//    Observable<BaseBean<BannerList>> getBanner(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 首页通知数据
//     */
//    @GET("user/getNotice")
//    Observable<BaseBean<Notice>> getNotice(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 获取服务条款
//     */
//    @GET("user/getService")
//    Observable<BaseBean<Notice>> getService(@QueryMap HashMap<String, Object> params);
//
//
//    /**
//     * 接单
//     */
//    @FormUrlEncoded
//    @POST("user/order_ok")
//    Observable<BaseBean> order_ok(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 取消订单
//     */
//    @FormUrlEncoded
//    @POST("user/order_chancel")
//    Observable<BaseBean> order_chancel(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 未处理订单
//     */
//    @GET("user/getUnContactOrderList")
//    Observable<BaseBean<OrderList>> getUnContactOrderList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 修改订单预约时间
//     */
//    @FormUrlEncoded
//    @POST("user/post_shop_order_time")
//    Observable<BaseBean> post_shop_order_time(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 提醒订单
//     */
//    @GET("user/getNoticeOrderList")
//    Observable<BaseBean<OrderList>> getNoticeOrderList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 取消的订单
//     */
//    @GET("user/getChancelOrderList")
//    Observable<BaseBean<OrderList>> getChancelOrderList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 全部订单
//     */
//    @GET("user/getAllOrderList")
//    Observable<BaseBean<OrderList>> getAllOrderList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 获取某个订单的维修方案
//     */
//    @GET("user/getOrderRepairInfo")
//    Observable<BaseBean<OrderRepairList>> getOrderRepairInfo(@QueryMap HashMap<String, Object> params);
//
//
//    /**
//     * 上传维修方案，数组格式的
//     */
//    @FormUrlEncoded
//    @POST("user/updateOrderRepairList")
//    Observable<PriceResponse> updateOrderRepairList(@FieldMap HashMap<String, Object> params);
//
//
//    /**
//     * 提交上门维修时间
//     */
//    @FormUrlEncoded
//    @POST("user/update_return_time")
//    Observable<BaseBean> update_return_time(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 获取优惠券
//     */
//    @GET("user/get_coupon")
//    Observable<BaseBean<Coupon>> get_coupon(@QueryMap HashMap<String, Object> params);
//
//    @Multipart
//    @POST("user/upload")
//    Observable<BaseBean<List<Url>>> uploadFiles(@Part() List<MultipartBody.Part> files, @Part("totalFiles") RequestBody totalFiles);
//
//
//    /**
//     * 登录时获取验证码
//     *
//     * @param params
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("user/sendLoginCaptcha")
//    Observable<BaseBean> sendLoginCaptcha(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 登录时获取验证码
//     *
//     * @param params
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("user/checkLoginCaptcha")
//    Observable<BaseBean<User>> checkLoginCaptcha(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 得到街道信息
//     */
//    @GET("area/getChengDuAreaInfo")
//    Observable<CityInfo> getChengDuAreaInfo();
//
//
//    /**
//     * cht 图片上传
//     *
//     * @param photo
//     * @return
//     */
//    @POST("user/saveProfile")
//    Observable<BaseBean<User>> saveProfile(@Body RequestBody photo);
//
//    /**
//     * cht 发布房源
//     *
//     * @param
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("house/addSellHouseStep1")
//    Observable<BaseBean<HouseIdBean>> addSellHouseStep1(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * cht 发布房源2
//     *
//     * @param
//     * @return
//     */
//    @POST("house/addSellHouseStep2")
//    Observable<BaseBean<HouseIdBean>> addSellHouseStep2(@Body RequestBody photo);
//
//    /**
//     * 退出登录
//     */
//    @POST("user/logoff")
//    Observable<BaseBean> logoff(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 获得自己发布的房源列表
//     */
//    @GET("house/getSellHouseList")
//    Observable<BaseBean<HouseBean>> getSellHouseList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 修改房源
//     *
//     * @param
//     * @return
//     */
//    @POST("house/editSellHouse")
//    Observable<BaseBean> editSellHouse(@Body RequestBody photo);
//
//    /**
//     * cht 发布房源
//     *
//     * @param
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("house/deleteSellHouse")
//    Observable<BaseBean> deleteSellHouse(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 查看所有在售的房子
//     */
//    @GET("house/getOnSellHouseList")
//    Observable<BaseBean<HouseBean>> getOnSellHouseList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 将某房添加到浏览记录
//     */
//    @FormUrlEncoded
//    @POST("user/saveReviewInfo")
//    Observable<BaseBean> saveReviewInfo(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 收藏
//     */
//    @FormUrlEncoded
//    @POST("user/addToFavorite")
//    Observable<BaseBean> addToFavorite(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 取消收藏
//     */
//    @FormUrlEncoded
//    @POST("user/cancelFavorite")
//    Observable<BaseBean> cancelFavorite(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 查看我的浏览记录
//     */
//    @GET("user/getReviewInfoList")
//    Observable<BaseBean<HouseBean>> getReviewInfoList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 我的收藏记录
//     */
//    @GET("user/getFavoriteList")
//    Observable<BaseBean<HouseBean>> getFavoriteList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 我的收藏记录
//     */
//    @GET("article/getArticleList")
//    Observable<BaseBean<ArticleBean>> getArticleList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 获得我的优惠券
//     */
//    @GET("user/getCouponList")
//    Observable<BaseBean<CouonBaen>> getCouponList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 兑换优惠码
//     */
//    @FormUrlEncoded
//    @POST("user/exchangeInvitecode")
//    Observable<BaseBean> exchangeInvitecode(@FieldMap HashMap<String, Object> params);
//
//    /*
//     * *
//     */
//    @FormUrlEncoded
//    @POST("house/sellHousePublish")
//    Observable<BaseBean<PayRey>> sellHousePublish(@FieldMap HashMap<String, Object> params);
//
//    /*
//     * *微信登录1
//     */
//    @FormUrlEncoded
//    @POST("user/uploadWechatInfo")
//    Observable<BaseBean<User>> uploadWechatInfo(@FieldMap HashMap<String, Object> params);
//
//    /*
//     * *微信登录
//     */
//    @FormUrlEncoded
//    @POST("user/bindAccountInfo")
//    Observable<BaseBean<User>> bindAccountInfo(@FieldMap HashMap<String, Object> params);
//
//    /*
//     * *付费得到房主信息
//     */
//    @FormUrlEncoded
//    @POST("house/getSellerInfo")
//    Observable<BaseBean<PayRey>> getSellerInfo(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 历史价格记录
//     */
//    @GET("house/getSecondHouseBuyRecord")
//    Observable<BaseBean<CostListBean>> getSecondHouseBuyRecord(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 看房记录
//     */
//    @GET("house/getBuyHouseList")
//    Observable<BaseBean<HouseBean>> getBuyHouseList(@QueryMap HashMap<String, Object> params);
//
//    /*
//     * *退押金
//     */
//    @FormUrlEncoded
//    @POST("house/applyRefund")
//    Observable<BaseBean> applyRefund(@FieldMap HashMap<String, Object> params);
//
//    /*
//     * *扫描推广人员二维码获得金额
//     */
//    @FormUrlEncoded
//    @POST("user/scanQrcode")
//    Observable<BaseBean> scanQrcode(@FieldMap HashMap<String, Object> params);
//
//
//    /*
//     * *抵扣
//     */
//    @FormUrlEncoded
//    @POST("user/deductAmount")
//    Observable<BaseBean> deductAmount(@FieldMap HashMap<String, Object> params);
//
//    /**
//     * 优惠券列表
//     */
//    @GET("user/getAmountdetail")
//    Observable<BaseBean<CouonBaen>> getAmountdetail(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 检查版本跟新
//     */
//    @GET("index/getAppVersion")
//    Observable<BaseBean<VersinCodeBean>> getAppVersion();
//
//    /**
//     * 更新/首页获取房屋列表
//     */
//    @GET("house/getIndexHouseList")
//    Observable<BaseBean<HouseBean>> getIndexHouseList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 更新/获取公共目录求购列表
//     */
//    @GET("house/publicQiugouList")
//    Observable<BaseBean<QiuGouBean>> publicQiugouList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 更新/发布求购列表
//     */
//    @POST("house/addQiuGou")
//    Observable<BaseBean> addQiuGou(@Body RequestBody photo);
//
//    /**
//     * 更新/历史成交价格
//     */
//    @GET("house/getHistoryPrice")
//    Observable<BaseBean<CostListBean>> getHistoryPrice(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 更新/获取服务评价
//     */
//    @GET("house/getServerCommitList")
//    Observable<BaseBean<CommitBean>> getServerCommitList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 更新/查看所有法拍房
//     */
//    @GET("house/getFaPaiHouseList")
//    Observable<BaseBean<HouseBean>> getFaPaiHouseList(@QueryMap HashMap<String, Object> params);
//
//    /**
//     * 删除维修项目
//     */
//    @FormUrlEncoded
//    @POST("user/delRepairing")
//    Observable<BaseBean> deleteRepairItem(@FieldMap HashMap<String, String> params);
//
//    /**
//     * 获取支付二维码
//     */
//    @FormUrlEncoded
//    @POST("user/actionNative")
//    Observable<BaseBean<String>> getQrCode(@FieldMap HashMap<String, String> params);
//
//    /**
//     * 查询订单是否支付成功
//     */
//    @FormUrlEncoded
//    @POST("user/get_order")
//    Observable<BaseBean> getPaid(@FieldMap HashMap<String, String> params);
//
//    /**
//     * 查询订单数量
//     */
//    @POST("user/getAllOrderCount")
//    Observable<BaseBean<OrderCount>> getOrderCount();
//
//    /**
//     * 查询订单数量
//     */
//    @FormUrlEncoded
//    @POST("user/add_order_src")
//    Observable<BaseBean> sendFiles(@FieldMap HashMap<String, String> params);
//    /**
//     * 免费活动
//     */
//    @FormUrlEncoded
//    @POST("user/free")
//    Observable<BaseBean<String>> free(@FieldMap HashMap<String, String> params);
}
