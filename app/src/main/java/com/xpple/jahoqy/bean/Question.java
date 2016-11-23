package com.xpple.jahoqy.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by sengar on 2015/9/30 0030.
 */
public class Question extends BmobObject {

    private User user; //发布用户
    private String title; //标题
    private String details;  //信息描述
    private int Award;  //积分
    private String type;  //类型
    private String city; //城市
    private String state;   //状态
    private BmobFile picture1;
    private BmobFile picture2;
    private BmobFile picture3;


    public Question(){}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getAward() {
        return Award;
    }

    public void setAward(int award) {
        Award = award;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public BmobFile getPicture1() {
        return picture1;
    }

    public void setPicture1(BmobFile picture1) {
        this.picture1 = picture1;
    }

    public void setPicture2(BmobFile picture2) {
        this.picture2 = picture2;
    }

    public BmobFile getPicture2() {
        return picture2;
    }

    public void setPicture3(BmobFile picture3) {
        this.picture3 = picture3;
    }

    public BmobFile getPicture3() {
        return picture3;
    }
}
