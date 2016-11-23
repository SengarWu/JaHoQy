package com.xpple.jahoqy.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Koreleone on 2015/9/30.
 */
public class OfferHelp extends BmobObject {
    private User user;
    private SeekHelp sh;
    private String accept;
    private String comment;
    private String result;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SeekHelp getSh() {
        return sh;
    }

    public void setSh(SeekHelp sh) {
        this.sh = sh;
    }
}
