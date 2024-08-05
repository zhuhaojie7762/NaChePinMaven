package com.aichebaba.rooftop.ue;

public class UeditorState {
    public static final String SUCCESS = "SUCCESS";

    private String state;
    private String url;
    private String title;
    private String original;

    public UeditorState() {}

    public UeditorState(String state, String url, String title, String original) {
        this.state = state;
        if (url.startsWith("http")) {
            this.url = url;
        } else {
            this.url = url.replaceAll("\\/\\/", "\\/");
        }
        this.title = title;
        this.original = original;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}
