package com.xpple.jahoqy.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by caolin on 2015/10/4.
 */
public class Gangs extends BmobObject implements Serializable {
    private String gangsName;//帮派名称
    private BmobFile gangsPhoto;//帮派图标
    private String gangsPurpose;//帮派宗旨
    private String gangsType;//帮派类型
    private String gangsCreater;//帮派创始人
    private int gangsGrade;//帮派等级
    private String gangsObjectId;//
    private String gangGao;
    public void  Gangs(){}
    public void  Gangs(String gangsName,BmobFile gangsPhoto,String gangsPurpose,
                       String gangsType,String gangsCreater,int gangsGrade,String gangsObjectID){
        this.gangsName=gangsName;
        this.gangsPhoto=gangsPhoto;
        this.gangsPurpose=gangsPurpose;
        this.gangsType=gangsType;
        this.gangsCreater=gangsCreater;
        this.gangsGrade=gangsGrade;
        this.gangsObjectId=gangsObjectId;
    }
    public void setGanggao(String gangGao) {
        this.gangGao = gangGao;
    }

    public void setGangsName(String gangsName) {
        this.gangsName = gangsName;
    }

    public void setGangsPurpose(String gangsPurpose) {
        this.gangsPurpose = gangsPurpose;
    }

    public void setGangsType(String gangsType) {
        this.gangsType = gangsType;
    }

    public void setGangsCreater(String gangsCreater) {
        this.gangsCreater = gangsCreater;
    }
    public void setGangsObjectId(String gangsObjectId) {
        this.gangsObjectId = gangsObjectId;
    }
    public String getGanggao() {
        return gangGao;
    }
    public void setGangsGrade(int gangsGrade) {
        this.gangsGrade = gangsGrade;
    }
    public void setGangsPhoto(BmobFile gangsPhoto){
        this.gangsPhoto=gangsPhoto;
    }
    public String getGangsObjectId(){return gangsObjectId;}
    public String getGangsName(){return gangsName;}
    public BmobFile getGangsPhoto(){return gangsPhoto;}
    public String getGangsPurpose(){return gangsPurpose;}
    public String getGangsType(){return gangsType;}
    public String getGangsCreater(){return gangsCreater;}
    public int getGangsGrade(){return gangsGrade;}
}
