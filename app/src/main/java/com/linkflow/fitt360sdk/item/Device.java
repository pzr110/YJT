package com.linkflow.fitt360sdk.item;

import java.io.Serializable;

public class Device implements Serializable {
    private String id;
    private String account;
    private String password;
    private String title;
    private String pid;
    private String sys_sn;
    private String stream_url;
    private String http_flv_url;
    private String rtmp_url;
    private String hls_url;
    private String img;
    private String token;
    private String device_sn;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSys_sn() {
        return sys_sn;
    }

    public void setSys_sn(String sys_sn) {
        this.sys_sn = sys_sn;
    }

    public String getStream_url() {
        return stream_url;
    }

    public void setStream_url(String stream_url) {
        this.stream_url = stream_url;
    }

    public String getHttp_flv_url() {
        return http_flv_url;
    }

    public void setHttp_flv_url(String http_flv_url) {
        this.http_flv_url = http_flv_url;
    }

    public String getRtmp_url() {
        return rtmp_url;
    }

    public void setRtmp_url(String rtmp_url) {
        this.rtmp_url = rtmp_url;
    }

    public String getHls_url() {
        return hls_url;
    }

    public void setHls_url(String hls_url) {
        this.hls_url = hls_url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
