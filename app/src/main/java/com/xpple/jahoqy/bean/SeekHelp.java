package com.xpple.jahoqy.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by Koreleone on 2015/9/29.
 */
public class SeekHelp extends BmobObject {

    private BmobGeoPoint address;
    private User user;
    private String state;
    private int needNum;
    private Integer giveHelpNum;
    private String title;
    private String type;
    private int awardInteral;
    private BmobDate limittime;
    private int givemoney;
    private String details;
    private String Phone;
    private BmobFile picture1;
    private BmobFile picture2;
    private BmobFile picture3;

    public Integer getGiveHelpNum() {
        return giveHelpNum;
    }

    public void setGiveHelpNum(Integer giveHelpNum) {
        this.giveHelpNum = giveHelpNum;
    }

    public BmobFile getPicture1() {
        return picture1;
    }

    public void setPicture1(BmobFile picture1) {
        this.picture1 = picture1;
    }

    public BmobFile getPicture2() {
        return picture2;
    }

    public void setPicture2(BmobFile picture2) {
        this.picture2 = picture2;
    }

    public BmobFile getPicture3() {
        return picture3;
    }

    public void setPicture3(BmobFile picture3) {
        this.picture3 = picture3;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public BmobDate getLimittime() {
        return limittime;
    }

    public void setLimittime(BmobDate limittime) {
        this.limittime = limittime;
    }

    public int getGivemoney() {
        return givemoney;
    }

    public void setGivemoney(int givemoney) {
        this.givemoney = givemoney;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BmobGeoPoint getAddress() {
        return address;
    }

    public void setAddress(BmobGeoPoint address) {
        this.address = address;
    }



    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getNeedNum() {
        return needNum;
    }

    public void setNeedNum(int needNum) {
        this.needNum = needNum;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAwardInteral() {
        return awardInteral;
    }

    public void setAwardInteral(int awardInteral) {
        this.awardInteral = awardInteral;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
