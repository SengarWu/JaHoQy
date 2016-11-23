package com.xpple.jahoqy.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;


public class User extends BmobUser {

    private String HuanxinPassword;//环信密码
    private BmobFile userPhoto;  //用户头像-
    private String gender;//
    private int experience;//经验值
    private int userIntegral;//用户积分
    private String gangsName; // 帮派名称
    private int gangsPosition; //帮派职位
    private String jahoAnnounce;  //江湖
    private String city;  //城市
    private BmobRelation answerQuestion;
    private Boolean sign;//签到
    public  Boolean getSign(){ return sign;}
    public void setSign(Boolean sign){this.sign=sign;}

    public BmobRelation getAnswerQuestion() {
        return answerQuestion;
    }

    public void setAnswerQuestion(BmobRelation answerQuestion) {
        this.answerQuestion = answerQuestion;
    }

    public String getHuanxinPassword(){return HuanxinPassword;}

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHuanxinPassword(String HuanxinPassword){this.HuanxinPassword=HuanxinPassword;}


    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getUserIntegral() {
        return userIntegral;
    }

    public void setUserIntegral(int userIntegral) {
        this.userIntegral = userIntegral;
    }

    public String getGangsName() {
        return gangsName;
    }

    public void setGangsName(String gangsName) {
        this.gangsName = gangsName;
    }

    public int getGangsPosition() {
        return gangsPosition;
    }

    public void setGangsPosition(int gangsPosition) {
        this.gangsPosition = gangsPosition;
    }

    public BmobFile getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(BmobFile userPhoto) {
        this.userPhoto = userPhoto;
    }


    public String getJahoAnnounce() {
        return jahoAnnounce;
    }

    public void setJahoAnnounce(String jahoAnnounce) {
        this.jahoAnnounce = jahoAnnounce;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}