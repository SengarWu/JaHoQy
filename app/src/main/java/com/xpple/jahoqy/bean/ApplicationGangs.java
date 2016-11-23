package com.xpple.jahoqy.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by caolin on 2015/10/4.
 */
public class ApplicationGangs extends BmobObject {
    private String nickName;//用户昵称
    private String gangsName;//帮派名称  外键
    private String applyReason;//申请理由
    private int gangsPosition;
    public void  setpplicationGangs(String nickName,String gangsName,String applyReason,int gangsPosition){
        this.gangsName=gangsName;
        this.applyReason=applyReason;
        this.nickName=nickName;
        this.gangsPosition=gangsPosition;
    }
    public int getGangsPosition(){return gangsPosition;}
    public String getGangsName(){return gangsName;}
    public String getNickName(){return nickName;}
    public String getApplyReason() {return applyReason;}
}
