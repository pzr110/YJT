package com.linkflow.cpe.bean;

public class TokenBean {

    private int status;

    private String msg;

    private TokenData data;

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setData(TokenData data) {
        this.data = data;
    }

    public TokenData getData() {
        return this.data;
    }
}
