package com.linkflow.cpe.bean;

import java.util.List;

public class TokenData {
    private String userid;

    private String channel;

    private String appid;

    private String nonce;

    private int timestamp;

    private List<String> gslb;

    private String token;

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppid() {
        return this.appid;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getNonce() {
        return this.nonce;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public void setGslb(List<String> gslb) {
        this.gslb = gslb;
    }

    public List<String> getGslb() {
        return this.gslb;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
