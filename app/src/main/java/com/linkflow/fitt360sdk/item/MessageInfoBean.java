package com.linkflow.fitt360sdk.item;

public class MessageInfoBean {
    private String id;
    private String title;
    private String content;
    private String is_must;
    private String created_at;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIs_must() {
        return is_must;
    }

    public void setIs_must(String is_must) {
        this.is_must = is_must;
    }
}
